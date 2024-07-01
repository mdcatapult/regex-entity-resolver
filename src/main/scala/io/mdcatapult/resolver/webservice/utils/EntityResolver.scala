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
