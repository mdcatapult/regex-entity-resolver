package io.mdcatapult.resolver.webservice.utils

import org.apache.poi.ss.usermodel.{DataFormatter, Row, WorkbookFactory}

import java.io.File
import scala.io.Source
import scala.jdk.CollectionConverters.IterableHasAsScala

/**
 * Generate a Map of key value pairs from XSLX or text file
 */
object MapGenerator {


  /**
   *
   * @param filepath Path to text file in the format code|=|name
   * @return Map of code|name key value pairs
   */
  def createProjectCodeMapFromTextFile(filepath: String): Map[String, String] = {

    val source = Source.fromFile(filepath)
    val lines = source.getLines.toList
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
    val sheet = WorkbookFactory.create(new File(filepath))
    val tab = sheet.getSheetAt(0)
    sheet.close()
    val formatter = new DataFormatter()

    tab.asScala.collect { row =>
      val code = Option(row.getCell(0, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL))
      val name = Option(row.getCell(1, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL))
      (code, name) match {
        case (Some(code), Some(name)) => formatter.formatCellValue(code) -> formatter.formatCellValue(name)
      }
    }.toMap
  }

}
