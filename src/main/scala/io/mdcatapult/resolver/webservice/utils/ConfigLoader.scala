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

package io.mdcatapult.resolver.webservice.utils

import scala.util.{Failure, Success, Try}
import pureconfig._
import pureconfig.generic.auto._

object ConfigLoader {

  def loadConfig(): AppConfig = {
    Try(ConfigSource.defaultApplication.loadOrThrow[WebServiceConfig]) match {
      case Failure(e) =>
        val exceptionMessage = s"Error loading WebServiceConfig: ${e.getMessage}"
        println(exceptionMessage)
        throw new Exception(exceptionMessage)
      case Success(config) => config.appConfig
    }
  }
}

final case class AppConfig(
                            name: String,
                            host: String,
                            port: Int,
                            sourceFilePath: String,
                            regex: String,
                            urlPath: String,
                            resolverMatchFromRegex: Boolean,
                            resolverMatchRegex: String
                          )
final case class WebServiceConfig(appConfig: AppConfig)
