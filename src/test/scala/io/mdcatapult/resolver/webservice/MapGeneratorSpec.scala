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
  private val exampleXLSXFilePath = "src/test/resources/informatics_projects.xlsx"
  private val xLSXNoFirstColumnFilePath = "src/test/resources/emptyFirstColumn.xlsx"
  private val xLSXNoSecondColumnFilePath = "src/test/resources/emptySecondColumn.xlsx"
  private val exampleOtherFilePath = "src/test/resources/empty.csv"
  private val emptyXLSXFilePath = "src/test/resources/empty.xlsx"

  val totalNumberProjects = 44

  "The mapGenerator" should {
    "generate a map when passed a text file" in {
      val codeMap = MapGenerator.createProjectCodeMapHandler(exampleTextFilePath)
      assert(codeMap.get.toList.length === totalNumberProjects)
    }

    "throw an error if passed an empty text file" in {
      val codeMap = MapGenerator.createProjectCodeMapHandler(emptyTextFilePath)
      assert(codeMap.isFailure)
    }

    "NOT throw an error if passed text file has a row with nothing preceding an equals sign" in {
      val codeMap = MapGenerator.createProjectCodeMapHandler(textFileNoKeyInRowPath)
      assert(codeMap.isSuccess)
    }

    "NOT throw an error if passed text file has a row with nothing following an equals sign" in {
      val codeMap = MapGenerator.createProjectCodeMapHandler(textFileNoValueInRowPath)
      assert(codeMap.isSuccess)
    }

    "throw an error if passed text file has a row with no equals sign (delimiter)" in {
      val codeMap = MapGenerator.createProjectCodeMapHandler(textFileNoEqualsInRowPath)
      assert(codeMap.isFailure)
    }

    "generate a map when passed an xlsx file, omitting first row" in {
      val codeMap = MapGenerator.createProjectCodeMapHandler(exampleXLSXFilePath)
      assert(codeMap.get.toList.length === totalNumberProjects)
    }

    "NOT error if passed xlsx file contains a row with an empty first column" in {
      val codeMap = MapGenerator.createProjectCodeMapHandler(xLSXNoFirstColumnFilePath)
      assert(codeMap.isSuccess)
    }

    "NOT error if passed xlsx file contains a row with an empty second column" in {
      val codeMap = MapGenerator.createProjectCodeMapHandler(xLSXNoSecondColumnFilePath)
      assert(codeMap.isSuccess)
    }

    "throw an error if passed an empty xlsx file" in {
      val codeMap = MapGenerator.createProjectCodeMapHandler(emptyXLSXFilePath)
      assert(codeMap.isFailure)
    }

    "throw an error if passed file does not have correct extension" in {
      val codeMap = MapGenerator.createProjectCodeMapHandler(exampleOtherFilePath)
      assert(codeMap.isFailure)
    }

  }
}
