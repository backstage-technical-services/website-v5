package org.backstage.quotes

import com.fasterxml.jackson.module.kotlin.readValue
import io.kotest.assertions.json.shouldContainJsonKeyValue
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.backstage.util.objectMapper

class QuoteSerialisationTests : BehaviorSpec() {
    init {
        Given("the JSON for a create request") {
            val requestJson = QuoteFixtures.CREATE_REQUEST_JSON

            When("de-serialising the request") {
                val request = objectMapper.readValue<QuoteRequest.Create>(requestJson)

                Then("the DTO should be de-serialised correctly") {
                    request shouldBe QuoteFixtures.CREATE_REQUEST
                }
            }
        }

        Given("the default response DTO") {
            val response = QuoteFixtures.RESPONSE_DEFAULT

            When("serialising the response") {
                val responseJson = objectMapper.writeValueAsString(response)

                Then("the response should be serialised correctly") {
                    responseJson.shouldEqualJson(QuoteFixtures.RESPONSE_DEFAULT_JSON)
                }
            }
        }

        Given("the default response DTO for a quote the user has voted for") {
            val response = QuoteFixtures.RESPONSE_DEFAULT.copy(
                userVote = QuoteVoteType.UPVOTE,
            )

            When("serialising the response") {
                val responseJson = objectMapper.writeValueAsString(response)

                Then("the userVote property should be serialised correctly") {
                    responseJson.shouldContainJsonKeyValue("userVote", "UPVOTE")
                }
            }
        }
    }
}
