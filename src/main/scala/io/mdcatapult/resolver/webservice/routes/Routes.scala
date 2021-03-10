package io.mdcatapult.resolver.webservice.routes

//import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
//import akka.http.scaladsl.model.Uri.Path.Segment
//import akka.http.scaladsl.server.Directives._
//import akka.http.scaladsl.server.Route
//import akka.http.scaladsl.unmarshalling.Unmarshal
//import com.typesafe.scalalogging.LazyLogging
//
//
//import scala.concurrent.ExecutionContextExecutor
//
//class Routes(implicit e: ExecutionContextExecutor,) extends LazyLogging {
//
//  val topLevelRoute: Route =
//    concat(
//      path("projects")(bodyRoute),
//      pathPrefix("project" / Segment)(stringRoute)
//    )
//
//  def bodyRoute: Route = {
//    (post & entity(as[HttpEntity])) { httpEntity =>
//      complete {
//        Unmarshal(httpEntity).to[String].map(body => {
//          val httpResponse = HttpResponse(StatusCodes.OK, entity = HttpEntity(ContentTypes.`application/json`, body))
//          complete(httpResponse)
//        })
//      }
//    }
//  }
//
//  def stringRoute(project: String): Route =
//    get {
//      val httpResponse = HttpResponse(StatusCodes.OK, entity = HttpEntity(ContentTypes.`application/json`, project))
//      logger.info(s" Success: HTTP Response for $project")
//      complete(httpResponse)
//    }
//
//    private def createHttpResponse(source: String): HttpResponse = {
//
//    }
//
//
//}
