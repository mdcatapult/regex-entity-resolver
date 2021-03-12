package io.mdcatapult.resolver.webservice

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.util.ByteString
import io.mdcatapult.resolver.webservice.model.Project
import io.mdcatapult.resolver.webservice.routes.Routes
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec


class ProjectResolverWebServiceSpec extends AnyWordSpec
  with Matchers
  with ScalatestRouteTest
  with SprayJsonSupport {

  private val routes = new Routes()
  private val projectsUri = "/projects"
  private val exampleCode = "MDCP-0167"
  private val notACode = "text"


  "The web service" should {
    "return the expected project wrapper for a GET request with a valid MDC project code as a path parameter" in {
      Get(s"$projectsUri/$exampleCode") ~> routes.topLevelRoute ~> check {
        val result = responseAs[List[Project]]
        assert(result.length === 1)
        assert(result(0).name === "Persephone")
      }
    }

    "return an empty json array for a GET request where no match is found" in {
      Get(s"$projectsUri/$notACode") ~> routes.topLevelRoute ~> check {
        val result = responseAs[List[Project]]
        assert(result.length === 0)
      }
    }

    "return the expected project wrapper for a POST request with body: MDCP-0167" in {
      Post(projectsUri, ByteString(exampleCode)) ~> routes.topLevelRoute ~> check {
        val result = responseAs[List[Project]]
        assert(result.head.name === "Persephone")
      }
    }

    "return an empty json array for a POST request where no match is found" in {
      Post(projectsUri, ByteString(notACode)) ~> routes.topLevelRoute ~> check {
        val result = responseAs[List[Project]]
        assert(result.length === 0)
      }
    }

  }
}
