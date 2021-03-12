package io.mdcatapult.resolver.webservice

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.testkit.ScalatestRouteTest
import io.mdcatapult.resolver.webservice.model.Project
import io.mdcatapult.resolver.webservice.routes.Routes
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec


class ProjectResolverWebServiceSpec extends AnyWordSpec
  with Matchers
  with ScalatestRouteTest
  with SprayJsonSupport {

  private val routes = new Routes()
  private val projectUri = "/project"
  private val exampleCode = "MDCP-0167"


  "The web service" should {
    "return the expected project wrapper for a GET request with a valid MDC project code as a path parameter" in {
      Get(s"$projectUri/$exampleCode") ~> routes.topLevelRoute ~> check {
        val result = responseAs[List[Project]]
        assert(result.length === 1)
        assert(result(0).name === "Persephone")
      }
    }


  }
}
