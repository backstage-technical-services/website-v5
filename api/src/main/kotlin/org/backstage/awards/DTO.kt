package org.backstage.awards

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank

sealed class AwardRequest {
    data class Create(
        @get:NotBlank
        @JsonProperty("name")
        val name: String,

        @get:NotBlank
        @JsonProperty("description")
        val description: String,

        @JsonProperty("recurring")
        val recurring: Boolean,
    ) : AwardRequest()

    data class Update(
        @get:NotBlank
        @JsonProperty("name")
        val name: String? = null,

        @get:NotBlank
        @JsonProperty("description")
        val description: String? = null,

        @JsonProperty("recurring")
        val recurring: Boolean? = null,
    ) : AwardRequest()
}

sealed class AwardResponse {
    data class Full(
        @JsonProperty("id")
        val id: Long,

        @JsonProperty("name")
        val name: String,

        @JsonProperty("description")
        val description: String?,

        @JsonProperty("recurring")
        val recurring: Boolean,

        @JsonProperty("suggestedBy")
        val suggestedBy: String?,

        @JsonProperty("approved")
        val approved: Boolean,
    ) : AwardResponse()
}
