package org.backstage.quotes

import com.fasterxml.jackson.module.kotlin.readValue
import io.kotest.assertions.json.shouldContainJsonKeyValue
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import org.backstage.util.objectMapper

class QuoteSerialisationTests : FunSpec() {
    init {
        test("the JSON for a create request should be de-serialised correctly") {
            val requestJson = QuoteFixtures.CREATE_REQUEST_JSON

            val request = objectMapper.readValue<QuoteRequest.Create>(requestJson)

            request shouldBe QuoteFixtures.CREATE_REQUEST
        }

        test("the default response DTO should serialise correctly") {
            val response = QuoteFixtures.RESPONSE_DEFAULT

            val responseJson = objectMapper.writeValueAsString(response)

            responseJson.shouldEqualJson(QuoteFixtures.RESPONSE_DEFAULT_JSON)
        }

        test("a default response DTO that the user has voted for should have the userVote property set") {
            val response = QuoteFixtures.RESPONSE_DEFAULT.copy(
                userVote = QuoteVoteType.UPVOTE,
            )

            val responseJson = objectMapper.writeValueAsString(response)

            responseJson.shouldContainJsonKeyValue("userVote", "UPVOTE")
        }

        context("the action response DTO should be serialised correctly") {
            withData(nameFn = {"action = $it"}, QuoteVoteAction.entries) {action ->
                val response = QuoteResponse.Vote(action = action)

                val responseJson = objectMapper.writeValueAsString(response)

                responseJson.shouldEqualJson("""
                    {
                        "action": "${action.name}"
                    }
                """.trimIndent())
            }
        }
    }
}
