package org.backstage.usergroups

import com.fasterxml.jackson.annotation.JsonProperty

sealed class UserGroupResponse {
    data class Default(
        @JsonProperty("id")
        val id: String,

        @JsonProperty("name")
        val name: String,
    ) : UserGroupResponse()
}
