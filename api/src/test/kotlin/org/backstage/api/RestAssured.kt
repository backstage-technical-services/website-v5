package org.backstage.api

import io.restassured.http.ContentType
import io.restassured.response.Response
import io.restassured.response.ValidatableResponse
import io.restassured.specification.RequestSpecification
import jakarta.ws.rs.core.Response.StatusType


fun RequestSpecification.asJson(): RequestSpecification = this.contentType(ContentType.JSON)
fun RequestSpecification.asJson(body: Any): RequestSpecification = this.asJson().body(body)

fun Response.`do`(block: Response.() -> Unit): Response = this.apply { block() }
fun Response.printResponse(): Response = this.apply { prettyPrint() }

fun ValidatableResponse.shouldBeJson(): ValidatableResponse = this.contentType(ContentType.JSON)
fun ValidatableResponse.statusCode(status: StatusType): ValidatableResponse = this.statusCode(status.statusCode)
