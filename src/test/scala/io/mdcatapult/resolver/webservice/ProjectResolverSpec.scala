package io.mdcatapult.resolver.webservice

import io.mdcatapult.resolver.webservice.utils.ProjectCodeResolver
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec


class ProjectResolverSpec extends AnyWordSpec with Matchers {

  val exampleCode = "Some text. MDCP-0167. Some other stuff."


  "The projectResolver" should {
    "" in {
      ProjectCodeResolver.resolve(exampleCode)
//      assert(true)
    }
  }
}
