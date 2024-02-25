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
    fun upvote(id: Long): QuoteResponse.Vote
    fun downvote(id: Long): QuoteResponse.Vote
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
        ) { quote -> QuoteResponseFactory.default(quote, getCurrentUser()) }

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
        .let { quote -> QuoteResponseFactory.default(quote, getCurrentUser()) }

    override fun upvote(id: Long) = updateVotes(id = id, type = QuoteVoteType.UPVOTE)
        .let(QuoteResponseFactory::vote)

    override fun downvote(id: Long) = updateVotes(id = id, type = QuoteVoteType.DOWNVOTE)
        .let(QuoteResponseFactory::vote)

    @Transactional
    fun updateVotes(id: Long, type: QuoteVoteType): QuoteVoteAction {
        val quote = repository.findByIdOrThrow(id)
        val user = userService.findByIdentityId(identityId = identity.getUserId())

        return when (val vote = quote.votes.firstOrNull { it.user.id == user.id }) {
            // First time voting => just add the vote
            null -> {
                QuoteVoteEntity(
                    type = type,
                    user = user,
                ).also { quote.votes.add(it) }
                quote.rating += type.weight
                LOGGER.info("User ${user.id} ${type.name.lowercase()}d quote $id")

                QuoteVoteAction.ADDED
            }

            else -> {
                val oldType = vote.type
                quote.rating -= oldType.weight

                when(vote.type) {
                    // Same type => remove vote
                    type -> {
                        quote.votes.remove(vote)
                        LOGGER.info("User ${user.id} removed their $oldType vote for quote $id")

                        QuoteVoteAction.REMOVED
                    }
                    // Different type => update to new type
                    else -> {
                        vote.type = type
                        quote.rating += type.weight
                        LOGGER.info("User ${user.id} changed their vote for quote $id from $oldType to $type")

                        QuoteVoteAction.CHANGED
                    }
                }
            }
        }
    }

    @Transactional
    override fun delete(id: Long) {
        val quote = repository.findById(id)

        if (quote != null) {
            repository.delete(quote)
            LOGGER.info("Deleted quote $id")
        } else {
            LOGGER.debug("Tried to delete quote $id but it doesn't exist")
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
