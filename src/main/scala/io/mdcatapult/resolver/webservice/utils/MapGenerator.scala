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


  def createEntityMapHandler(filepath: String): Try[Map[String, String]] = {
    val fileType = filepath.split("\\.").last
    val entityMap: Try[Map[String, String]] = {
      fileType match {
        case "xlsx" => createEntityMapFromXLSXFile(filepath)
        case "txt" => createEntityMapFromTextFile(filepath)
        case _ =>
          Failure(new Exception("Error: source file extension must be either xlsx or txt"))
      }
    }

    if (entityMap.isFailure) {
      logger.error(entityMap.failed.get.getMessage)
    }
    entityMap
  }

  /**
   *
   * @param filepath Path to text file in the format code|=|name
   * @return Map of code|name key value pairs
   */
  private def createEntityMapFromTextFile(filepath: String): Try[Map[String, String]] = {
    val entityMapTry = Try {
      val source = Source.fromFile(filepath)
      val lines = source.getLines().toList
      source.close()

      if (lines.isEmpty) {
        throw new Exception("Error: no lines found in source file - could not create map")
      } else lines
        .filter(line => line.nonEmpty)
        .map(line => {
          val parts = line.split("=", -2)
          val entity = parts(0)
          val name = parts(1)
          if (entity.isEmpty || name.isEmpty) logger.warn(s"Warning: incomplete row in source file containing $entity $name")
          entity -> name
        }).toMap
    }
    entityMapTry
  }

  /**
   *
   * @param filepath Path to xlsx file where the first two columns are projectCode and projectName
   * @return Map with projectCode|projectName key value pairs
   */
  private def createEntityMapFromXLSXFile(filepath: String): Try[Map[String, String]] = {
    val workBookFile: File = new File(filepath)
    val fis: FileInputStream = new FileInputStream(workBookFile)
    val wb: Try[Workbook] = Try {
      WorkbookFactory.create(fis)
    }
    wb match {
      case Success(book) =>
        val tab: Sheet = book.getSheetAt(0)
        book.close()
        val formatter = new DataFormatter()
        Try {
          val rows = tab.asScala
            .view
            .map(row => {
              (Option(row.getCell(0, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL)),
                Option(row.getCell(1, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL)))
            })
          if (rows.toList.isEmpty) throw new Exception("Error: no lines found in source file - could not create map")
          rows.filter(rowOption => {
            // log warning only if a single value is present in a row - not if row is empty
            if (rowOption._1.isEmpty && rowOption._2.isDefined || rowOption._2.isEmpty && rowOption._1.isDefined)
              logger.warn(s"Warning: incomplete row in source file containing ${rowOption._1.getOrElse(rowOption._2.getOrElse())}")
            rowOption._1.isDefined && rowOption._2.isDefined
          })
            .map(definedRows => {
              (formatter.formatCellValue(definedRows._1.get), formatter.formatCellValue(definedRows._2.get))
            })
            .tail
            .toMap
        }
      case Failure(exception) => Failure(exception)
    }
  }

}
