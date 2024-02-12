package org.backstage.auth

import com.auth0.client.auth.AuthAPI
import com.auth0.client.mgmt.ManagementAPI
import com.auth0.client.mgmt.filter.RolesFilter
import com.auth0.exception.APIException
import com.auth0.json.mgmt.roles.Role
import io.quarkus.arc.profile.IfBuildProfile
import io.quarkus.cache.CacheResult
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.context.Dependent
import jakarta.enterprise.context.RequestScoped
import jakarta.enterprise.inject.Produces
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
        private val LOGGER = LoggerFactory.getLogger(Auth0Config::class.java)
    }
}

@ApplicationScoped
@IfBuildProfile(anyOf = ["prod", "dev"])
class Auth0AuthService(
    private val managementAPI: ManagementAPI,
) : AuthService {
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
        private val LOGGER = LoggerFactory.getLogger(Auth0AuthService::class.java)
    }
}

private fun Role.toGroup() = UserGroupResponse.Default(id = id, name = name)
