package io.mdcatapult.resolver.webservice

import io.mdcatapult.resolver.webservice.utils.MapGenerator
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec


class MapGeneratorSpec extends AnyWordSpec with Matchers {

  private val exampleTextFilePath = "src/test/resources/codes.txt"
  private val exampleXLSXFilePath = "src/test/resources/informatics_projects.xlsx"

  "The mapGenerator" should {
    "generate a map when passed a text file" in {
      val codeMap = MapGenerator.createProjectCodeMapHandler(exampleTextFilePath)
      assert(codeMap.toList.length === 44)
    }

    "generate a map when passed an xlsx file, omitting first row" in {
      val codeMap = MapGenerator.createProjectCodeMapHandler(exampleXLSXFilePath)
      assert(codeMap.toList.length === 44)
    }


  }
}
