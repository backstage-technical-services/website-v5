package org.backstage.monitoring

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test

@QuarkusTest
class MetricsTests {
    @Test
    fun `metrics endpoint should return a non-empty response`() {
        given()
            .`when`().get("/q/metrics")
            .then()
            .statusCode(200)
            .body(Matchers.not(Matchers.blankOrNullString()))
    }
}
