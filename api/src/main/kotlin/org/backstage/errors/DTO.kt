package org.backstage.errors

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import org.backstage.util.PATTERN_DATETIME
import java.time.LocalDateTime

data class ValidationError(
    @JsonProperty("timestamp")
    @JsonFormat(pattern = PATTERN_DATETIME)
    val timestamp: LocalDateTime = LocalDateTime.now(),

    @JsonProperty("code")
    val code: Any = UnprocessableEntityStatus.statusCode,

    @JsonProperty("errors")
    val errors: List<Violation>,
) {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    data class Violation(
        @JsonProperty("field")
        val field: String,

        @JsonProperty("value")
        val value: Any?,

        @JsonProperty("messageKey")
        val messageKey: String? = null,

        @JsonProperty("message")
        val message: String? = null,
    )
}

@JsonInclude(JsonInclude.Include.NON_NULL)
data class GeneralError(
    @JsonProperty("timestamp")
    @JsonFormat(pattern = PATTERN_DATETIME)
    val timestamp: LocalDateTime = LocalDateTime.now(),

    @JsonProperty("code")
    val code: Any,

    @JsonProperty("message")
    val message: String?
)
