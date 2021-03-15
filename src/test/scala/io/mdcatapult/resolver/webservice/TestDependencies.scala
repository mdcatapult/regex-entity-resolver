package io.mdcatapult.resolver.webservice

import io.mdcatapult.resolver.webservice.utils.{ConfigLoader, MapGenerator, ProjectCodeResolver}

import scala.util.matching.Regex

trait TestDependencies {
  val config = ConfigLoader.loadConfig()
  val projectCodeMap: Map[String, String] = MapGenerator.createProjectCodeMapHandler(config.sourceFilePath).get
  val mdcProjectRegex: Regex = config.regex.r
}
