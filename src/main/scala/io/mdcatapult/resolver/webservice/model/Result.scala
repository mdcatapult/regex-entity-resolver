package io.mdcatapult.resolver.webservice.model

import spray.json._

case class Result(entity: String, resolvedEntity: String)

object Result extends DefaultJsonProtocol {
  implicit val fmt: RootJsonFormat[Result] = jsonFormat2(Result.apply)
}
