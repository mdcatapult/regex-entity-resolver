package io.mdcatapult.resolver.webservice

import akka.actor.ActorSystem
import akka.http.scaladsl.Http

import com.typesafe.scalalogging.LazyLogging
import io.mdcatapult.resolver.webservice.routes.Routes
import io.mdcatapult.resolver.webservice.utils.ConfigLoader

import scala.concurrent.ExecutionContextExecutor


object ProjectResolverWebService extends LazyLogging {

  def main(args: Array[String]): Unit = {

    implicit val system: ActorSystem = ActorSystem()
    implicit val executionContext: ExecutionContextExecutor = system.dispatcher
    val config = ConfigLoader.loadConfig()
    val topLevelRoute = new Routes().topLevelRoute
    val host = config.host
    val port = config.port
    Http().newServerAt(host, port).bind(topLevelRoute)
    logger.info(s"Server listening at: $host:$port")
  }
}
