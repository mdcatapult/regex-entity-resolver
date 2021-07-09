package io.mdcatapult.resolver.webservice

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import com.typesafe.scalalogging.LazyLogging
import io.mdcatapult.resolver.webservice.routes.Routes
import io.mdcatapult.resolver.webservice.utils.{ConfigLoader, MapGenerator, EntityResolver}

import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success, Try}
import scala.util.matching.Regex


object EntityResolverWebService extends LazyLogging {

  def main(args: Array[String]): Unit = {

    implicit val system: ActorSystem = ActorSystem()
    implicit val executionContext: ExecutionContextExecutor = system.dispatcher
    val config = ConfigLoader.loadConfig()
    val entityMap: Try[Map[String, String]] = MapGenerator.createEntityMapHandler(config.sourceFilePath)
    val entityRegex: Regex = config.regex.r
    entityMap match {
      case Failure(exception) =>
        logger.error(exception.getMessage)
        throw exception
      case Success(entityMap) =>
        val resolver = new EntityResolver(entityRegex, entityMap)(config)
        val topLevelRoute = new Routes(resolver, config.urlPath).topLevelRoute
        val host = config.host
        val port = config.port
        Http().newServerAt(host, port).bind(topLevelRoute)
        logger.info(s"Server listening at: $host:$port")
    }
  }
}
