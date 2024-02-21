package org.backstage.auth

import com.auth0.client.auth.AuthAPI
import com.auth0.client.mgmt.ManagementAPI
import com.auth0.client.mgmt.filter.RolesFilter
import com.auth0.client.mgmt.filter.UserFilter
import com.auth0.exception.APIException
import com.auth0.json.mgmt.roles.Role
import com.auth0.json.mgmt.users.User
import io.quarkus.arc.profile.IfBuildProfile
import io.quarkus.cache.CacheResult
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.context.Dependent
import jakarta.enterprise.context.RequestScoped
import jakarta.enterprise.inject.Produces
import org.apache.commons.lang3.RandomStringUtils
import org.backstage.users.UserRequest
import org.backstage.usergroup.UserGroupResponse
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.slf4j.LoggerFactory
import java.net.URL

@Dependent
@IfBuildProfile(anyOf = ["prod", "dev"])
class Auth0Config(
    @ConfigProperty(name = "quarkus.oidc.auth-server-url") private val authServerUrl: String,
    @ConfigProperty(name = "quarkus.oidc.client-id") private val clientId: String,
    @ConfigProperty(name = "quarkus.oidc.credentials.secret") private val clientSecret: String,
) {
    private val auth0Domain = URL(authServerUrl).host

    @Produces
    @ApplicationScoped
    fun authApi(): AuthAPI = AuthAPI.newBuilder(auth0Domain, clientId, clientSecret).build()

    @Produces
    @RequestScoped
    fun managementApi(
        authAPI: AuthAPI,
    ): ManagementAPI {
        LOGGER.debug("Fetching access token for management API")
        val accessToken = authAPI.requestToken("$authServerUrl/api/v2/")
            .execute()
            .body
            .accessToken

        return ManagementAPI.newBuilder(auth0Domain, accessToken).build()
    }

    companion object {
        const val USER_CONNECTION = "Username-Password-Authentication"
        private val LOGGER = LoggerFactory.getLogger(Auth0Config::class.java)
    }
}

@ApplicationScoped
@IfBuildProfile(anyOf = ["prod", "dev"])
class Auth0AuthService(
    private val managementAPI: ManagementAPI,
) : AuthService {
    private fun generatePassword(): CharArray = RandomStringUtils
        .randomAlphanumeric(PASSWORD_LENGTH)
        .toCharArray()

    override fun createUser(request: UserRequest.Create): String {
        LOGGER.debug("Checking if user exists in Auth0 with email ${request.email}")
        val existingUser = UserFilter()
            .withQuery("email:${request.email}")
            .let { filter -> managementAPI.users().list(filter) }
            .execute()
            .body
            .items
            .firstOrNull()

        return when (existingUser) {
            null -> {
                User()
                    .apply {
                        setConnection(Auth0Config.USER_CONNECTION)
                        nickname = request.username
                        email = request.email
                        isEmailVerified = true
                        name = "${request.firstName} ${request.lastName}"
                        givenName = request.firstName
                        familyName = request.lastName
                        setPassword(generatePassword())
                    }
                    .let { user -> managementAPI.users().create(user) }
                    .execute()
                    .body
                    .id
                    .also { LOGGER.debug("Created user with ID $it in Auth0") }
                    .also { userId ->
                        managementAPI.users()
                            .addRoles(userId, listOf(request.group))
                            .execute()
                            .also { LOGGER.debug("Added role ${request.group} to user $userId") }
                    }
            }

            else -> {
                LOGGER.debug("User with ID ${existingUser.id} already exists in Auth0")
                existingUser.id
            }
        }
    }

    @CacheResult(cacheName = "auth0-roles")
    override fun listUserGroups(pageIndex: Int, pageSize: Int): UserGroupList {
        val filter = RolesFilter()
            .withTotals(true)
            .withPage(pageIndex, pageSize)

        val rolesResponse = managementAPI
            .roles()
            .list(filter)
            .execute()
            .body

        LOGGER.debug("Fetched ${rolesResponse.items.size} roles from Auth0 for page $pageIndex and size $pageSize")

        return UserGroupList(
            totalItems = rolesResponse.total.toLong(),
            groups = rolesResponse.items.associate { role -> role.id to role.name }
        )
    }

    @CacheResult(cacheName = "auth0-role")
    override fun getUserGroup(id: String): UserGroupResponse.Default? = try {
        managementAPI.roles()
            .get(id)
            .execute()
            .body
            .let(Role::toGroup)
    } catch (e: APIException) {
        LOGGER.error("Could not fetch role with ID $id from Auth0: ${e.message}", e)
        null
    }

    companion object {
        private const val PASSWORD_LENGTH = 20
        private val LOGGER = LoggerFactory.getLogger(Auth0AuthService::class.java)
    }
}

private fun Role.toGroup() = UserGroupResponse.Default(id = id, name = name)
