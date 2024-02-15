package org.backstage.usergroup

import io.quarkus.test.common.http.TestHTTPEndpoint
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.security.TestSecurity
import io.restassured.RestAssured
import jakarta.ws.rs.core.Response.Status.OK
import org.backstage.*
import org.backstage.auth.Roles
import org.junit.jupiter.api.Test

@QuarkusTest
@TestHTTPEndpoint(UserGroupResource::class)
class UserGroupResourceTests {
    @Test
    fun `listing user groups as an authenticated user should return a 401 response`() {
        RestAssured
            .`when`()
            .get()

            .then()
            .shouldBeUnauthorised()
    }

    @Test
    @TestSecurity(user = AuthHelpers.DEFAULT_USER_ID)
    fun `listing user groups without the correct role should return a 403 response`() {
        RestAssured
            .`when`()
            .get()

            .then()
            .shouldBeForbidden()
    }

    @Test
    @TestSecurity(user = AuthHelpers.DEFAULT_USER_ID, roles = [Roles.UserGroups.READ])
    fun `listing the user groups should return a valid response`() {
        RestAssured
            .`when`()
            .get()

            .then()
            .shouldBeJson()
            .statusCode(OK)
    }
}
