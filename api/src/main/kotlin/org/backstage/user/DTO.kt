package org.backstage.user

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

sealed class UserRequest {
    data class Create(
        @get:NotBlank
        @JsonProperty("username")
        val username: String,

        @get:NotBlank
        @get:Email
        @JsonProperty("email")
        val email: String,

        @get:NotBlank
        @JsonProperty("firstName")
        val firstName: String,

        @get:NotBlank
        @JsonProperty("lastName")
        val lastName: String,

        @get:NotBlank
        @JsonProperty("group")
        val group: String,
    ) : UserRequest()
}

sealed class UserResponse {
    data class Minimal(
        @JsonProperty("id")
        val id: Long,

        @JsonProperty("email")
        val email: String,

        @JsonProperty("name")
        val name: String,
    ) : UserResponse()
}
