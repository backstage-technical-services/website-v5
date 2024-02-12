package org.backstage.usergroup

import io.kotest.assertions.json.shouldEqualJson
import io.kotest.core.spec.style.BehaviorSpec
import org.backstage.util.objectMapper

class UserGroupSerialisationTests : BehaviorSpec() {
    init {
        Given("a default response DTO") {
            val response = UserGroupFixtures.DEFAULT_RESPONSE.copy()

            When("serialising the response") {
                val responseJson = objectMapper.writeValueAsString(response)

                Then("the response should be serialised correctly") {
                    responseJson.shouldEqualJson(UserGroupFixtures.DEFAULT_RESPONSE_JSON)
                }
            }
        }
    }
}
