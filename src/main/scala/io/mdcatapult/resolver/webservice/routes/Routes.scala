package io.mdcatapult.resolver.webservice.routes

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.model.Uri.Path.Segment
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import com.typesafe.scalalogging.LazyLogging
import io.mdcatapult.resolver.webservice.utils.ProjectCodeResolver
import spray.json.enrichAny

import scala.concurrent.ExecutionContextExecutor

class Routes(implicit e: ExecutionContextExecutor, m: Materializer) extends LazyLogging {

  val topLevelRoute: Route =
    concat(
      path("project" / Segment)(stringRoute)
    )

  def stringRoute(projectCode: String): Route = get {
      val resolverResult = ProjectCodeResolver.resolve(projectCode).toJson.toString
      logger.info(s" Success: HTTP Response for $projectCode")
      complete(HttpResponse(StatusCodes.OK, entity = HttpEntity(ContentTypes.`application/json`, resolverResult)))
  }

}
