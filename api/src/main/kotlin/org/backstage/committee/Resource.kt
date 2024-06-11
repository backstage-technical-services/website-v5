package org.backstage.committee

import jakarta.annotation.security.RolesAllowed
import jakarta.validation.Valid
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.backstage.auth.Roles

@Path("/committee")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class CommitteeResource(
    private val service: CommitteeService,
) {
    @GET
//    @RolesAllowed(Roles.Committee.READ)
    fun list(): Response = service.list()
        .let { response -> Response.ok(response).build() }

    @PUT
    @RolesAllowed(Roles.Committee.EDIT)
    fun update(
        @Valid request: CommitteeRequest.Update,
    ): Response = service.update(request)
        .let { Response.noContent().build() }
}
