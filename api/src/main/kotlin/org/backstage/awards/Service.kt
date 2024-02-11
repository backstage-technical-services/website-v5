package org.backstage.awards

import io.quarkus.panache.common.Page
import io.quarkus.panache.common.Sort
import io.quarkus.security.identity.SecurityIdentity
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import org.backstage.auth.getUserId
import org.backstage.util.*
import org.slf4j.LoggerFactory

interface AwardService {
    fun list(pageIndex: Int, pageSize: Int): PaginatedResponse<AwardResponse.Full>
    fun get(id: Long): AwardResponse.Full
    fun create(request: AwardRequest.Create): Long
    fun suggest(request: AwardRequest.Create): Long
    fun update(id: Long, request: AwardRequest.Update)
    fun delete(id: Long)
    fun approve(id: Long)
}

@ApplicationScoped
class RepositoryAwardService(
    private val repository: AwardRepository,
    private val identity: SecurityIdentity
) : AwardService {
    override fun list(pageIndex: Int, pageSize: Int): PaginatedResponse<AwardResponse.Full> = repository
        .findPaginated(
            page = Page.of(pageIndex, pageSize).checkPageSize(MAX_PAGE_SIZE),
            sort = Sort.ascending("name"),
            converter = AwardEntity::toClass,
        )

    override fun create(request: AwardRequest.Create): Long = createAward(request = request, asSuggestion = false)
        .also { LOGGER.info("Created award with ID $it") }

    override fun suggest(request: AwardRequest.Create): Long = createAward(request = request, asSuggestion = true)
        .also { LOGGER.info("Created award suggestion with ID $it") }

    @Transactional
    fun createAward(request: AwardRequest.Create, asSuggestion: Boolean): Long {
        val entity = AwardEntity(
            name = request.name,
            description = request.description,
            recurring = request.recurring,
            suggestedBy = when (asSuggestion) {
                true -> identity.getUserId()
                else -> null
            },
            approved = !asSuggestion,
        )

        repository.persist(entity)

        return entity.id!!
    }

    override fun get(id: Long): AwardResponse.Full = repository.findByIdOrThrow(id).toClass()

    @Transactional
    override fun update(id: Long, request: AwardRequest.Update) = repository.findByIdOrThrow(id)
        .modify(request)
        .also { LOGGER.info("Updated award with ID $id") }

    @Transactional
    override fun approve(id: Long) {
        val award = repository.findByIdOrThrow(id)

        if (!award.approved) {
            award.approved = true

            LOGGER.info("Marked award with ID $id as approved")
        }
    }

    @Transactional
    override fun delete(id: Long) {
        val award = repository.findById(id)

        if (award != null) {
            repository.delete(award)
            LOGGER.info("Deleted award with ID $id")
        } else {
            LOGGER.debug("Tried to delete award with ID $id but it doesn't exist")
        }
    }

    companion object {
        const val MAX_PAGE_SIZE = 50
        private val LOGGER = LoggerFactory.getLogger(RepositoryAwardService::class.java)
    }
}

fun AwardEntity.modify(request: AwardRequest.Update) {
    request.name?.run { name = this }
    request.description?.run { description = this }
    request.recurring?.run { recurring = this }
}
