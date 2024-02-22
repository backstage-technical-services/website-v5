package org.backstage.quotes

import io.quarkus.panache.common.Page
import io.quarkus.panache.common.Sort
import io.quarkus.security.identity.SecurityIdentity
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import org.backstage.auth.getUserId
import org.backstage.users.UserService
import org.backstage.util.PaginatedResponse
import org.backstage.util.checkPageSize
import org.backstage.util.findByIdOrThrow
import org.backstage.util.findPaginated
import org.slf4j.LoggerFactory

interface QuoteService {
    fun list(pageIndex: Int, pageSize: Int): PaginatedResponse<QuoteResponse.Default>
    fun create(request: QuoteRequest.Create): Long
    fun like(id: Long)
    fun dislike(id: Long)
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
            converter = QuoteEntity::toClass,
        )

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

    override fun like(id: Long) = updateLikes(id = id, type = QuoteLikeType.LIKE)

    override fun dislike(id: Long) = updateLikes(id = id, type = QuoteLikeType.DISLIKE)

    @Transactional
    fun updateLikes(id: Long, type: QuoteLikeType) {
        val quote = repository.findByIdOrThrow(id)
        val user = userService.findByIdentityId(identityId = identity.getUserId())

        when(val like = quote.likes.firstOrNull { it.user.id == user.id }) {
            null -> {
                QuoteLikeEntity(
                    type = type,
                    user = user,
                ).also { quote.likes.add(it) }
            }
            else -> {
                like.type = type
            }
        }

        when(type) {
            QuoteLikeType.LIKE -> LOGGER.info("User with ID ${user.id} liked quote with ID $id")
            QuoteLikeType.DISLIKE -> LOGGER.info("User with ID ${user.id} disliked quote with ID $id")
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

    companion object {
        const val MAX_PAGE_SIZE = 50
        private val LOGGER = LoggerFactory.getLogger(RepositoryQuoteService::class.java)
    }
}
