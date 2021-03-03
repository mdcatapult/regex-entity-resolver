package utils

import java.io.{BufferedWriter, FileWriter}
import scala.collection.mutable.ListBuffer
import scala.util.{Failure, Success, Try}
import org.apache.poi.ss.usermodel.{ DataFormatter, WorkbookFactory, Row }
import java.io.File
import collection.JavaConversions._


object XLSXRegexGenerator {

  def main(args: Array[String]): Unit = {
    val sourceFilePath = "src/main/resources/informatics_projects.xlsx"
    val outputFilePath = "src/test/resources/regex.txt"

    val outputDelimiter = "="

    def regexify(projectCode: String): String = s"\\b$projectCode\\b"

    val regexGeneratorProcess = for {
      projectRegexList <- createRegexList(sourceFilePath, outputDelimiter, regexify)
      _ = writeFile(projectRegexList, outputFilePath)
    } yield Unit

    regexGeneratorProcess match {
      case Success(_) => println("MDC project codes file created successfully")
      case Failure(exception) =>
        System.err.println("Error creating project codes regex file")
        throw exception
    }
  }

  def writeFile(regexList: List[String], outputFilePath: String): Try[Unit] = {
    Try {
      val file = new File(outputFilePath)
      val bufferedWriter = new BufferedWriter(new FileWriter(file))
      val regexListAsString = regexList.mkString("\n")
      bufferedWriter.write(regexListAsString)
      bufferedWriter.close()
    }
  }

  private def createRegexList(sourceFilepath: String,
                              outputDelimiter: String,
                              regexFn: String => String): Try[List[String]] = {

    val lines = getLinesFromFile(sourceFilepath)

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

  private def getLinesFromFile(sourceFilepath: String): List[(String, String)] = {

    val buffer = new ListBuffer[(String, String)]
    val sheet = WorkbookFactory
      .create(new File(sourceFilepath))
      .getSheetAt(0)
    val formatter = new DataFormatter()
    for (row <- sheet) {
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
