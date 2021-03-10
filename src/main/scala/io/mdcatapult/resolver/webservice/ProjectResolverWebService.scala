package io.mdcatapult.resolver.webservice

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import scala.concurrent.ExecutionContextExecutor
import com.typesafe.scalalogging.LazyLogging
import io.mdcatapult.resolver.webservice.utils.ConfigLoader

import scala.concurrent.ExecutionContextExecutor


object ProjectResolverWebService extends LazyLogging {

  def main(args: Array[String]): Unit = {

    implicit val system: ActorSystem = ActorSystem()
    implicit val executionContext: ExecutionContextExecutor = system.dispatcher
    val config = ConfigLoader.loadConfig()
    val host = config.host
    val port = config.port
    Http().newServerAt(host, port)
    logger.info(s"Server listening at: $host:$port")
  }
}
