package org.backstage.util

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import io.quarkus.panache.common.Page
import io.quarkus.panache.common.Sort
import io.quarkus.security.identity.SecurityIdentity
import jakarta.enterprise.inject.spi.CDI
import jakarta.persistence.*
import jakarta.ws.rs.core.Response
import org.apache.commons.lang3.StringUtils
import org.backstage.auth.getUserId
import org.backstage.error.exceptionWithMessage
import org.hibernate.annotations.ColumnDefault
import org.hibernate.envers.DefaultRevisionEntity
import org.hibernate.envers.RevisionEntity
import org.hibernate.envers.RevisionListener
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

private val LOGGER: Logger = LoggerFactory.getLogger(BaseEntity::class.java)

inline fun <reified E : BaseEntity> KClass<E>.formatClassName(): String =
    this.java.simpleName
        .removeSuffix("Entity")
        .let(StringUtils::splitByCharacterTypeCamelCase)
        .joinToString(separator = " ")
        .lowercase()

inline fun <reified E : BaseEntity> PanacheRepository<E>.findByIdOrThrow(id: Long): E = findById(id)
    ?: throw Response.Status.NOT_FOUND exceptionWithMessage "Could not find ${E::class.formatClassName()} with ID $id"

fun <E : BaseEntity, R : Any> PanacheRepository<E>.findPaginated(
    page: Page,
    sort: Sort? = null,
    converter: (entity: E) -> R
): PaginatedResponse<R> {
    val query = when (sort) {
        null -> this.findAll()
        else -> this.findAll(sort)
    }.page(page)

    return PaginatedResponse(
        page = PageInfo(
            pageIndex = query.page().index,
            pageSize = query.page().size,
            totalPages = query.pageCount(),
            totalItems = query.count()
        ),
        items = query.list().map(converter)
    )
}

fun Page.checkPageSize(maxPageSize: Int): Page = when (this.size > maxPageSize) {
    true -> {
        LOGGER.debug("Requested page size (${this.size}) is greater than allowed maximum ($maxPageSize).")
        Page.of(this.index, maxPageSize)
    }

    false -> this
}

fun <E : SoftDeletableEntity> PanacheRepository<E>.softDelete(entity: E) {
    entity.deleted = true
    this.persistAndFlush(entity)
}

@MappedSuperclass
abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    open var id: Long? = null

    fun hasIdSet(): Boolean = id !== null
}

@MappedSuperclass
abstract class SoftDeletableEntity(
    @Suppress("ForbiddenComment")
    @ColumnDefault("false") // TODO: remove when we move to liquibase
    @Column(name = "deleted", nullable = false)
    open var deleted: Boolean = false
) : BaseEntity()

@Entity
@Table(name = "revision_info")
@RevisionEntity(RevisionInfoEntityListener::class)
class RevisionInfoEntity(
    @Column(name = "user_id", nullable = false)
    var userId: String
) : DefaultRevisionEntity()

class RevisionInfoEntityListener : RevisionListener {
    private val identity = CDI.current().select(SecurityIdentity::class.java).get()

    override fun newRevision(revisionEntity: Any?) {
        (revisionEntity as? RevisionInfoEntity)?.apply {
            userId = identity.getUserId()
        }
    }
}
