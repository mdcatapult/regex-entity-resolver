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

import io.mdcatapult.resolver.webservice.utils.{ConfigLoader, MapGenerator, EntityResolver}

import scala.util.matching.Regex

trait TestDependencies {
  val config = ConfigLoader.loadConfig()
  val projectCodeMap: Map[String, String] = MapGenerator.createEntityMapHandler(config.sourceFilePath).get
  val mdcProjectRegex: Regex = config.regex.r
}
