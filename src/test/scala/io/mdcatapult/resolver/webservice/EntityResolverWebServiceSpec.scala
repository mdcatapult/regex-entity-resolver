package io.mdcatapult.resolver.webservice

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.util.ByteString
import io.mdcatapult.resolver.webservice.model.Result
import io.mdcatapult.resolver.webservice.routes.Routes
import io.mdcatapult.resolver.webservice.utils.EntityResolver
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec


class EntityResolverWebServiceSpec extends AnyWordSpec
  with Matchers
  with ScalatestRouteTest
  with SprayJsonSupport
  with TestDependencies {

  private val resolver = new EntityResolver(mdcProjectRegex, projectCodeMap)(config)

  private val projectsUri = "projects"
  private val routes = new Routes(resolver, projectsUri)
  private val exampleCode = "MDCP-0167"
  private val notACode = "text"


  "The web service" should {
    "return the expected project wrapper for a GET request with a valid MDC project code as a path parameter" in {
      Get(s"/$projectsUri/$exampleCode") ~> routes.topLevelRoute ~> check {
        val result = responseAs[List[Result]]
        assert(result.length === 1)
        assert(result.head.resolvedEntity === "Persephone")
      }
    }

    "return an empty json array for a GET request where no match is found" in {
      Get(s"/$projectsUri/$notACode") ~> routes.topLevelRoute ~> check {
        val result = responseAs[List[Result]]
        assert(result.length === 0)
      }
    }

    "return the expected project wrapper for a POST request with body: MDCP-0167" in {
      Post(s"/${projectsUri}", ByteString(exampleCode)) ~> routes.topLevelRoute ~> check {
        val result = responseAs[List[Result]]
        assert(result.head.resolvedEntity === "Persephone")
      }
    }

    "return an empty json array for a POST request where no match is found" in {
      Post(s"/${projectsUri}", ByteString(notACode)) ~> routes.topLevelRoute ~> check {
        val result = responseAs[List[Result]]
        assert(result.length === 0)
      }
    }

  }
}
