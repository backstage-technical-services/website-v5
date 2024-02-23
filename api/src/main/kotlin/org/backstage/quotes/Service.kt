package org.backstage.quotes

import io.quarkus.panache.common.Page
import io.quarkus.panache.common.Sort
import io.quarkus.security.identity.SecurityIdentity
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import org.backstage.auth.getUserId
import org.backstage.auth.getUserIdOrNull
import org.backstage.users.UserService
import org.backstage.util.PaginatedResponse
import org.backstage.util.checkPageSize
import org.backstage.util.findByIdOrThrow
import org.backstage.util.findPaginated
import org.slf4j.LoggerFactory

interface QuoteService {
    fun list(pageIndex: Int, pageSize: Int): PaginatedResponse<QuoteResponse.Default>
    fun create(request: QuoteRequest.Create): Long
    fun get(id: Long): QuoteResponse.Default
    fun upvote(id: Long)
    fun downvote(id: Long)
    fun delete(id: Long)
}

@ApplicationScoped
class RepositoryQuoteService(
    private val repository: QuoteRepository,
    private val userService: UserService,
    private val identity: SecurityIdentity,
) : QuoteService {
    override fun list(pageIndex: Int, pageSize: Int): PaginatedResponse<QuoteResponse.Default> = repository
        .findPaginated(
            page = Page.of(pageIndex, pageSize).checkPageSize(MAX_PAGE_SIZE),
            sort = Sort.descending("date"),
        ) { quote -> QuoteConverter.toDefaultResponse(quote, getCurrentUser()) }

    override fun create(request: QuoteRequest.Create): Long = createQuote(request)
        .also { LOGGER.info("Created quote with ID $it") }

    @Transactional
    fun createQuote(request: QuoteRequest.Create): Long {
        val entity = QuoteEntity(
            culprit = request.culprit,
            quote = request.quote,
            date = request.date,
            addedBy = userService.findByIdentityId(identityId = identity.getUserId()),
        )

        repository.persist(entity)

        return entity.id!!
    }

    override fun get(id: Long): QuoteResponse.Default = repository
        .findByIdOrThrow(id)
        .let { quote -> QuoteConverter.toDefaultResponse(quote, getCurrentUser()) }

    override fun upvote(id: Long) = updateVotes(id = id, type = QuoteVoteType.UPVOTE)

    override fun downvote(id: Long) = updateVotes(id = id, type = QuoteVoteType.DOWNVOTE)

    @Transactional
    fun updateVotes(id: Long, type: QuoteVoteType) {
        val quote = repository.findByIdOrThrow(id)
        val user = userService.findByIdentityId(identityId = identity.getUserId())

        when (val vote = quote.votes.firstOrNull { it.user.id == user.id }) {
            null -> {
                QuoteVoteEntity(
                    type = type,
                    user = user,
                ).also { quote.votes.add(it) }
            }

            else -> {
                quote.rating -= vote.type.weight
                vote.type = type
            }
        }

        quote.rating += type.weight

        when (type) {
            QuoteVoteType.UPVOTE -> LOGGER.info("User with ID ${user.id} upvoted quote with ID $id")
            QuoteVoteType.DOWNVOTE -> LOGGER.info("User with ID ${user.id} downvoted quote with ID $id")
        }
    }

    @Transactional
    override fun delete(id: Long) {
        val quote = repository.findById(id)

        if (quote != null) {
            repository.delete(quote)
            LOGGER.info("Deleted quote with ID $id")
        } else {
            LOGGER.debug("Tried to delete quote with ID $id but it doesn't exist")
        }
    }

    private fun getCurrentUser() = identity
        .getUserIdOrNull()
        ?.let { identityId -> userService.findByIdentityId(identityId) }

    companion object {
        const val MAX_PAGE_SIZE = 50
        private val LOGGER = LoggerFactory.getLogger(RepositoryQuoteService::class.java)
    }
}
