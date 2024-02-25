package org.backstage.quotes

import jakarta.annotation.security.RolesAllowed
import jakarta.validation.Valid
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.backstage.auth.Roles
import org.backstage.http.HttpHeaders

@Path("/quote")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class QuoteResource(
    private val service: QuoteService,
) {
    @GET
    @RolesAllowed(Roles.Quotes.READ)
    fun list(
        @QueryParam("pageIndex") pageIndex: Int,
        @DefaultValue("20") @QueryParam("pageSize") pageSize: Int,
    ): Response = service.list(pageIndex = pageIndex, pageSize = pageSize)
        .let { quotes -> Response.ok(quotes).build() }

    @POST
    @RolesAllowed(Roles.Quotes.CREATE)
    fun create(
        @Valid request: QuoteRequest.Create,
    ): Response = service.create(request)
        .let { quoteId ->
            Response.noContent()
                .header(HttpHeaders.RESOURCE_ID, quoteId)
                .build()
        }

    @PATCH
    @Path("{id}/upvote")
    @RolesAllowed(Roles.Quotes.VOTE)
    fun upvote(
        @PathParam("id") id: Long,
    ): Response = service.upvote(id)
        .let { response -> Response.ok(response).build() }

    @PATCH
    @Path("{id}/downvote")
    @RolesAllowed(Roles.Quotes.VOTE)
    fun downvote(
        @PathParam("id") id: Long,
    ): Response = service.downvote(id)
        .let { response -> Response.ok(response).build() }

    @DELETE
    @Path("{id}")
    @RolesAllowed(Roles.Quotes.DELETE)
    fun delete(
        @PathParam("id") id: Long,
    ): Response = service.delete(id)
        .let { Response.noContent().build() }
}
