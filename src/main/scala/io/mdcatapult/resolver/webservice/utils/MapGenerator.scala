package io.mdcatapult.resolver.webservice.utils

import com.typesafe.scalalogging.LazyLogging
import org.apache.poi.ss.usermodel.{DataFormatter, Row, Sheet, Workbook, WorkbookFactory}

import java.io.{File, FileInputStream}
import scala.io.Source
import scala.jdk.CollectionConverters.IterableHasAsScala
import scala.util.{Failure, Success, Try}

/**
 * Generate a Map of key value pairs from XSLX or text file
 */
object MapGenerator extends LazyLogging {


  def createProjectCodeMapHandler(filepath: String): Try[Map[String, String]] = {
    val fileType = filepath.split("\\.").last
    val projectCodeMapTry: Try[Map[String, String]] =
      fileType match {
        case "xlsx" => Success(createProjectCodeMapFromXLSXFile(filepath))
        case "txt" => Success(createProjectCodeMapFromTextFile(filepath))
        case _ =>
          Failure(new Exception("Error: source file extension must be either xlsx or txt"))
      }
    projectCodeMapTry.flatMap(projectCodeMap => {
      if (projectCodeMap.nonEmpty) Success(projectCodeMap)
      else Failure(new Exception("Error: file has no valid rows"))
    })
  }

  /**
   *
   * @param filepath Path to text file in the format code|=|name
   * @return Map of code|name key value pairs
   */
  def createProjectCodeMapFromTextFile(filepath: String): Map[String, String] = {

    val source = Source.fromFile(filepath)
    val lines = source.getLines().toList
    source.close()

    lines.map(line => {
      val parts = line.split("=")
      val code = parts(0)
      val name = parts(1)
      code -> name
    }).toMap
  }

  /**
   *
   * @param filepath Path to xlsx file where the first two columns are projectCode and projectName
   * @return Map with projectCode|projectName key value pairs
   */
  def createProjectCodeMapFromXLSXFile(filepath: String): Map[String, String] = {
    val workBookFile: File = new File(filepath)
    val fis: FileInputStream = new FileInputStream(workBookFile)
    val wb: Workbook = WorkbookFactory.create(fis)
    val tab: Sheet = wb.getSheetAt(0)
    wb.close()
    val formatter = new DataFormatter()

    tab.asScala
      .view
      .map(row => {
        (Option(row.getCell(0, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL)),
          Option(row.getCell(1, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL)))
      })
      .filter(rowOption => {
        rowOption._1.isDefined && rowOption._2.isDefined
      })
      .map(definedRows => {
        (formatter.formatCellValue(definedRows._1.get), formatter.formatCellValue(definedRows._2.get))
      })
      .tail
      .toMap
  }

}
