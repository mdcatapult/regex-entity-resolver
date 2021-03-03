package utils

import java.io.{BufferedWriter, File, FileWriter}
import scala.collection.mutable.ListBuffer
import scala.io.{BufferedSource, Source}
import scala.util.{Failure, Success, Try}

object CSVRegexGenerator {

  def main(args: Array[String]): Unit = {
    val sourceFilePath = "src/main/resources/informatics_projects.csv"
    val outputFilePath = "src/test/resources/regex.txt"

    val outputDelimiter = "="
    val inputDelimiter = ","

    def regexify(projectCode: String): String = s"\\b$projectCode\\b"

    val regexGeneratorProcess = for {
      projectRegexList <- createRegexList(sourceFilePath, inputDelimiter, outputDelimiter, regexify)
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
                              inputDelimiter: String,
                              outputDelimiter: String,
                              regexFn: String => String): Try[List[String]] = {

    val lines = getLinesFromFile(sourceFilepath)

    val regexLineTrys = lines.map(line => {

      Try {
        val parts = line.split(inputDelimiter)
        val (projectCode, projectName) = (parts.head, parts(1))
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

  private def getLinesFromFile(sourceFilepath: String): List[String] = {
    val projectInfo: BufferedSource = Source.fromFile(sourceFilepath)

    val buffer = new ListBuffer[String]
    for (line <- projectInfo.getLines()) {
      buffer += line
    }
    projectInfo.close()
    buffer.tail.result()
  }
}
