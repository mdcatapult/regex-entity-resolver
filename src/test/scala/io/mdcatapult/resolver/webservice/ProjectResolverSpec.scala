package io.mdcatapult.resolver.webservice

import io.mdcatapult.resolver.webservice.utils.ProjectCodeResolver
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec


class ProjectResolverSpec extends AnyWordSpec with Matchers {

  private val exampleCode = "Some text. MDCP-0167. Some other stuff."
  private val exampleMultipleCodes = "Some text. MDCP-0167. Some other stuff. MDCP-0159."
  private val exampleInexistentCode = "Some text. MDCP-9999. Some other stuff."
  private val exampleInvalidCode = "Some text. MDCP-01678. Some other stuff."

  "The projectResolver" should {
    "resolve a single project code" in {
      assert(ProjectCodeResolver.resolve(exampleCode).length === 1)
    }

    "resolve multiple project codes" in {
      assert(ProjectCodeResolver.resolve(exampleMultipleCodes).length === 2)
    }

    "not resolve a project code in the correct format but for which there is no MDC project" in {
      assert(ProjectCodeResolver.resolve(exampleInexistentCode).length === 0)
    }

    "not resolve a code which is not in the correct format" in {
      assert(ProjectCodeResolver.resolve(exampleInvalidCode).length === 0)
    }

  }
}
