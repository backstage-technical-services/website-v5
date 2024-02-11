package org.backstage.error

import com.fasterxml.jackson.databind.JsonMappingException
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldContainAll
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import jakarta.persistence.EntityNotFoundException
import jakarta.ws.rs.ForbiddenException
import jakarta.ws.rs.NotAllowedException
import jakarta.ws.rs.NotAuthorizedException
import jakarta.ws.rs.WebApplicationException
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.Response.Status.*
import jakarta.ws.rs.core.Response.StatusType
import org.backstage.http.HttpHeaders
import java.sql.SQLException
import kotlin.reflect.jvm.internal.ReflectProperties.Val
import org.hibernate.exception.ConstraintViolationException as HibernateConstraintViolationException

class ExceptionHandlerTests : BehaviorSpec() {
    init {
        Given("an illegal argument exception") {
            val exception = IllegalArgumentException()

            When("building the response") {
                val response = ExceptionResponseFactory.handleThrownException(exception)

                Then("the status code should be correct") {
                    response.status shouldBe BAD_REQUEST.statusCode
                }
                Then("the response should have the content type") {
                    response.shouldHaveContentTypeHeader()
                }
                Then("The response should have the correct message") {
                    response.shouldBeGeneralErrorWith(
                        status = BAD_REQUEST,
                        message = null
                    )
                }
            }
        }

        Given("a WebApplicationException") {
            val exception = WebApplicationException("Message", BAD_GATEWAY)

            When("building the response") {
                val response = ExceptionResponseFactory.handleThrownException(exception)

                Then("the status code should be set correctly") {
                    response.status shouldBe exception.response.status
                }

                Then("the body should be set correctly") {
                    response.hasEntity().shouldBeTrue()
                    with(response.readEntity(GeneralError::class.java)) {
                        code shouldBe exception.response.status
                        message shouldBe exception.message
                    }
                }
            }
        }

        Given("a NotAuthorizedException") {
            val exception = NotAuthorizedException("Not authorized", Response.status(UNAUTHORIZED).build())

            When("building the response") {
                val response = ExceptionResponseFactory.handleThrownException(exception)

                Then("the status code should be set correctly") {
                    response.status shouldBe exception.response.status
                }

                Then("the body should be set correctly") {
                    response.hasEntity().shouldBeFalse()
                }
            }
        }

        Given("a ForbiddenException") {
            val exception = ForbiddenException("Forbidden")

            When("building the response") {
                val response = ExceptionResponseFactory.handleThrownException(exception)

                Then("the status code should be set correctly") {
                    response.status shouldBe exception.response.status
                }

                Then("the body should be set correctly") {
                    response.hasEntity().shouldBeFalse()
                }
            }
        }

        Given("a not allowed exception") {
            val exception = NotAllowedException(Throwable(), "GET")

            When("building the response") {
                val response = ExceptionResponseFactory.handleThrownException(exception)

                Then("the status code should be correct") {
                    response.status shouldBe METHOD_NOT_ALLOWED.statusCode
                }
            }
        }

        Given("a not implemented error") {
            val exception = NotImplementedError()

            When("building the response") {
                val response = ExceptionResponseFactory.handleThrownException(exception)

                Then("the status code should be correct") {
                    response.status shouldBe NOT_IMPLEMENTED.statusCode
                }
                Then("the response should have the content type") {
                    response.shouldHaveContentTypeHeader()
                }
                Then("The response should have the correct message") {
                    response.shouldBeGeneralErrorWith(
                        status = NOT_IMPLEMENTED,
                        message = "Method not implemented"
                    )
                }
            }
        }

        Given("a general Throwable") {
            val exception = Throwable("Message")

            When("building the response") {
                val response = ExceptionResponseFactory.handleThrownException(exception)

                Then("the status code should be correct") {
                    response.status shouldBe INTERNAL_SERVER_ERROR.statusCode
                }
                Then("the response should have the content type") {
                    response.shouldHaveContentTypeHeader()
                }
            }
        }

        Given("no exception") {
            val exception: Throwable? = null

            When("building the response") {
                val response = ExceptionResponseFactory.handleThrownException(exception)

                Then("the status code should be correct") {
                    response.status shouldBe INTERNAL_SERVER_ERROR.statusCode
                }
                Then("the response should have the content type") {
                    response.shouldHaveContentTypeHeader()
                }
                Then("the body should have the expected message") {
                    response.shouldBeGeneralErrorWith(
                        status = INTERNAL_SERVER_ERROR,
                        message = "An unknown and unhandled error has occurred"
                    )
                }
            }
        }
    }
}

class PersistenceExceptionHandlerTests : BehaviorSpec() {
    init {
        Given("a hibernate constraint violation exception") {
            val exception = HibernateConstraintViolationException("Message", SQLException(), "Constraint")

            When("building the response") {
                val response = ExceptionResponseFactory.handleThrownException(exception)

                Then("the response should have the correct status code") {
                    response.status shouldBe UnprocessableEntityStatus.statusCode
                }
                Then("the response should have the content type") {
                    response.shouldHaveContentTypeHeader()
                }
                Then("the response should have the correct body") {
                    response.shouldBeGeneralErrorWith(
                        status = UnprocessableEntityStatus,
                        message = exception.message
                    )
                }
            }
        }

        Given("an entity not found exception") {
            val exception = EntityNotFoundException()

            When("building the response") {
                val response = ExceptionResponseFactory.handleThrownException(exception)

                Then("the response should have the correct status code") {
                    response.status shouldBe NOT_FOUND.statusCode
                }
                Then("the response should have the content type") {
                    response.shouldHaveContentTypeHeader()
                }
                Then("the response should have the correct body") {
                    response.shouldBeGeneralErrorWith(
                        status = NOT_FOUND,
                        message = null
                    )
                }
            }
        }
    }
}

class BuildErrorResponseTests : DescribeSpec() {
    init {
        describe("building a validation error response from an unprocessable entity status") {
            val response = UnprocessableEntityStatus.buildValidationErrorResponse(
                field = "FIELD",
                value = "VALUE",
                messageKey = "KEY",
                message = "The error message",
            )

            it("the response should contain the correct status code") {
                response.status shouldBe UnprocessableEntityStatus.statusCode
            }
            it("the response should include the content type header") {
                response.shouldHaveContentTypeHeader()
            }
            it("the body should be correct") {
                response.shouldBeValidationErrorWith(
                    field = "FIELD",
                    value = "VALUE",
                    messageKey = "KEY",
                    message = "The error message",
                )
            }
        }

        describe("building a validation error response from an unprocessable entity status with no message key") {
            val response = UnprocessableEntityStatus.buildValidationErrorResponse(
                field = "FIELD",
                value = "VALUE",
                messageKey = null
            )

            it("the constraint should be null") {
                response.shouldBeValidationErrorWith(
                    field = "FIELD",
                    value = "VALUE",
                    messageKey = null,
                    message = null,
                )
            }
        }

        describe("building an error response from any status") {
            val status = NOT_FOUND
            val response = status.buildResponse(
                message = "The error message"
            )

            it("the response should have the correct status code") {
                response.status shouldBe status.statusCode
            }
            it("the response should include the content type header") {
                response.shouldHaveContentTypeHeader()
            }
            it("the body should contain the correct information") {
                response.shouldBeGeneralErrorWith(
                    status = status,
                    message = "The error message"
                )
            }
        }

        describe("building an error response from a status with no message") {
            val response = NOT_FOUND.buildResponse()

            it("the message should be null") {
                val entity = response.entity
                entity.shouldBeTypeOf<GeneralError>()
                entity.message.shouldBeNull()
            }
        }

        describe("building an error response from a general Throwable and status code") {
            val status = NOT_FOUND
            val exception = Throwable("A test exception")
            val response = exception.buildGeneralErrorResponse(status)

            it("the response should have the correct status code") {
                response.status shouldBe status.statusCode
            }
            it("the response should include the content type header") {
                response.shouldHaveContentTypeHeader()
            }
            it("the body should contain the correct information") {
                response.shouldBeGeneralErrorWith(
                    status = status,
                    message = exception.message
                )
            }
        }

        describe("building an error response from a HttpException") {
            val exception = WebApplicationException("Bad Request", BAD_REQUEST)
            val response = exception.buildGeneralErrorResponse()

            it("the response should have the correct status code") {
                response.status shouldBe BAD_REQUEST.statusCode
            }
            it("the response should include the content type header") {
                response.shouldHaveContentTypeHeader()
            }
            it("the body should contain the correct information") {
                response.shouldBeGeneralErrorWith(
                    status = BAD_REQUEST,
                    message = exception.message
                )
            }
        }
    }
}

class PathBuildingTests : BehaviorSpec() {
    init {
        Given("a list of paths, with and without field names") {
            val paths = listOf(
                JsonMappingException.Reference("from", "field"),
                JsonMappingException.Reference("from", 1),
                JsonMappingException.Reference("from", "second"),
                JsonMappingException.Reference("from", "third"),
                JsonMappingException.Reference("from", 6)
            )

            When("building to a string") {
                val path = paths.buildPath()

                Then("the result should be correct") {
                    path shouldBe "field[1].second.third[6]"
                }
            }
        }
    }
}

fun Response.shouldHaveContentTypeHeader(headerValue: String = MediaType.APPLICATION_JSON) {
    this.headers[HttpHeaders.CONTENT_TYPE]?.firstOrNull() shouldBe headerValue
}

fun Response.shouldBeValidationErrorWith(
    field: String,
    value: Any? = null,
    messageKey: String? = null,
    message: String? = null
) {
    with(entity) {
        shouldBeTypeOf<ValidationError>()
        code shouldBe UnprocessableEntityStatus.statusCode

        errors shouldHaveSize 1

        with(errors.first()) {
            this.field shouldBe field

            when (value) {
                null -> this.value.shouldBeNull()
                else -> {
                    this.value.shouldNotBeNull()
                    this.value shouldBe value
                }
            }

            when (messageKey) {
                null -> this.messageKey.shouldBeNull()
                else -> {
                    this.messageKey.shouldNotBeNull()
                    this.messageKey shouldBe messageKey
                }
            }

            when (message) {
                null -> this.message.shouldBeNull()
                else -> {
                    this.message.shouldNotBeNull()
                    this.message shouldBe message
                }
            }
        }
    }
}

fun Response.shouldBeGeneralErrorWith(status: StatusType, message: String?) {
    with(this.entity) {
        this.shouldBeTypeOf<GeneralError>()
        this.code shouldBe status.statusCode

        when (message) {
            null -> this.message.shouldBeNull()
            else -> {
                this.message.shouldNotBeNull()
                this.message shouldBe message
            }
        }

        this.timestamp.shouldNotBeNull()
    }
}
