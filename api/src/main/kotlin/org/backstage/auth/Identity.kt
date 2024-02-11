package org.backstage.auth

import io.quarkus.oidc.runtime.OidcJwtCallerPrincipal
import io.quarkus.security.identity.SecurityIdentity

fun SecurityIdentity.getUserId(): String = this.getUserIdOrNull()
    ?: error("Could not determine ID of user $this")

fun SecurityIdentity.getUserIdOrNull(): String? = when (isAnonymous) {
    true -> null
    false -> when (val principal = this.principal) {
        null -> null
        is OidcJwtCallerPrincipal -> principal.subject
        else -> principal.name
    }
}
