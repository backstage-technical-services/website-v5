package org.backstage.usergroups

import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.core.Response
import org.backstage.auth.AuthService
import org.backstage.errors.exceptionWithMessage
import org.backstage.util.PageInfo
import org.backstage.util.PaginatedResponse
import kotlin.math.ceil
import kotlin.math.min

interface UserGroupService {
    fun list(pageIndex: Int, pageSize: Int): PaginatedResponse<UserGroupResponse.Default>
    fun get(id: String): UserGroupResponse.Default
}

@ApplicationScoped
class AuthUserGroupService(
    private val authService: AuthService
) : UserGroupService {
    override fun list(pageIndex: Int, pageSize: Int): PaginatedResponse<UserGroupResponse.Default> {
        val pageSize = min(pageSize, MAX_PAGE_SIZE)

        val userGroups = authService.listUserGroups(pageIndex = pageIndex, pageSize = pageSize)

        return PaginatedResponse(
            page = PageInfo(
                pageIndex = pageIndex,
                pageSize = pageSize,
                totalItems = userGroups.totalItems,
                totalPages = ceil(userGroups.totalItems.toDouble() / pageSize).toInt(),
            ),
            items = userGroups.groups.map { (id, name) -> UserGroupResponse.Default(id = id, name = name) },
        )
    }

    override fun get(id: String): UserGroupResponse.Default = authService.getUserGroup(id)
        ?: throw Response.Status.NOT_FOUND exceptionWithMessage "Could not find user group with ID $id"

    companion object {
        const val MAX_PAGE_SIZE = 50
    }
}
