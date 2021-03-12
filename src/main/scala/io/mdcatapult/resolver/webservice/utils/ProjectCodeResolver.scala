package io.mdcatapult.resolver.webservice.utils

import io.mdcatapult.resolver.webservice.model.Project
import org.apache.poi.ss.usermodel.{DataFormatter, Row, WorkbookFactory}

import java.io.File
import scala.io.Source
import scala.jdk.CollectionConverters.IterableHasAsScala
import scala.util.matching.Regex


object ProjectCodeResolver {

  //  val projectCodeMap: Map[String, String] = createProjectCodeMapFromTextFile("src/test/resources/codes.txt")
  val projectCodeMap: Map[String, String] = createProjectCodeMapFromXLSXFile("src/main/resources/informatics_projects.xlsx")


  /**
   *
   * @param filepath Path to text file in the format projectCode|=|projectName
   * @return Map with projectCode|projectName key value pairs
   */
  private def createProjectCodeMapFromTextFile(filepath: String): Map[String, String] = {

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
  private def createProjectCodeMapFromXLSXFile(filepath: String): Map[String, String] = {
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

  val mdcProjectRegex: Regex = "\\bMDCP-\\d{4}\\b".r

  /**
   * Given a string, identifies MDC project codes and resolves them to project name
   *
   * @param text String in which codes are to be identified
   * @return List of maps with keys of 'code' and 'name'
   */
  def resolve(text: String): List[Project] = {

    // find codes in the correct format
    val matches: List[String] = mdcProjectRegex.findAllIn(text).toList
    // lookup names from codes
    val codes: List[(String, Option[String])] = matches.map(singleMatch => {
      singleMatch -> projectCodeMap.get(singleMatch)
    })

    // ensure we only return codes which resolve to a project
    codes.collect {
      case (code, Some(name)) => Project(code, name)
    }
  }

}
