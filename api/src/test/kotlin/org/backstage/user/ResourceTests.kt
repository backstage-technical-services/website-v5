package org.backstage.user

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
import org.hamcrest.Matchers.greaterThan
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.Test

@QuarkusTest
@TestHTTPEndpoint(UserResource::class)
class UserResourceTests {
    @Inject
    lateinit var repository: UserRepository

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
    @TestSecurity(user = AuthHelpers.DEFAULT_USER_ID, roles = [Roles.Users.READ])
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
            .asJson(UserFixtures.CREATE_REQUEST_JSON)

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
            .asJson(UserFixtures.CREATE_REQUEST_JSON)

            .`when`()
            .post()

            .then()
            .shouldBeForbidden()
    }

    @Test
    @TestSecurity(user = AuthHelpers.DEFAULT_USER_ID, roles = [Roles.Users.CREATE])
    fun `creating an award should return a 204 status code and the resource ID`() {
        RestAssured
            .given()
            .asJson(UserFixtures.CREATE_REQUEST_JSON)

            .`when`()
            .post()

            .then()
            .statusCode(NO_CONTENT)
            .header(HttpHeaders.RESOURCE_ID, notNullValue())

            .extractId { userId ->
                cleanUpTest {
                    repository.deleteById(userId)
                }
            }
    }
}
