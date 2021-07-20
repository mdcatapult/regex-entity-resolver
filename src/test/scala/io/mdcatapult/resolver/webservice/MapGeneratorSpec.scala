package io.mdcatapult.resolver.webservice

import io.mdcatapult.resolver.webservice.utils.MapGenerator
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec


class MapGeneratorSpec extends AnyWordSpec with Matchers {

  private val exampleTextFilePath = "src/test/resources/codes.txt"
  private val emptyTextFilePath = "src/test/resources/empty.txt"
  private val textFileNoKeyInRowPath = "src/test/resources/missingKey.txt"
  private val textFileNoEqualsInRowPath = "src/test/resources/missingEquals.txt"
  private val textFileNoValueInRowPath = "src/test/resources/missingValue.txt"
  private val textFileMissingRowPath = "src/test/resources/missingRow.txt"
  private val xlsxFileMissingRowPath = "src/test/resources/missingRow.xlsx"
  private val exampleXLSXFilePath = "src/test/resources/informatics_projects.xlsx"
  private val xLSXNoFirstColumnFilePath = "src/test/resources/emptyFirstColumn.xlsx"
  private val xLSXNoSecondColumnFilePath = "src/test/resources/emptySecondColumn.xlsx"
  private val exampleOtherFilePath = "src/test/resources/empty.csv"
  private val emptyXLSXFilePath = "src/test/resources/empty.xlsx"

  val totalNumberProjects = 44
  val totalProjectsInMissingRowFile = 3

  "The mapGenerator" should {
    "generate a map when passed a text file" in {
      val codeMap = MapGenerator.createEntityMapHandler(exampleTextFilePath)
      assert(codeMap.get.toList.length === totalNumberProjects)
      assert(codeMap.get.toList.contains("MDCP-0199", "Aphrodite"))
    }

    "throw an error if passed an empty text file" in {
      val codeMap = MapGenerator.createEntityMapHandler(emptyTextFilePath)
      assert(codeMap.isFailure)
    }

    "NOT throw an error if passed text file has a row with nothing preceding an equals sign" in {
      val codeMap = MapGenerator.createEntityMapHandler(textFileNoKeyInRowPath)
      assert(codeMap.isSuccess)
    }

    "NOT throw an error if passed text file has a row with nothing following an equals sign" in {
      val codeMap = MapGenerator.createEntityMapHandler(textFileNoValueInRowPath)
      assert(codeMap.isSuccess)
    }

    "omit empty lines in a text file" in {
      val codeMap = MapGenerator.createEntityMapHandler(textFileMissingRowPath)
      assert(codeMap.get.toList.length === totalProjectsInMissingRowFile)
    }

    "throw an error if passed text file has a row with no equals sign (delimiter)" in {
      val codeMap = MapGenerator.createEntityMapHandler(textFileNoEqualsInRowPath)
      assert(codeMap.isFailure)
    }

    "generate a map when passed an xlsx file, omitting first row" in {
      val codeMap = MapGenerator.createEntityMapHandler(exampleXLSXFilePath)
      assert(codeMap.get.toList.length === totalNumberProjects)
      assert(codeMap.get.toList.contains("MDCP-0199", "Aphrodite"))
    }

    "NOT error if passed xlsx file contains a row with an empty first column" in {
      val codeMap = MapGenerator.createEntityMapHandler(xLSXNoFirstColumnFilePath)
      assert(codeMap.isSuccess)
      assert(codeMap.get.toList.contains("MDCP-0142", "Artemis"))
    }

    "NOT error if passed xlsx file contains a row with an empty second column" in {
      val codeMap = MapGenerator.createEntityMapHandler(xLSXNoSecondColumnFilePath)
      assert(codeMap.isSuccess)
      assert(codeMap.get.toList.contains("MDCP-0159", "Charon"))
    }

    "throw an error if passed an empty xlsx file" in {
      val codeMap = MapGenerator.createEntityMapHandler(emptyXLSXFilePath)
      assert(codeMap.isFailure)
    }

    "omit empty rows in an xlsx file" in {
      val codeMap = MapGenerator.createEntityMapHandler(xlsxFileMissingRowPath)
      assert(codeMap.get.toList.length === totalProjectsInMissingRowFile)
      assert(codeMap.get.toList.contains("MDCP-0199", "Aphrodite"))
    }

    "throw an error if passed file does not have correct extension" in {
      val codeMap = MapGenerator.createEntityMapHandler(exampleOtherFilePath)
      assert(codeMap.isFailure)
    }

  }
}
