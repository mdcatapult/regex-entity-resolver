package io.mdcatapult.resolver.webservice

import io.mdcatapult.resolver.webservice.model.Project
import io.mdcatapult.resolver.webservice.utils.{AppConfig, MapGenerator, ProjectCodeResolver}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

/**
 * Can we find things like "ABC-123" and "DEF-123" in text using custom regex
 * and resolve them
 */
class RegexResolverSpec extends AnyWordSpec with Matchers  {

  private val regexCodesFilePath = "src/test/resources/regex_codes.txt"
  val codeMap = MapGenerator.createProjectCodeMapHandler(regexCodesFilePath).get
  val appConfig: AppConfig = AppConfig(name =  "Test", host =  "localhost", port = 8888, sourceFilePath = "src/test/resources/regex_codes.txt", regex = "", urlPath = "test", resolverMatchFromRegex = true, resolverMatchRegex = "\\b\\s{0,1}.{0,1}\\d{1,20}\\b")
  private val resolver = new ProjectCodeResolver("\\b(ABC|DEF)\\s{0,1}.{0,1}\\d{1,20}\\b".r, codeMap)(appConfig)

  "The regex resolver" can {

    "find a single thing" in {
      val results = resolver.resolve("This contains ABC-123")
      assert(results.length === 1)
      assert(results(0).equals( Project("ABC-123", "\"I am a code\"")))
    }

    "find instances of different things" in {
      val results = resolver.resolve("This contains ABC-123 but also DEF-456")
      assert(results.length === 2)
      assert(results(0).equals(Project("ABC-123", "\"I am a code\"")))
      assert(results(1).equals(Project("DEF-456","\"I am another code\"")))
    }

    "find similar things" in {
      val results = resolver.resolve("This contains ABC-123 but also ABC-456")
      assert(results.length === 2)
      assert(results(0).equals(Project("ABC-123", "\"I am a code\"")))
      assert(results(1).equals(Project("ABC-456","\"I am a code\"")))
    }

    "find a mix of similar things" in {
      val results = resolver.resolve("This contains ABC-123 but also DEF-123 as well as DEF-456 and ABC-456")
      assert(results.length === 4)
      assert(Set(Project("ABC-456","\"I am a code\""), Project("ABC-123","\"I am a code\""), Project("DEF-456","\"I am another code\""), Project("DEF-123","\"I am another code\"")).subsetOf(results.toSet))
    }

    "dedup the same thing" in {
      val results = resolver.resolve("This contains ABC-123 but also ABC-123")
      assert(results.length === 1)
      assert(results(0).equals(Project("ABC-123", "\"I am a code\"")))
    }

  }
}

