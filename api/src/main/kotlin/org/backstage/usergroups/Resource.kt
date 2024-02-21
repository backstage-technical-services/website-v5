package org.backstage.usergroups

import jakarta.annotation.security.RolesAllowed
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.backstage.auth.Roles

@Path("/user-group")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class UserGroupResource(
    private val service: UserGroupService,
) {
    @GET
    @RolesAllowed(Roles.UserGroups.READ)
    fun list(
        @QueryParam("pageIndex") pageIndex: Int,
        @DefaultValue("20") @QueryParam("pageSize") pageSize: Int,
    ): Response = service.list(pageIndex = pageIndex, pageSize = pageSize)
        .let { userGroups -> Response.ok(userGroups).build() }
}
