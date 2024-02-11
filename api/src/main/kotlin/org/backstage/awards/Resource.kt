package org.backstage.awards

import jakarta.annotation.security.RolesAllowed
import jakarta.validation.Valid
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.backstage.auth.Roles
import org.backstage.http.HttpHeaders

@Path("/award")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class AwardResource(
    private val service: AwardService
) {
    @GET
    @RolesAllowed(Roles.Awards.READ)
    fun list(
        @QueryParam("pageIndex") pageIndex: Int,
        @DefaultValue("20") @QueryParam("pageSize") pageSize: Int,
    ): Response = service.list(pageIndex = pageIndex, pageSize = pageSize)
        .let { awards -> Response.ok(awards).build() }

    @POST
    @RolesAllowed(Roles.Awards.CREATE)
    fun create(
        @Valid request: AwardRequest.Create
    ): Response = service.create(request)
        .let { awardId ->
            Response.noContent()
                .header(HttpHeaders.RESOURCE_ID, awardId)
                .build()
        }

    @POST
    @Path("/suggest")
    @RolesAllowed(Roles.Awards.SUGGEST)
    fun suggest(
        @Valid request: AwardRequest.Create
    ): Response = service.suggest(request)
        .let { awardId ->
            Response.noContent()
                .header(HttpHeaders.RESOURCE_ID, awardId)
                .build()
        }

    @GET
    @Path("/{id}")
    @RolesAllowed(Roles.Awards.READ, Roles.Awards.UPDATE)
    fun get(
        @PathParam("id") id: Long
    ): Response = service.get(id)
        .let { award -> Response.ok(award).build() }

    @PATCH
    @Path("/{id}")
    @RolesAllowed(Roles.Awards.UPDATE)
    fun update(
        @PathParam("id") id: Long,
        @Valid request: AwardRequest.Update
    ): Response {
        service.update(id, request)

        return Response.noContent().build()
    }

    @PATCH
    @Path("/{id}/approve")
    @RolesAllowed(Roles.Awards.APPROVE)
    fun approve(
        @PathParam("id") id: Long
    ): Response {
        service.approve(id)

        return Response.noContent().build()
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed(Roles.Awards.DELETE)
    fun delete(
        @PathParam("id") id: Long
    ): Response {
        service.delete(id)

        return Response.noContent().build()
    }
}
