package org.backstage.auth

import jakarta.enterprise.context.ApplicationScoped
import org.backstage.users.UserRequest
import org.backstage.usergroup.UserGroupResponse
import java.util.*

@ApplicationScoped
class NoopAuthService : AuthService {
    override fun createUser(request: UserRequest.Create): String = UUID.randomUUID().toString()

    override fun listUserGroups(pageIndex: Int, pageSize: Int): UserGroupList =
        UserGroupList(totalItems = 0, groups = emptyMap())

    override fun getUserGroup(id: String): UserGroupResponse.Default = UserGroupResponse.Default(
        id = id,
        name = "Example User Group",
    )
}
