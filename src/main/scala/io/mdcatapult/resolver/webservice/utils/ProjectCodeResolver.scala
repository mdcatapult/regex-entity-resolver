package io.mdcatapult.resolver.webservice.utils

import io.mdcatapult.resolver.webservice.model.Project

import scala.io.Source


object ProjectCodeResolver {

  val projectCodeMap: Map[String, String] = createProjectCodeMap("src/test/resources/codes.txt")

  private def createProjectCodeMap(filepath: String): Map[String, String] = {

    val source = Source.fromFile(filepath)
    val lines = source.getLines.toList
    source.close()

    lines.map(line => {
      val parts = line.split("=")
      val code = parts(0)
      val name = parts(1)
      name -> code
    }).toMap
  }

  val mdcProjectRegex = "\\bMDCP-\\d{4}\\b".r

  def resolve(text: String): Unit = {

    val matches: List[String] = mdcProjectRegex.findAllIn(text).toList
    val codes: List[(String, Option[String])] = matches.map(singleMatch => {
      singleMatch -> projectCodeMap.get(singleMatch)
    })

    codes.collect {
      case (code, Some(name)) => Project(code, name)
    }
  }


}
