package org.backstage.committee

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.persistence.*
import org.backstage.users.UserEntity
import org.backstage.util.BaseEntity
import org.hibernate.envers.Audited

@ApplicationScoped
class CommitteeRepository : PanacheRepository<CommitteeRoleEntity>

@Audited
@Entity
@Table(name = "committee_roles")
data class CommitteeRoleEntity(
    @Column(name = "name", nullable = false)
    var name: String,

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    var description: String,

    @Column(name = "email", nullable = false)
    var email: String,

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    var user: UserEntity?,

    @Column(name = "order", nullable = false, unique = true)
    var order: Int,
) : BaseEntity()
