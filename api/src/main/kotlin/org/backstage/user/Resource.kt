package org.backstage.user

import jakarta.annotation.security.RolesAllowed
import jakarta.validation.Valid
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.backstage.auth.Roles
import org.backstage.http.HttpHeaders

@Path("/user")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class UserResource(
    private val service: UserService,
) {
    @GET
    @RolesAllowed(Roles.Users.READ)
    fun list(
        @QueryParam("pageIndex") pageIndex: Int,
        @DefaultValue("20") @QueryParam("pageSize") pageSize: Int,
    ): Response = service.list(pageIndex = pageIndex, pageSize = pageSize)
        .let { users -> Response.ok(users).build() }

    @POST
    @RolesAllowed(Roles.Users.CREATE)
    fun create(
        @Valid request: UserRequest.Create,
    ): Response = service.create(request)
        .let { userId ->
            Response.noContent()
                .header(HttpHeaders.RESOURCE_ID, userId)
                .build()
        }
}
