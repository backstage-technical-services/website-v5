package org.backstage.user

import com.fasterxml.jackson.module.kotlin.readValue
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.backstage.util.objectMapper

class UserSerialisationTests : BehaviorSpec() {
    init {
        Given("the JSON for a create request") {
            val requestJson = UserFixtures.CREATE_REQUEST_JSON

            When("de-serialising the request") {
                val request = objectMapper.readValue<UserRequest.Create>(requestJson)

                Then("the DTO should be de-serialised correctly") {
                    request shouldBe UserFixtures.CREATE_REQUEST
                }
            }
        }

        Given("the minimal response DTO") {
            val response = UserFixtures.RESPONSE_MINIMAL.copy()

            When("serialising the response") {
                val responseJson = objectMapper.writeValueAsString(response)

                Then("the response should be serialised correctly") {
                    responseJson.shouldEqualJson(UserFixtures.RESPONSE_MINIMAL_JSON)
                }
            }
        }
    }
}
