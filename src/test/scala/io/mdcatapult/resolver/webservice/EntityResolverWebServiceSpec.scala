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

import org.apache.pekko.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import org.apache.pekko.http.scaladsl.testkit.ScalatestRouteTest
import org.apache.pekko.util.ByteString
import io.mdcatapult.resolver.webservice.model.Result
import io.mdcatapult.resolver.webservice.routes.Routes
import io.mdcatapult.resolver.webservice.utils.EntityResolver
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec


class EntityResolverWebServiceSpec extends AnyWordSpec
  with Matchers
  with ScalatestRouteTest
  with SprayJsonSupport
  with TestDependencies {

  private val resolver = new EntityResolver(mdcProjectRegex, projectCodeMap)(config)

  private val projectsUri = "projects"
  private val routes = new Routes(resolver, projectsUri)
  private val exampleCode = "MDCP-0167"
  private val notACode = "text"


  "The web service" should {
    "return the expected project wrapper for a GET request with a valid MDC project code as a path parameter" in {
      Get(s"/$projectsUri/$exampleCode") ~> routes.topLevelRoute ~> check {
        val result = responseAs[List[Result]]
        assert(result.length === 1)
        assert(result.head.resolvedEntity === "Persephone")
      }
    }

    "return an empty json array for a GET request where no match is found" in {
      Get(s"/$projectsUri/$notACode") ~> routes.topLevelRoute ~> check {
        val result = responseAs[List[Result]]
        assert(result.length === 0)
      }
    }

    "return the expected project wrapper for a POST request with body: MDCP-0167" in {
      Post(s"/${projectsUri}", ByteString(exampleCode)) ~> routes.topLevelRoute ~> check {
        val result = responseAs[List[Result]]
        assert(result.head.resolvedEntity === "Persephone")
      }
    }

    "return an empty json array for a POST request where no match is found" in {
      Post(s"/${projectsUri}", ByteString(notACode)) ~> routes.topLevelRoute ~> check {
        val result = responseAs[List[Result]]
        assert(result.length === 0)
      }
    }

  }
}
