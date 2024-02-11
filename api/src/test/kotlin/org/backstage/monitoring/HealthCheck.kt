package org.backstage.monitoring

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

@QuarkusTest
class HealthCheckTests {
    @Test
    fun `liveness check should return as healthy`() {
        given()
            .`when`().get("/q/health/live")
            .then()
            .statusCode(200)
            .body("status", equalTo("UP"))
    }
}
