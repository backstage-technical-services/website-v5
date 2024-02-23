package org.backstage.quotes

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.persistence.*
import org.backstage.users.UserEntity
import org.backstage.util.BaseEntity
import org.hibernate.envers.Audited
import java.time.LocalDateTime

@ApplicationScoped
class QuoteRepository : PanacheRepository<QuoteEntity>

@Audited
@Entity
@Table(name = "quotes")
data class QuoteEntity(
    @Column(name = "culprit", nullable = false)
    var culprit: String,

    @Column(name = "quote", nullable = false, columnDefinition = "TEXT")
    var quote: String,

    @Column(name = "date", nullable = false)
    var date: LocalDateTime,

    @ManyToOne(optional = false)
    @JoinColumn(name = "added_by_id")
    var addedBy: UserEntity,

    @Column(name = "rating", nullable = false)
    var rating: Int = 0,
) : BaseEntity() {
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "quote_id")
    var likes: MutableList<@JvmSuppressWildcards QuoteLikeEntity> = mutableListOf()
}

@Audited
@Entity
@Table(name = "quote_likes")
data class QuoteLikeEntity(
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    var type: QuoteLikeType,

    @ManyToOne
    @JoinColumn(name = "user_id")
    var user: UserEntity,
) : BaseEntity()
