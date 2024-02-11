package org.backstage.error

import io.kotest.assertions.json.shouldContainJsonKeyValue
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.assertions.json.shouldNotContainJsonKey
import io.kotest.core.spec.style.BehaviorSpec
import org.backstage.util.objectMapper
import java.time.LocalDateTime

class ErrorResponseSerialisationTests : BehaviorSpec() {
    init {
        Given("a general error response") {
            val errorResponse = GENERAL_ERROR

            When("serialising the response") {
                val serialisedError = objectMapper.writeValueAsString(errorResponse)

                Then("the serialised response should match the expected JSON") {
                    serialisedError shouldEqualJson """
                        {
                            "timestamp": "2020-01-01 00:00:00",
                            "code": "ERROR_CODE",
                            "message": "An example message"
                        }
                    """.trimIndent()
                }
            }
        }

        Given("a general error response with no message") {
            val errorResponse = GENERAL_ERROR.copy(
                message = null
            )

            When("serialising the response") {
                val serialisedError = objectMapper.writeValueAsString(errorResponse)

                Then("the message key should be missing") {
                    serialisedError shouldNotContainJsonKey "message"
                }
            }
        }

        Given("a validation error DTO") {
            val errorResponse = VALIDATION_ERROR

            When("serialising the response") {
                val serialisedError = objectMapper.writeValueAsString(errorResponse)

                Then("the serialised response should match the expected JSON") {
                    serialisedError shouldEqualJson """
                        {
                            "timestamp": "2020-01-01 00:00:00",
                            "code": 422,
                            "errors": [
                                {
                                    "field": "prop",
                                    "value": "val",
                                    "messageKey": "${ErrorCode.UNKNOWN_ENUM_VALUE}"
                                }
                            ]
                        }
                    """.trimIndent()
                }
            }
        }

        Given("a validation error with no errors") {
            val errorResponse = VALIDATION_ERROR.copy(errors = emptyList())

            When("serialising the response") {
                val serialisedError = objectMapper.writeValueAsString(errorResponse)

                Then("the serialised response should contain the errors key") {
                    serialisedError.shouldContainJsonKeyValue("errors", emptyList<Any>())
                }
            }
        }
    }

    companion object {
        private val GENERAL_ERROR = GeneralError(
            timestamp = LocalDateTime.of(2020, 1, 1, 0, 0, 0),
            code = "ERROR_CODE",
            message = "An example message"
        )
        private val VALIDATION_ERROR = ValidationError(
            timestamp = LocalDateTime.of(2020, 1, 1, 0, 0, 0),
            errors = listOf(
                ValidationError.Violation(
                    field = "prop",
                    value = "val",
                    messageKey = ErrorCode.UNKNOWN_ENUM_VALUE,
                )
            ),
        )
    }
}
