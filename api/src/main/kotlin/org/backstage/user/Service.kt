package org.backstage.user

import io.quarkus.panache.common.Page
import io.quarkus.panache.common.Sort
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR
import jakarta.ws.rs.core.Response.Status.NOT_FOUND
import org.backstage.auth.AuthService
import org.backstage.error.exceptionWithMessage
import org.backstage.usergroup.UserGroupService
import org.backstage.util.PaginatedResponse
import org.backstage.util.checkPageSize
import org.backstage.util.findPaginated
import org.slf4j.LoggerFactory

interface UserService {
    fun list(pageIndex: Int, pageSize: Int): PaginatedResponse<UserResponse.Minimal>
    fun create(request: UserRequest.Create): Long
    fun findByIdentityId(identityId: String): UserEntity
}

@ApplicationScoped
class RepositoryUserService(
    private val repository: UserRepository,
    private val authService: AuthService,
    private val userGroupService: UserGroupService,
) : UserService {
    override fun list(pageIndex: Int, pageSize: Int): PaginatedResponse<UserResponse.Minimal> = repository
        .findPaginated(
            page = Page.of(pageIndex, pageSize).checkPageSize(MAX_PAGE_SIZE),
            sort = Sort.ascending("firstName", "lastName"),
            converter = UserEntity::toClass,
        )

    override fun create(request: UserRequest.Create): Long = createUser(request)
        .also { LOGGER.info("Created user with ID $it") }

    override fun findByIdentityId(identityId: String): UserEntity = repository
        .findByIdentityId(identityId = identityId)
        ?: throw NOT_FOUND exceptionWithMessage "Could not find user for that identity ID"

    @Transactional
    fun createUser(request: UserRequest.Create): Long {
        if (repository.existsByUsername(request.username)) {
            throw Response.Status.CONFLICT exceptionWithMessage "A user with that username already exists"
        }
        if (repository.existsByEmail(request.email)) {
            throw Response.Status.CONFLICT exceptionWithMessage "A user with that email already exists"
        }

        val identityId = try {
            authService.createUser(request)
        } catch (e: Exception) {
            LOGGER.error("An error occurred when creating the user with the auth service: ${e.message}", e)
            throw INTERNAL_SERVER_ERROR exceptionWithMessage "An internal error occurred when creating the user"
        }

        val entity = UserEntity(
            identityId = identityId,
            username = request.username,
            email = request.email,
            firstName = request.firstName,
            lastName = request.lastName,
            groupId = userGroupService.get(request.group).id,
        )

        repository.persist(entity)

        return entity.id!!
    }

    companion object {
        const val MAX_PAGE_SIZE = 50
        private val LOGGER = LoggerFactory.getLogger(RepositoryUserService::class.java)
    }
}
