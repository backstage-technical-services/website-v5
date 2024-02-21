package org.backstage.awards

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
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test

@QuarkusTest
@TestHTTPEndpoint(AwardResource::class)
class AwardResourceTests {
    @Inject
    lateinit var repository: AwardRepository

    @Test
    fun `listing awards as an authenticated user should return a 401 response`() {
        RestAssured
            .`when`()
            .get()

            .then()
            .shouldBeUnauthorised()
    }

    @Test
    @TestSecurity(user = AuthHelpers.DEFAULT_USER_ID)
    fun `listing awards without the correct role should return a 403 response`() {
        RestAssured
            .`when`()
            .get()

            .then()
            .shouldBeForbidden()
    }

    @Test
    @TestSecurity(user = AuthHelpers.DEFAULT_USER_ID, roles = [Roles.Awards.READ])
    fun `listing the awards should return a valid response`() {
        RestAssured
            .`when`()
            .get()

            .then()
            .shouldBeJson()
            .statusCode(OK)
            .body("size()", greaterThan(0))
    }

    @Test
    fun `creating an award as an unauthenticated user should return a 401 response`() {
        RestAssured
            .given()
            .asJson(AwardFixtures.CREATE_REQUEST_JSON)

            .`when`()
            .post()

            .then()
            .shouldBeUnauthorised()
    }

    @Test
    @TestSecurity(user = AuthHelpers.DEFAULT_USER_ID)
    fun `creating an award without the correct role should return a 403 response`() {
        RestAssured
            .given()
            .asJson(AwardFixtures.CREATE_REQUEST_JSON)

            .`when`()
            .post()

            .then()
            .shouldBeForbidden()
    }

    @Test
    @TestSecurity(user = AuthHelpers.DEFAULT_USER_ID, roles = [Roles.Awards.CREATE])
    fun `creating an award should return a 204 status code and the resource ID`() {
        RestAssured
            .given()
            .asJson(AwardFixtures.CREATE_REQUEST_JSON)

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
    }

    @Test
    fun `getting the details of an award as an unauthenticated user should return a 401 response`() {
        RestAssured
            .`when`()
            .get("/${AwardFixtures.EXISTING_ENTITY_ID}")

            .then()
            .shouldBeUnauthorised()
    }

    @Test
    @TestSecurity(user = AuthHelpers.DEFAULT_USER_ID)
    fun `getting the details of an award without the correct role should return a 403 response`() {
        RestAssured
            .`when`()
            .get("/${AwardFixtures.EXISTING_ENTITY_ID}")

            .then()
            .shouldBeForbidden()
    }

    @Test
    @TestSecurity(user = AuthHelpers.DEFAULT_USER_ID, roles = [Roles.Awards.READ])
    fun `getting the details of an award should return the award details`() {
        val award = setUpTest {
            AwardFixtures.makeEntity()
                .also { repository.persist(it) }
        }

        RestAssured
            .`when`()
            .get("/${award.id}")

            .then()
            .shouldBeJson()
            .statusCode(OK)
            .body("id", equalTo(award.id()))

        cleanUpTest {
            repository.delete(award)
        }
    }

    @Test
    @TestSecurity(user = AuthHelpers.DEFAULT_USER_ID, roles = [Roles.Awards.READ])
    fun `getting the details of a non-existent award should return a 404 response`() {
        RestAssured
            .`when`()
            .get("/${AwardFixtures.NON_EXISTENT_ID}")

            .then()
            .shouldShowNotFound()
    }

    @Test
    fun `updating an award as an anonymous user should return a 401 response`() {
        RestAssured
            .given()
            .asJson(AwardFixtures.UPDATE_REQUEST_JSON)

            .`when`()
            .patch("/1")

            .then()
            .shouldBeUnauthorised()
    }

    @Test
    @TestSecurity(user = AuthHelpers.DEFAULT_USER_ID)
    fun `updating an award without the correct role should return a 403 response`() {
        RestAssured
            .given()
            .asJson(AwardFixtures.UPDATE_REQUEST_JSON)

            .`when`()
            .patch("/1")

            .then()
            .shouldBeForbidden()
    }


    @Test
    @TestSecurity(user = AuthHelpers.DEFAULT_USER_ID, roles = [Roles.Awards.UPDATE])
    fun `updating an existing award should return a 204 status code`() {
        val award = setUpTest {
            AwardFixtures.makeEntity()
                .also { repository.persist(it) }
        }

        RestAssured
            .given()
            .asJson(AwardFixtures.UPDATE_REQUEST_JSON)

            .`when`()
            .patch("/${award.id}")

            .then()
            .statusCode(NO_CONTENT)

        cleanUpTest {
            repository.delete(award)
        }
    }

    @Test
    @TestSecurity(user = AuthHelpers.DEFAULT_USER_ID, roles = [Roles.Awards.UPDATE])
    fun `updating a non-existent award should return a 404 response`() {
        RestAssured
            .given()
            .asJson(AwardFixtures.UPDATE_REQUEST_JSON)

            .`when`()
            .patch("/${AwardFixtures.NON_EXISTENT_ID}")

            .then()
            .shouldShowNotFound()
    }

    @Test
    fun `approving an award as an unauthenticated user should return a 401 response`() {
        RestAssured
            .`when`()
            .patch("/1/approve")

            .then()
            .shouldBeUnauthorised()
    }

    @Test
    @TestSecurity(user = AuthHelpers.DEFAULT_USER_ID)
    fun `approving an award without the correct role should return a 403 response`() {
        RestAssured
            .`when`()
            .patch("/1/approve")

            .then()
            .shouldBeForbidden()
    }

    @Test
    @TestSecurity(user = AuthHelpers.DEFAULT_USER_ID, roles = [Roles.Awards.APPROVE])
    fun `approving an award should return a 204 response`() {
        val award = setUpTest {
            AwardFixtures.makeEntity()
                .also { repository.persist(it) }
        }

        RestAssured
            .`when`()
            .patch("/${award.id}/approve")

            .then()
            .statusCode(NO_CONTENT)

        cleanUpTest {
            repository.delete(award)
        }
    }

    @Test
    fun `deleting an award as an unauthenticated user should return a 401 response`() {
        RestAssured
            .`when`().delete("/1")
            .printResponse()
            .then()
            .shouldBeUnauthorised()
    }

    @Test
    @TestSecurity(user = AuthHelpers.DEFAULT_USER_ID)
    fun `deleting an award as a user without the correct role should return a 403 response`() {
        RestAssured
            .`when`().delete("/1")

            .then()
            .shouldBeForbidden()
    }

    @Test
    @TestSecurity(user = AuthHelpers.DEFAULT_USER_ID, roles = [Roles.Awards.DELETE])
    fun `deleting an award should return a 204 status code`() {
        RestAssured
            .`when`().delete("/${AwardFixtures.NON_EXISTENT_ID}")

            .then()
            .statusCode(NO_CONTENT)
    }
}
