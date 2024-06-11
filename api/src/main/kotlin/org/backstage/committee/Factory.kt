package org.backstage.committee

object CommitteeResponseFactory {
    fun default(roles: List<CommitteeRoleEntity>) = CommitteeResponse.Default(
        roles = roles.map { role ->
            CommitteeResponse.Default.Role(
                id = role.id!!,
                name = role.name,
                description = role.description,
                email = role.email,
                user = role.user?.let { "${it.firstName} ${it.lastName}" },
            )
        }
    )
}
