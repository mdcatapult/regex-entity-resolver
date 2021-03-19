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
                            urlPath: String
                          )
final case class WebServiceConfig(appConfig: AppConfig)
