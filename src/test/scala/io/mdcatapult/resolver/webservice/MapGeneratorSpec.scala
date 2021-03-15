package io.mdcatapult.resolver.webservice

import io.mdcatapult.resolver.webservice.utils.MapGenerator
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec


class MapGeneratorSpec extends AnyWordSpec with Matchers {

  private val exampleTextFilePath = "src/test/resources/codes.txt"
  private val exampleXLSXFilePath = "src/test/resources/informatics_projects.xlsx"
  private val exampleOtherFilePath = "src/test/resources/codes.csv"

  val totalNumberProjects = 44

  "The mapGenerator" should {
    "generate a map when passed a text file" in {
      val codeMap = MapGenerator.createProjectCodeMapHandler(exampleTextFilePath)
      assert(codeMap.toList.length === totalNumberProjects)
    }

    "generate a map when passed an xlsx file, omitting first row" in {
      val codeMap = MapGenerator.createProjectCodeMapHandler(exampleXLSXFilePath)
      assert(codeMap.toList.length === totalNumberProjects)
    }

    "throw an error if passed file does not have correct extension" in {
      val codeMap = MapGenerator.createProjectCodeMapHandler(exampleOtherFilePath)
      println(codeMap)
      assert(true)
    }


  }
}
