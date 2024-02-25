package org.backstage.quotes

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.PastOrPresent
import org.backstage.util.PATTERN_DATETIME
import java.time.LocalDateTime

enum class QuoteVoteType(
    val weight: Int,
) { UPVOTE(1), DOWNVOTE(-1) }

enum class QuoteVoteAction { ADDED, CHANGED, REMOVED }

sealed class QuoteRequest {
    data class Create(
        @get:NotBlank
        @JsonProperty("culprit")
        val culprit: String,

        @get:NotBlank
        @JsonProperty("quote")
        val quote: String,

        @get:PastOrPresent
        @JsonFormat(pattern = PATTERN_DATETIME)
        @JsonProperty
        val date: LocalDateTime,
    ) : QuoteRequest()
}

sealed class QuoteResponse {
    data class Default(
        @JsonProperty("id")
        val id: Long,

        @JsonProperty("culprit")
        val culprit: String,

        @JsonProperty("quote")
        val quote: String,

        @JsonFormat(pattern = PATTERN_DATETIME)
        @JsonProperty("date")
        val date: LocalDateTime,

        @JsonProperty("rating")
        val rating: Int,

        @JsonProperty("userVote")
        val userVote: QuoteVoteType?,
    ) : QuoteResponse()

    data class Vote(
        @JsonProperty("action")
        val action: QuoteVoteAction
    ) : QuoteResponse()
}
