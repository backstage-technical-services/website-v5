package org.backstage.api.monitoring

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured
import jakarta.ws.rs.core.Response
import org.backstage.api.shouldBeJson
import org.backstage.api.statusCode
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

@QuarkusTest
class HealthCheckTest {
    @Test
    fun `the health check should always return up`() {
        RestAssured
            .given()
            .`when`().get("/q/health/live")
            .then()
            .statusCode(Response.Status.OK)
            .shouldBeJson()
            .body("status", equalTo("UP"))
    }
}
