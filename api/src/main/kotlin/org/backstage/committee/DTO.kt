package org.backstage.committee

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

sealed class CommitteeRequest {
    data class Update(
        @JsonProperty("roles")
        val roles: List<Role>,
    ) : CommitteeRequest() {
        data class Role(
            @JsonProperty("id")
            val id: Long? = null,

            @get:NotBlank
            @JsonProperty("name")
            val name: String,

            @get:NotBlank
            @JsonProperty("description")
            val description: String,

            @get:NotBlank
            @get:Email
            @JsonProperty("email")
            val email: String,

            @JsonProperty("userId")
            val userId: Long? = null,
        )
    }
}

sealed class CommitteeResponse {
    data class Default(
        @JsonProperty("roles")
        val roles: List<Role>,
    ) : CommitteeResponse() {
        data class Role(
            @JsonProperty("id")
            val id: Long,

            @JsonProperty("name")
            val name: String,

            @JsonProperty("description")
            val description: String,

            @JsonProperty("email")
            val email: String,

            @JsonProperty("user")
            val user: String?,
        )
    }
}
