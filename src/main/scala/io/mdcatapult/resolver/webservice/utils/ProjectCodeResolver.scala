package io.mdcatapult.resolver.webservice.utils

import io.mdcatapult.resolver.webservice.model.Project
import scala.util.matching.Regex


class ProjectCodeResolver(mdcProjectRegex: Regex, projectCodeMap: Map[String, String])(config: AppConfig) {

  /**
   * Given a string, identifies MDC project codes and resolves them to project name
   *
   * @param text String in which codes are to be identified
   * @return List of maps with keys of 'code' and 'name'
   */
  def resolve(text: String): List[Project] = {
    // find codes in the correct format
    val matches: List[String] = mdcProjectRegex.findAllIn(text).toList
    // lookup names from codes
    val codes: List[(String, Option[String])] = matches.map(singleMatch => {
      // Are we using the regex to resolve the entity
      if (config.resolverMatchFromRegex) {
        val actualMatch = config.resolverMatchRegex.r.findAllIn(singleMatch).toList.head
        val textWithoutMatch = singleMatch.replace(actualMatch, "")
        singleMatch -> projectCodeMap.get(textWithoutMatch)
      } else {
        singleMatch -> projectCodeMap.get(singleMatch)
      }
    })

    // ensure we only return codes which resolve to a project
    codes.collect {
      case (code, Some(name)) => Project(code, name)
    }.distinct
  }

}
