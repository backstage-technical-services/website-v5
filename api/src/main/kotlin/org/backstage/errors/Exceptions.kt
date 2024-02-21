package org.backstage.errors

import jakarta.ws.rs.WebApplicationException
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR

infix fun Response.Status.exceptionWithMessage(message: String) = WebApplicationException(message, this)

object ExceptionFactory {
    fun couldNotAssignId(entity: Any? = null) = when (entity) {
        null -> INTERNAL_SERVER_ERROR exceptionWithMessage "Failed to assign ID to entity"
        else -> INTERNAL_SERVER_ERROR exceptionWithMessage "Failed to assign ID to ${entity::class}"
    }

    fun entityMissingId(entity: Any) =
        INTERNAL_SERVER_ERROR exceptionWithMessage "Entity ${entity::class} is missing its ID"
}
