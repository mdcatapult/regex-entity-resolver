/*
 * Copyright 2024 Medicines Discovery Catapult
 * Licensed under the Apache License, Version 2.0 (the "Licence");
 * you may not use this file except in compliance with the Licence.
 * You may obtain a copy of the Licence at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package io.mdcatapult.resolver.webservice

import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.http.scaladsl.Http
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
