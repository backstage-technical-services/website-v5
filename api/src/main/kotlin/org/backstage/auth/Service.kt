package org.backstage.auth

import org.backstage.users.UserRequest
import org.backstage.usergroups.UserGroupResponse

data class UserGroupList(
    val totalItems: Long,
    val groups: Map<String, String>,
)

interface AuthService {
    fun createUser(request: UserRequest.Create): String

    fun listUserGroups(pageIndex: Int, pageSize: Int): UserGroupList

    fun getUserGroup(id: String): UserGroupResponse.Default?
}
