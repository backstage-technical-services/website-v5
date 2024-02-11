package org.backstage.error

import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import jakarta.persistence.EntityNotFoundException
import jakarta.validation.ConstraintViolationException
import jakarta.ws.rs.ForbiddenException
import jakarta.ws.rs.NotAuthorizedException
import jakarta.ws.rs.WebApplicationException
import jakarta.ws.rs.core.HttpHeaders
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.Response.Status
import jakarta.ws.rs.core.Response.StatusType
import jakarta.ws.rs.ext.ExceptionMapper
import jakarta.ws.rs.ext.Provider
import org.backstage.error.JsonExceptionHandler.handleInvalidFormat
import org.backstage.error.JsonExceptionHandler.handleInvalidJsonType
import org.backstage.error.JsonExceptionHandler.handleMismatchedInput
import org.backstage.error.JsonExceptionHandler.handleValidationConstraintViolation
import org.backstage.error.PersistenceExceptionHandler.handleEntityNotFound
import org.backstage.error.PersistenceExceptionHandler.handleHibernateConstraintViolation
import org.jboss.logging.Logger
import java.time.format.DateTimeParseException
import org.hibernate.exception.ConstraintViolationException as HibernateConstraintViolationException

private val logger = Logger.getLogger(ExceptionResponseFactory::class.java)

@Provider
class GlobalExceptionMapper : ExceptionMapper<Throwable> {
    override fun toResponse(exception: Throwable): Response = ExceptionResponseFactory.handleThrownException(exception)
}

@Provider
class ConstraintViolationExceptionMapper : ExceptionMapper<ConstraintViolationException> {
    override fun toResponse(exception: ConstraintViolationException): Response =
        ExceptionResponseFactory.handleThrownException(exception)
}

object ExceptionResponseFactory {
    fun handleThrownException(exception: Throwable?): Response {
        tailrec fun handleForException(exception: Throwable?, parent: Throwable? = null): Response =
            when (exception) {
                is IllegalArgumentException -> handleIllegalArgumentException(exception)
                is EntityNotFoundException -> handleEntityNotFound(exception)
                is InvalidFormatException -> handleInvalidFormat(exception)
                is InvalidTypeIdException -> handleInvalidJsonType(exception)
                is MismatchedInputException -> handleMismatchedInput(exception)
                is HibernateConstraintViolationException -> handleHibernateConstraintViolation(exception)
                is NotImplementedError -> handleNotImplemented(exception)
                is WebApplicationException -> handleWebApplicationException(exception)
                is ConstraintViolationException -> handleValidationConstraintViolation(exception)
                is NullPointerException -> handleNullPointerException(exception)
                null, parent -> unhandledException(
                    parent ?: Exception("An unknown and unhandled error has occurred")
                )

                else -> handleForException(exception.cause, exception)
            }

        return handleForException(exception, null)
    }

    private fun handleIllegalArgumentException(exception: Throwable): Response = exception
        .apply(logger::recordError)
        .buildGeneralErrorResponse(status = Status.BAD_REQUEST)

    private fun handleWebApplicationException(exception: WebApplicationException): Response = when (exception) {
        is NotAuthorizedException, is ForbiddenException -> Response.status(exception.response.statusInfo).build()
        else -> exception.buildGeneralErrorResponse()
    }

    private fun handleNotImplemented(
        @Suppress("UNUSED_PARAMETER") exception: NotImplementedError
    ): Response = Status.NOT_IMPLEMENTED
        .buildResponse(message = "Method not implemented")

    private fun handleNullPointerException(exception: NullPointerException): Response = when {
        exception.message?.contains("Parameter specified as non-null is null") ?: false -> Status.BAD_REQUEST
            .buildResponse("Missing required parameter")
        else -> unhandledException(exception)
    }

    private fun unhandledException(exception: Throwable): Response = exception
        .apply(logger::recordError)
        .buildGeneralErrorResponse(status = Status.INTERNAL_SERVER_ERROR)
}

object JsonExceptionHandler {
    fun handleInvalidFormat(exception: InvalidFormatException): Response {
        val fieldPath = exception.path.buildPath()

        return when (val cause = exception.cause) {
            is DateTimeParseException -> handleDateTimeParseException(cause, fieldPath)
            else -> when (exception.targetType.isEnum) {
                true -> handleInvalidEnumValue(exception, fieldPath)
                false -> UnprocessableEntityStatus.buildValidationErrorResponse(
                    field = fieldPath,
                    value = exception.value,
                    messageKey = ErrorCode.INVALID_FORMAT
                )
            }
        }
    }

    fun handleInvalidJsonType(exception: InvalidTypeIdException): Response =
        UnprocessableEntityStatus.buildValidationErrorResponse(
            field = exception.path.buildPath(),
            value = exception.typeId,
            messageKey = ErrorCode.INVALID_JSON_TYPE
        )

    fun handleMismatchedInput(exception: MismatchedInputException): Response =
        UnprocessableEntityStatus.buildValidationErrorResponse(
            field = exception.path.buildPath(),
            value = null,
            messageKey = ErrorCode.INCORRECT_TYPE,
            message = "Expected type: ${exception.targetType.name.substringAfterLast(".")}",
        )

    private fun handleInvalidEnumValue(exception: InvalidFormatException, fieldPath: String) =
        UnprocessableEntityStatus.buildValidationErrorResponse(
            field = fieldPath,
            value = exception.value,
            messageKey = ErrorCode.INVALID_ENUM_VALUE,
            message = "Expected one of: ${exception.targetType.enumConstants.joinToString(separator = ", ")}",
        )

    private fun handleDateTimeParseException(exception: DateTimeParseException, fieldPath: String): Response =
        UnprocessableEntityStatus.buildValidationErrorResponse(
            field = fieldPath,
            value = exception.parsedString,
            messageKey = ErrorCode.INVALID_DATETIME_FORMAT
        )

    fun handleValidationConstraintViolation(exception: ConstraintViolationException): Response = ValidationError(
        errors = exception.constraintViolations.map { violation ->
            ValidationError.Violation(
                field = violation.propertyPath.toString(),
                value = violation.invalidValue,
                messageKey = violation.constraintDescriptor.annotation.annotationClass.simpleName,
                message = violation.message,
            )
        }
    ).let { Response.status(UnprocessableEntityStatus).entity(it).build() }
}

object PersistenceExceptionHandler {
    fun handleHibernateConstraintViolation(exception: HibernateConstraintViolationException): Response = exception
        .apply(logger::recordError)
        .buildGeneralErrorResponse(UnprocessableEntityStatus)

    fun handleEntityNotFound(exception: EntityNotFoundException): Response = exception
        .buildGeneralErrorResponse(Status.NOT_FOUND)
}

fun Logger.recordError(exception: Throwable) = this.error(exception.message)
fun Logger.recordWarning(exception: Throwable) = this.warn(exception.message)

fun Throwable.buildGeneralErrorResponse(status: StatusType): Response =
    Response.status(status)
        .entity(GeneralError(code = status.statusCode, message = message))
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        .build()

fun WebApplicationException.buildGeneralErrorResponse(): Response =
    this.buildGeneralErrorResponse(this.response.statusInfo)

fun StatusType.buildResponse(message: String? = null): Response =
    Response.status(this)
        .entity(GeneralError(code = statusCode, message = message))
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        .build()

fun UnprocessableEntityStatus.buildValidationErrorResponse(
    field: String,
    value: Any?,
    messageKey: String? = null,
    message: String? = null,
): Response {
    val error = ValidationError(
        errors = listOf(
            ValidationError.Violation(
                field = field,
                value = value,
                messageKey = messageKey,
                message = message,
            )
        )
    )

    return Response.status(this.statusCode)
        .entity(error)
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        .build()
}

object UnprocessableEntityStatus : StatusType {
    override fun getStatusCode(): Int = 422
    override fun getReasonPhrase(): String = "Unprocessable entity"
    override fun getFamily(): Status.Family = Status.Family.CLIENT_ERROR
}

fun List<JsonMappingException.Reference>.buildPath(): String =
    this.joinToString(separator = "") { pathPart ->
        when (pathPart.fieldName == null) {
            true -> "[${pathPart.index}]"
            false -> ".${pathPart.fieldName}"
        }
    }.removePrefix(".")
