package org.backstage.user

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import org.backstage.util.SoftDeletableEntity
import org.hibernate.annotations.ColumnDefault
import org.hibernate.envers.Audited
import java.time.LocalDate

@ApplicationScoped
class UserRepository : PanacheRepository<UserEntity> {
    fun findByIdentityId(identityId: String): UserEntity? = find("identityId = ?", identityId).firstResult()
    fun existsByEmail(email: String): Boolean = count("email = ?1", email) > 0
    fun existsByUsername(username: String): Boolean = count("username = ?1", username) > 0
}

@Audited
@Entity
@Table(name = "users")
data class UserEntity(
    @Column(name = "identity_id", nullable = false, unique = true)
    var identityId: String,

    @Column(name = "username", nullable = false, unique = true)
    var username: String,

    @Column(name = "email", nullable = false, unique = true)
    var email: String,

    @Column(name = "first_name", nullable = false)
    var firstName: String,

    @Column(name = "last_name", nullable = false)
    var lastName: String,

    @Column(name = "nickname", nullable = true)
    var nickname: String? = null,

    @Column(name = "phone", nullable = true)
    var phone: String? = null,

    @Column(name = "address", nullable = true, columnDefinition = "TEXT")
    var address: String? = null,

    @Column(name = "tool_colours", nullable = true)
    var toolColours: String? = null,

    @Column(name = "date_of_birth", nullable = true)
    var dateOfBirth: LocalDate? = null,

    @ColumnDefault("false")
    @Column(name = "show_email", nullable = false)
    var showEmail: Boolean = false,

    @ColumnDefault("false")
    @Column(name = "show_phone", nullable = false)
    var showPhone: Boolean = false,

    @ColumnDefault("false")
    @Column(name = "show_address", nullable = false)
    var showAddress: Boolean = false,

    @ColumnDefault("false")
    @Column(name = "show_age", nullable = false)
    var showAge: Boolean = false,

    @Column(name = "group_id", nullable = false)
    var groupId: String,
) : SoftDeletableEntity()

inline fun <reified T> UserEntity.toClass(): T = when (T::class) {
    UserResponse.Minimal::class -> UserResponse.Minimal(
        id = id!!,
        email = email,
        name = "$firstName $lastName",
    ) as T
    else -> throw IllegalArgumentException("Cannot convert ${this::class} to ${T::class}")
}
