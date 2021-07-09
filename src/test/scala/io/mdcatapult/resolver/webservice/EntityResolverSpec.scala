package io.mdcatapult.resolver.webservice

import io.mdcatapult.resolver.webservice.utils.EntityResolver
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec


class EntityResolverSpec extends AnyWordSpec with Matchers with TestDependencies {

  private val exampleCode = "Some text. MDCP-0167. Some other stuff."
  private val exampleMultipleCodes = "Some text. MDCP-0167. Some other stuff. MDCP-0159."
  private val exampleInexistentCode = "Some text. MDCP-9999. Some other stuff."
  private val exampleInvalidCode = "Some text. MDCP-01678. Some other stuff."

  private val resolver = new EntityResolver(mdcProjectRegex, projectCodeMap)(config)

  "The projectResolver" should {
    "resolve a single project code" in {
      assert(resolver.resolve(exampleCode).length === 1)
    }

    "resolve multiple project codes" in {
      assert(resolver.resolve(exampleMultipleCodes).length === 2)
    }

    "not resolve a project code in the correct format but for which there is no MDC project" in {
      assert(resolver.resolve(exampleInexistentCode).length === 0)
    }

    "not resolve a code which is not in the correct format" in {
      assert(resolver.resolve(exampleInvalidCode).length === 0)
    }

  }
}
