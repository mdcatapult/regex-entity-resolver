package io.mdcatapult.resolver.webservice.utils

import com.typesafe.scalalogging.LazyLogging
import org.apache.poi.ss.usermodel.{DataFormatter, Row, Sheet, Workbook, WorkbookFactory}

import java.io.{File, FileInputStream}
import scala.io.Source
import scala.jdk.CollectionConverters.IterableHasAsScala

/**
 * Generate a Map of key value pairs from XSLX or text file
 */
object MapGenerator extends LazyLogging {


  def createProjectCodeMapHandler(filepath: String): Map[String, String] = {
    val fileType = filepath.split("\\.").last
    fileType match {
      case "xlsx" => createProjectCodeMapFromXLSXFile(filepath)
      case "txt" => createProjectCodeMapFromTextFile(filepath)
      case _ =>
        val exception = "Error: source file extension must be either xlsx or txt"
        logger.info(exception)
        throw new Exception(exception)
    }
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

    tab.asScala.collect { row =>
      val code = Option(row.getCell(0, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL))
      val name = Option(row.getCell(1, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL))
      (code, name) match {
        case (Some(code), Some(name)) => formatter.formatCellValue(code) -> formatter.formatCellValue(name)
      }
    }.tail.toMap
  }

}
