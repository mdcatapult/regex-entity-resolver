package io.mdcatapult.resolver.webservice.routes

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.Materializer
import com.typesafe.scalalogging.LazyLogging
import io.mdcatapult.resolver.webservice.utils.ProjectCodeResolver
import spray.json._

import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success, Try}

class Routes(projectCodeResolver: ProjectCodeResolver, urlPath: String)(implicit e: ExecutionContextExecutor, m: Materializer) extends LazyLogging {

  val topLevelRoute: Route =
    concat(
      path(urlPath)(bodyRoute),
      pathPrefix(urlPath / Segment)(stringRoute)
    )

  def stringRoute(projectCode: String): Route = get {
    val resolverResult = Try {
      projectCodeResolver.resolve(projectCode).toJson.toString
    }
    complete {
      resolverResult match {
        case Success(result) =>
          logger.info(s" Success: HTTP Response for $projectCode")
          HttpResponse(StatusCodes.OK, entity = HttpEntity(ContentTypes.`application/json`, result))
        case Failure(e) =>
          logger.error("Failed exception", e)
          HttpResponse(StatusCodes.InternalServerError, entity = HttpEntity(ContentTypes.`application/json`, e.getMessage))
      }
    }
  }

  def bodyRoute: Route = {
    (post & entity(as[HttpEntity])) { httpEntity =>
      complete {
        Unmarshal(httpEntity).to[String].map(body => {
          val resolverResult = Try {
            projectCodeResolver.resolve(body).toJson.toString
          }
          resolverResult match {
            case Success(result) =>
              HttpResponse(StatusCodes.OK, entity = HttpEntity(ContentTypes.`application/json`, result))
            case Failure(e) =>
              logger.error("Failed exception", e)
              HttpResponse(StatusCodes.InternalServerError, entity = HttpEntity(ContentTypes.`application/json`, e.getMessage))
          }
        })
      }
    }
  }

}



