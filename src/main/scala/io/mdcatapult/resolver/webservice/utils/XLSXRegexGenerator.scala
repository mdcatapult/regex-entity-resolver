package io.mdcatapult.resolver.webservice.utils

import org.apache.poi.ss.usermodel.{DataFormatter, Row, WorkbookFactory}
import scala.jdk.CollectionConverters._
import java.io.{BufferedWriter, File, FileWriter}
import scala.collection.mutable.ListBuffer
import scala.util.{Failure, Success, Try}

/**
 * Given an xlsx spreadsheet where the first two columns represent projectCode and projectName,
 * wraps the project code in a regex and outputs a file in the format regexPattern|delimiter|projectName
 * for use as rule definition file by SparkNLP RegexMatcher
 */
object XLSXRegexGenerator {

  def main(args: Array[String]): Unit = {
    val sourceFilePath = "src/main/resources/informatics_projects.xlsx"
    val outputFilePath = "src/main/resources/regex.txt"

    val outputDelimiter = "="

    def regexify(projectCode: String): String = s"\\b$projectCode\\b"

    val regexGeneratorProcess = for {
      projectRegexList <- createRegexList(sourceFilePath, outputDelimiter, regexify)
      _ = writeFile(projectRegexList, outputFilePath)
    } yield()

    regexGeneratorProcess match {
      case Success(_) => println("MDC project codes file created successfully")
      case Failure(exception) =>
        System.err.println("Error creating project codes regex file")
        throw exception
    }
  }

  /**
   * @param regexList List of strings in the form regexPattern<"delimiter">projectName
   * @param outputFilePath   Path of the written file
   * @return Try indicating whether or not creation of the regex file was successful
   */
  def writeFile(regexList: List[String], outputFilePath: String): Try[Unit] = {
    Try {
      val file = new File(outputFilePath)
      val bufferedWriter = new BufferedWriter(new FileWriter(file))
      val regexListAsString = regexList.mkString("\n")
      bufferedWriter.write(regexListAsString)
      bufferedWriter.close()
    }
  }

  /**
   * @param sourceFilepath  Location of source xlsx spreadsheet
   * @param outputDelimiter Defines the delimiter used in the SparkNLP RegexMatcher (set in .setRules method)
   * @param regexFn         Function that wraps a projectCode in a regex
   * @return List of lines in the format regexPattern|delimiter|projectName
   */
  private def createRegexList(sourceFilepath: String,
                              outputDelimiter: String,
                              regexFn: String => String): Try[List[String]] = {

    val lines = getTupleList(sourceFilepath)

    val regexLineTrys = lines.map(line => {
      Try {
        val (projectCode, projectName) = (line._1, line._2)
        val projectRegex = regexFn(projectCode)

        s"$projectRegex$outputDelimiter$projectName"
      }
    })

    val failures = regexLineTrys collect { case Failure(e) => e }

    if (failures.isEmpty) Success(regexLineTrys.map(_.get))
    else {
      val failureMessages = failures collect {
        case e: ArrayIndexOutOfBoundsException =>
          System.err.println("What have you done\n")
          e.getMessage
        case e: Exception => e.getMessage
      }
      Failure(new Exception(s"Error creating project code regex: ${failureMessages.mkString("\n")}"))
    }
  }

  /**
   *
   * @param sourceFilepath Path of xlsx spreadsheet
   * @return List of tuples containing the contents of the first two cells of each row of the spreadsheet at sourceFilePath
   */
  private def getTupleList(sourceFilepath: String): List[(String, String)] = {

    val buffer = new ListBuffer[(String, String)]
    val sheet = WorkbookFactory
      .create(new File(sourceFilepath))
      .getSheetAt(0)
    val formatter = new DataFormatter()
    for (row <- sheet.asScala) {
      val projectCodeAndName =
        for {
          a <- Option(row.getCell(0, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL))
          b <- Option(row.getCell(1, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL))
        } yield {
          (formatter.formatCellValue(a), formatter.formatCellValue(b))
        }
      projectCodeAndName.foreach(item => buffer += item)
    }
    buffer.tail.result()
  }
}
