package io.mdcatapult.resolver.webservice.utils

import io.mdcatapult.resolver.webservice.model.Result
import scala.util.matching.Regex


class EntityResolver(entityRegex: Regex, entityMap: Map[String, String])(config: AppConfig) {

  /**
   * Given a string, identifies entities and resolves them to a "thing"
   *
   * @param text String in which entities are to be identified
   * @return List of resolved entities with keys of 'code' and 'name'
   */
  def resolve(text: String): List[Result] = {
    // find entities in the text using a regex
    val matchedEntities: List[String] = entityRegex.findAllIn(text).toList
    // lookup names from codes
    val resolvedEntities: List[(String, Option[String])] = matchedEntities.map(singleMatch => {
      // Are we using the regex to resolve the entity
      if (config.resolverMatchFromRegex) {
        val actualMatch = config.resolverMatchRegex.r.findAllIn(singleMatch).toList.head
        val textWithoutMatch = singleMatch.replace(actualMatch, "")
        singleMatch -> entityMap.get(textWithoutMatch)
      } else {
        singleMatch -> entityMap.get(singleMatch)
      }
    })

    // ensure we only return codes which resolve to a project
    resolvedEntities.collect {
      case (entity, Some(resolvedValue)) => Result(entity, resolvedValue)
    }.distinct
  }

}
