package io.mdcatapult.resolver.webservice

import io.mdcatapult.resolver.webservice.model.Result
import io.mdcatapult.resolver.webservice.utils.{AppConfig, MapGenerator, EntityResolver}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

/**
 * Can we find things like "ABC-123" and "DEF-123" in text using custom regex
 * and resolve them
 */
class EntityResolverWithRegexSpec extends AnyWordSpec with Matchers  {

  private val regexCodesFilePath = "src/test/resources/regex_codes.txt"
  val codeMap = MapGenerator.createEntityMapHandler(regexCodesFilePath).get
  val appConfig: AppConfig = AppConfig(name =  "Test", host =  "localhost", port = 8888, sourceFilePath = "src/test/resources/regex_codes.txt", regex = "", urlPath = "test", resolverMatchFromRegex = true, resolverMatchRegex = "\\b\\s{0,1}.{0,1}\\d{1,20}\\b")
  private val resolver = new EntityResolver("\\b(ABC|DEF)\\s{0,1}.{0,1}\\d{1,20}\\b".r, codeMap)(appConfig)

  "The regex resolver" can {

    "find a single thing" in {
      val results = resolver.resolve("This contains ABC-123")
      assert(results.length === 1)
      assert(results(0).equals( Result("ABC-123", "\"I am a code\"")))
    }

    "find instances of different things" in {
      val results = resolver.resolve("This contains ABC-123 but also DEF-456")
      assert(results.length === 2)
      assert(results(0).equals(Result("ABC-123", "\"I am a code\"")))
      assert(results(1).equals(Result("DEF-456","\"I am another code\"")))
    }

    "find similar things" in {
      val results = resolver.resolve("This contains ABC-123 but also ABC-456")
      assert(results.length === 2)
      assert(results(0).equals(Result("ABC-123", "\"I am a code\"")))
      assert(results(1).equals(Result("ABC-456","\"I am a code\"")))
    }

    "find a mix of similar things" in {
      val results = resolver.resolve("This contains ABC-123 but also DEF-123 as well as DEF-456 and ABC-456")
      assert(results.length === 4)
      assert(Set(Result("ABC-456","\"I am a code\""), Result("ABC-123","\"I am a code\""), Result("DEF-456","\"I am another code\""), Result("DEF-123","\"I am another code\"")).subsetOf(results.toSet))
    }

    "dedup the same thing" in {
      val results = resolver.resolve("This contains ABC-123 but also ABC-123")
      assert(results.length === 1)
      assert(results(0).equals(Result("ABC-123", "\"I am a code\"")))
    }

  }
}

