package org.backstage

import io.restassured.http.ContentType
import io.restassured.response.Response
import io.restassured.response.ValidatableResponse
import io.restassured.specification.RequestSpecification
import jakarta.ws.rs.core.Response.Status.*
import jakarta.ws.rs.core.Response.StatusType
import org.backstage.http.HttpHeaders
import org.hamcrest.Matchers

fun RequestSpecification.asJson(): RequestSpecification = this.contentType(ContentType.JSON)
fun RequestSpecification.asJson(body: Any): RequestSpecification = this.asJson().body(body)

fun Response.`do`(block: Response.() -> Unit): Response = this.apply { block() }
fun Response.printResponse(): Response = this.apply { prettyPrint() }

fun ValidatableResponse.shouldBeJson(): ValidatableResponse = this.contentType(ContentType.JSON)
fun ValidatableResponse.statusCode(status: StatusType): ValidatableResponse = this.statusCode(status.statusCode)


fun ValidatableResponse.shouldBeUnauthorised(): ValidatableResponse = this
    .statusCode(UNAUTHORIZED)
//    .body(Matchers.blankOrNullString())

fun ValidatableResponse.shouldBeForbidden(): ValidatableResponse = this
    .statusCode(FORBIDDEN)
//    .body(Matchers.blankOrNullString())

fun ValidatableResponse.shouldShowNotFound(): ValidatableResponse = this
    .shouldBeJson()
    .statusCode(NOT_FOUND)
    .body("code", Matchers.equalTo(NOT_FOUND.statusCode))
    .body("message", Matchers.containsString("Could not find"))

fun ValidatableResponse.shouldShowNotImplemented(): ValidatableResponse = this
    .shouldBeJson()
    .statusCode(NOT_IMPLEMENTED)
    .body("message", Matchers.containsString("Method not implemented"))

fun ValidatableResponse.extractId(block: (Long) -> Unit) {
    val id = and().extract().header(HttpHeaders.RESOURCE_ID).toLong()

    block(id)
}
