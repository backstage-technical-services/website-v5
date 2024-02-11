package org.backstage.awards

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import org.backstage.util.SoftDeletableEntity
import org.hibernate.annotations.ResultCheckStyle
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import org.hibernate.envers.Audited

@ApplicationScoped
class AwardRepository : PanacheRepository<AwardEntity>

@Audited
@Entity
@Table(name = "awards")
@SQLDelete(sql = "update awards set deleted = true where id = ?", check = ResultCheckStyle.COUNT)
@SQLRestriction("deleted = false")
data class AwardEntity(
    @Column(name = "name", nullable = false)
    var name: String,

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    var description: String,

    @Column(name = "recurring", nullable = false)
    var recurring: Boolean,

    @Column(name = "suggested_by", nullable = true)
    var suggestedBy: String?,

    @Column(name = "approved", nullable = false)
    var approved: Boolean,
) : SoftDeletableEntity()

@Suppress("ForbiddenComment")
inline fun <reified T> AwardEntity.toClass(): T = when(T::class) {
    AwardResponse.Full::class -> AwardResponse.Full(
        id = id!!,
        name = name,
        description = description,
        recurring = recurring,
        suggestedBy = suggestedBy, // TODO: need to convert this to a name/object
        approved = approved
    ) as T
    else -> throw IllegalArgumentException("Cannot convert ${this::class} to ${T::class}")
}
