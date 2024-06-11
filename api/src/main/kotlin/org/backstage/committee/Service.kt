package org.backstage.committee

import io.quarkus.panache.common.Sort
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import org.backstage.users.UserRepository
import org.slf4j.LoggerFactory

interface CommitteeService {
    fun list(): CommitteeResponse.Default
    fun update(request: CommitteeRequest.Update)
}

@ApplicationScoped
class RepositoryCommitteeService(
    private val repository: CommitteeRepository,
    private val userRepository: UserRepository,
) : CommitteeService {
    override fun list(): CommitteeResponse.Default = repository
        .listAll(Sort.ascending("order"))
        .let(CommitteeResponseFactory::default)

    @Transactional
    override fun update(request: CommitteeRequest.Update) {
        val roleIds = request.roles.mapIndexed { index, role ->
            val order = index + 1
            val user = role.userId?.let { userId -> userRepository.findById(userId) }

            when (val roleEntity = role.id?.let { roleId -> repository.findById(roleId) }) {
                null -> CommitteeRoleEntity(
                    name = role.name,
                    description = role.description,
                    email = role.email,
                    user = user,
                    order = order,
                ).let { it.id!! }
                    .also { LOGGER.debug("Created a new committee role (${role.name}) with ID $it") }

                else -> {
                    LOGGER.debug("Updating committee role with ID ${role.id}")
                    roleEntity.apply {
                        this.name = role.name
                        this.description = role.description
                        this.email = role.email
                        this.user = user
                        this.order = order
                    }.id!!
                }
            }
        }


        LOGGER.debug("Deleting any orphaned committee roles")
        repository.delete("id not in ?1", roleIds)
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(RepositoryCommitteeService::class.java)
    }
}
