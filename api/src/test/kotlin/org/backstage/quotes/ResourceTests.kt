package org.backstage.quotes

import io.quarkus.test.common.http.TestHTTPEndpoint
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.security.TestSecurity
import io.restassured.RestAssured
import jakarta.inject.Inject
import jakarta.ws.rs.core.Response.Status.NO_CONTENT
import jakarta.ws.rs.core.Response.Status.OK
import org.backstage.*
import org.backstage.auth.Roles
import org.backstage.http.HttpHeaders
import org.backstage.users.UserFixtures
import org.backstage.users.UserRepository
import org.hamcrest.Matchers.greaterThan
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.Test

@QuarkusTest
@TestHTTPEndpoint(QuoteResource::class)
class QuoteResourceTests {
    @Inject
    lateinit var repository: QuoteRepository

    @Inject
    lateinit var userRepository: UserRepository

    @Test
    fun `listing quotes as an authenticated user should return a 401 response`() {
        RestAssured
            .`when`()
            .get()

            .then()
            .shouldBeUnauthorised()
    }

    @Test
    @TestSecurity(user = AuthHelpers.DEFAULT_USER_ID)
    fun `listing quotes without the correct role should return a 403 response`() {
        RestAssured
            .`when`()
            .get()

            .then()
            .shouldBeForbidden()
    }

    @Test
    @TestSecurity(user = AuthHelpers.DEFAULT_USER_ID, roles = [Roles.Quotes.READ])
    fun `listing the quotes should return a valid response`() {
        val user = setUpTest {
            UserFixtures.makeEntity(identityId = AuthHelpers.DEFAULT_USER_ID)
                .also { userRepository.persist(it) }
        }

        RestAssured
            .`when`()
            .get()

            .then()
            .shouldBeJson()
            .statusCode(OK)
            .body("size()", greaterThan(0))

        cleanUpTest {
            userRepository.delete(user)
        }
    }

    @Test
    fun `creating a quote as an unauthenticated user should return a 401 response`() {
        RestAssured
            .given()
            .asJson(QuoteFixtures.CREATE_REQUEST_JSON)

            .`when`()
            .post()

            .then()
            .shouldBeUnauthorised()
    }

    @Test
    @TestSecurity(user = AuthHelpers.DEFAULT_USER_ID)
    fun `creating a quote without the correct role should return a 403 response`() {
        RestAssured
            .given()
            .asJson(QuoteFixtures.CREATE_REQUEST_JSON)

            .`when`()
            .post()

            .then()
            .shouldBeForbidden()
    }

    @Test
    @TestSecurity(user = AuthHelpers.DEFAULT_USER_ID, roles = [Roles.Quotes.CREATE])
    fun `creating a quote should return a 204 status code and the resource ID`() {
        val user = setUpTest {
            UserFixtures.makeEntity(identityId = AuthHelpers.DEFAULT_USER_ID)
                .also { userRepository.persist(it) }
        }

        RestAssured
            .given()
            .asJson(QuoteFixtures.CREATE_REQUEST_JSON)

            .`when`()
            .post()

            .then()
            .statusCode(NO_CONTENT)
            .header(HttpHeaders.RESOURCE_ID, notNullValue())

            .extractId { awardId ->
                cleanUpTest {
                    repository.deleteById(awardId)
                }
            }

        cleanUpTest {
            userRepository.delete(user)
        }
    }

    @Test
    fun `upvoting a quote as an anonymous user should return a 401 response`() {
        RestAssured
            .`when`()
            .patch("/1/upvote")

            .then()
            .shouldBeUnauthorised()
    }

    @Test
    @TestSecurity(user = AuthHelpers.DEFAULT_USER_ID)
    fun `upvoting a quote without the correct role should return a 403 response`() {
        RestAssured
            .`when`()
            .patch("/1/upvote")

            .then()
            .shouldBeForbidden()
    }


    @Test
    @TestSecurity(user = AuthHelpers.DEFAULT_USER_ID, roles = [Roles.Quotes.VOTE])
    fun `upvoting a quote should return a 204 status code`() {
        val user = setUpTest {
            UserFixtures.makeEntity(identityId = AuthHelpers.DEFAULT_USER_ID)
                .also { userRepository.persist(it) }
        }
        val quote = setUpTest {
            QuoteFixtures.makeEntity(addedBy = user)
                .also { repository.persist(it) }
        }

        RestAssured
            .`when`()
            .patch("/${quote.id}/upvote")

            .then()
            .statusCode(OK)
            .body("action", notNullValue())

        cleanUpTest {
            repository.delete(quote)
            userRepository.delete(user)
        }
    }

    @Test
    @TestSecurity(user = AuthHelpers.DEFAULT_USER_ID, roles = [Roles.Quotes.VOTE])
    fun `upvoting a non-existent quote should return a 404 response`() {
        RestAssured
            .`when`()
            .patch("/1000/upvote")

            .then()
            .shouldShowNotFound()
    }

    @Test
    fun `downvoting a quote as an anonymous user should return a 401 response`() {
        RestAssured
            .`when`()
            .patch("/1/downvote")

            .then()
            .shouldBeUnauthorised()
    }

    @Test
    @TestSecurity(user = AuthHelpers.DEFAULT_USER_ID)
    fun `downvoting a quote without the correct role should return a 403 response`() {
        RestAssured
            .`when`()
            .patch("/1/downvote")

            .then()
            .shouldBeForbidden()
    }


    @Test
    @TestSecurity(user = AuthHelpers.DEFAULT_USER_ID, roles = [Roles.Quotes.VOTE])
    fun `downvoting a quote should return a 204 status code`() {
        val user = setUpTest {
            UserFixtures.makeEntity(identityId = AuthHelpers.DEFAULT_USER_ID)
                .also { userRepository.persist(it) }
        }
        val quote = setUpTest {
            QuoteFixtures.makeEntity(addedBy = user)
                .also { repository.persist(it) }
        }

        RestAssured
            .`when`()
            .patch("/${quote.id}/downvote")

            .then()
            .statusCode(OK)
            .body("action", notNullValue())

        cleanUpTest {
            repository.delete(quote)
            userRepository.delete(user)
        }
    }

    @Test
    @TestSecurity(user = AuthHelpers.DEFAULT_USER_ID, roles = [Roles.Quotes.VOTE])
    fun `downvoting a non-existent quote should return a 404 response`() {
        RestAssured
            .`when`()
            .patch("/1000/downvote")

            .then()
            .shouldShowNotFound()
    }

    @Test
    fun `deleting a quote as an unauthenticated user should return a 401 response`() {
        RestAssured
            .`when`()
            .delete("/1")

            .then()
            .shouldBeUnauthorised()
    }

    @Test
    @TestSecurity(user = AuthHelpers.DEFAULT_USER_ID)
    fun `deleting a quote as a user without the correct role should return a 403 response`() {
        RestAssured
            .`when`()
            .delete("/1")

            .then()
            .shouldBeForbidden()
    }

    @Test
    @TestSecurity(user = AuthHelpers.DEFAULT_USER_ID, roles = [Roles.Quotes.DELETE])
    fun `deleting a quote should return a 204 status code`() {
        val user = setUpTest {
            UserFixtures.makeEntity(identityId = AuthHelpers.DEFAULT_USER_ID)
                .also { userRepository.persist(it) }
        }
        val quote = setUpTest {
            QuoteFixtures.makeEntity(addedBy = user)
                .also { repository.persist(it) }
        }

        RestAssured
            .`when`()
            .delete("/${quote.id}")

            .then()
            .statusCode(NO_CONTENT)

        cleanUpTest {
            userRepository.delete(user)
        }
    }

    @Test
    @TestSecurity(user = AuthHelpers.DEFAULT_USER_ID, roles = [Roles.Quotes.DELETE])
    fun `deleting a non-existent quote should return a 204 status code`() {
        RestAssured
            .`when`()
            .delete("/1000")

            .then()
            .statusCode(NO_CONTENT)
    }
}
