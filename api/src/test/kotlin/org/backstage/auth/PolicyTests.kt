package org.backstage.auth

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntity
import io.quarkus.security.UnauthorizedException
import io.quarkus.security.identity.SecurityIdentity
import io.quarkus.security.runtime.QuarkusPrincipal
import io.quarkus.test.junit.QuarkusTest
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.backstage.AuthHelpers
import org.junit.jupiter.api.Test
import java.util.*

private val USER_ID = UUID.fromString("566e8ad1-f5e3-4062-93e4-541c961f0c98")

@QuarkusTest
class PolicyInjectionTests {
    @Inject
    lateinit var injectedPolicy: ExampleInjectedPolicy

    @Test
    fun `policies that extend the abstract policy should be injectable`() {
        injectedPolicy.shouldNotBeNull()
    }
}

class SingletonPolicyTests : FunSpec() {
    init {
        val user = AuthHelpers.createMockedIdentity(userId = USER_ID.toString())

        context("a singleton policy and an entity") {
            val policy = object : Policy<ExampleEntity>(user) {
                override fun authorise(action: Any, entity: ExampleEntity?) = when (action) {
                    "allow" -> allow()
                    "deny" -> deny()
                    "lambda-valid" -> authorise { true }
                    "lambda-invalid" -> authorise { false }
                    else -> throw IllegalArgumentException("Unknown action '$action'")
                }
            }

            test("using an action that allows should not throw an exception") {
                shouldNotThrow<UnauthorizedException> {
                    policy.authorise("allow")
                }
            }
            test("using an action that denies should throw an exception") {
                shouldThrow<UnauthorizedException> {
                    policy.authorise("deny")
                }
            }
            test("using an action that uses a successful lambda should not throw an exception") {
                shouldNotThrow<UnauthorizedException> {
                    policy.authorise("lambda-valid")
                }
            }
            test("using an action that uses an unsuccessful lambda should throw an exception") {
                shouldThrow<UnauthorizedException> {
                    policy.authorise("lambda-invalid")
                }
            }
        }
    }
}

class UserIdTests : FunSpec() {
    init {
        context("an anonymous identity") {
            val identity = mockk<SecurityIdentity> {
                every { isAnonymous } returns true
            }

            test("getting the user ID should return null") {
                identity.getUserIdOrNull().shouldBeNull()
            }
        }

        context("an OIDC principal") {
            val identity = AuthHelpers.createMockedIdentity()

            test("getting the user ID should return the correct value") {
                identity.getUserIdOrNull() shouldBe AuthHelpers.DEFAULT_USER_ID
            }
        }

        context("a quarkus principal") {
            val user = mockk<SecurityIdentity> {
                every { isAnonymous } returns false
                every { principal } returns QuarkusPrincipal(AuthHelpers.DEFAULT_USER_ID)
            }

            test("getting the user ID should return the correct value") {
                user.getUserIdOrNull() shouldBe AuthHelpers.DEFAULT_USER_ID
            }
        }
    }
}

@Singleton
class ExampleInjectedPolicy(identity: SecurityIdentity) : Policy<ExampleEntity>(identity) {
    override fun authorise(action: Any, entity: ExampleEntity?) = allow()
}

class ExampleEntity(
    var createdBy: UUID
) : PanacheEntity(), HasAuthor {
    override val authorId = createdBy
}
