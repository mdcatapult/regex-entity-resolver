package io.mdcatapult.resolver.webservice.model

import spray.json._

case class Project(code: String, name: String)

object Project extends DefaultJsonProtocol {
  implicit val fmt: RootJsonFormat[Project] = jsonFormat2(Project.apply)
}
