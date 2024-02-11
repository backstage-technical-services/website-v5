package org.backstage.auth

import io.quarkus.security.UnauthorizedException
import io.quarkus.security.identity.SecurityIdentity
import org.backstage.util.BaseEntity

interface HasAuthor {
    val authorId: Any
}

private fun deny() {
    throw UnauthorizedException()
}

/**
 * Determines whether the current user is authorised based on the result of a given [fn] which takes no arguments.
 * This is useful for determining access for actions with no entity to check, or where the entity does not dictate
 * access permissions.
 */
fun authorise(fn: () -> Boolean) {
    if (!fn()) {
        deny()
    }
}

/**
 * Determines whether the current user is authorised based on the result of a given [fn], which depends on a given
 * [entity]. A use-case for this is only allowing the original author to modify the [entity]. Access is also denied
 * if no [entity] is provided.
 */
fun <T : BaseEntity> authorise(entity: T?, fn: (entity: T) -> Boolean) {
    if (entity == null || !fn(entity)) {
        deny()
    }
}

/**
 * Determines whether the current user is authorised based on the result of a given [fn], which takes no arguments, and
 * execute [onAuthorised] if they are. This method should throw an [UnauthorizedException] if the action is not allowed.
 * If the authorisation needs to inspect the current user it can use an injected [SecurityIdentity].
 */
fun <T> authoriseAndDo(fn: () -> Boolean, onAuthorised: () -> T): T {
    authorise(fn = fn)

    return onAuthorised()
}

/**
 * Determines whether the current user is authorised based on the result of a given [fn], which may depend on a given
 * [entity], and execute [onAuthorised] if they are. This method should throw an [UnauthorizedException] if the action
 * is not allowed. If the authorisation needs to inspect the current user it can use an injected [SecurityIdentity].
 */
fun <T, U : BaseEntity> authoriseAndDo(fn: (U) -> Boolean, entity: U?, onAuthorised: () -> T): T {
    authorise(entity = entity, fn = fn)

    return onAuthorised()
}

/**
 * Role-Based Access Control (RBAC) does not provide a way to determine whether a given action is authorised if the
 * authorisation logic depends on the entity being accessed. For example, events should only be editable by admins or
 * members with a TEM role.
 *
 * This class provides abstraction over the basic handling of authorisation while allowing each domain to dictate what
 * the specific logic for determining access should be, for an entity of type [T]. This class can be extended and then
 * injected into the service layer.
 *
 * ```
 * @ApplicationScoped
 * class ExamplePolicy(private val identity: SecurityIdentity) : Policy<ExampleEntity>() {
 *      override fun authorise(action: Any, entity: ExampleEntity? = null) = when (action) {
 *          "create" -> allow()
 *          "edit" -> authorise { example -> identity.isAuthor(example) }
 *          else -> deny()
 *      }
 * }
 * ```
 *
 * Alternatively, this can be implemented using a singleton object within the service layer.
 *
 * ```
 * private val policy = object : Policy<ExampleEntity>() {
 *      override fun authorise(action: Any, entity: ExampleEntity? = null) = when (action) {
 *          "create" -> allow()
 *          "edit" -> authorise { example -> identity.isAuthor(example) }
 *          else -> deny()
 *      }
 * }
 * ```
 */
abstract class Policy<T>(private val identity: SecurityIdentity) {
    protected fun allow() = Unit
    protected fun deny() {
        throw UnauthorizedException()
    }

    /**
     * Determine whether to authorise a given [action], which may depend on a given [entity]. This method should throw
     * an [UnauthorizedException] if the action is not allowed, and do nothing if it is. If the authorisation needs to
     * inspect the current user it can use an injected [SecurityIdentity].
     */
    abstract fun authorise(action: Any, entity: T? = null)
}

/**
 * Determines whether the current [SecurityIdentity] is the author of a given [entity], using a [fn] to determine
 * the ID of the author.
 */
fun <T : BaseEntity> SecurityIdentity.isAuthor(entity: T?, fn: (entity: T) -> Any) = when (entity) {
    null -> false
    else -> fn(entity).toString() == this.getUserIdOrNull()
}

/**
 * If the given [entity] implements the [HasAuthor] interface this method offers an alternative way to determine
 * whether the current [SecurityIdentity] is the author, using the [HasAuthor.authorId] property.
 */
fun SecurityIdentity.isAuthor(entity: HasAuthor?) = when (entity) {
    null -> false
    else -> entity.authorId.toString() == this.getUserIdOrNull()
}
