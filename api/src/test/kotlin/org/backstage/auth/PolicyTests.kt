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

private val USER_CORRECT = UUID.fromString("566e8ad1-f5e3-4062-93e4-541c961f0c98")
private val USER_INCORRECT = UUID.fromString("891832e6-ee0b-4085-870b-0cbba0a62671")

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
        val user = AuthHelpers.createMockedIdentity(userId = USER_CORRECT.toString())

        context("a singleton policy and an entity") {
            val policy = object : Policy<ExampleEntity>(user) {
                override fun authorise(action: Any, entity: ExampleEntity?) = when (action) {
                    "allow" -> allow()
                    "deny" -> deny()
                    "lambda-valid" -> authorise { true }
                    "lambda-invalid" -> authorise { false }
//                    "no-entity" -> authorise(entity) { true }
//                    "entity-lambda-valid" -> authorise(entity) { true }
//                    "entity-lambda-invalid" -> authorise(entity) { false }
//                    "author-interface" -> authorise(entity) { e -> user.isAuthor(e) }
//                    "author-lambda-valid" -> authorise(entity) { e -> user.isAuthor(e) { USER_CORRECT } }
//                    "author-lambda-invalid" -> authorise(entity) { e -> user.isAuthor(e) { USER_INCORRECT } }
                    else -> throw IllegalArgumentException("Unknown action '$action'")
                }
            }
            val entity = ExampleEntity(createdBy = USER_CORRECT)

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
            test("using using a lambda that requires an entity but no entity passed should throw an exception") {
                shouldThrow<UnauthorizedException> {
                    policy.authorise("no-entity", null)
                }
            }
            test("the entity passes the lambda should not throw an exception") {
                shouldNotThrow<UnauthorizedException> {
                    policy.authorise("entity-lambda-valid", entity)
                }
            }
            test("the entity does not pass the lambda should throw an exception") {
                shouldThrow<UnauthorizedException> {
                    policy.authorise("entity-lambda-invalid", entity)
                }
            }
            test("the hasAuthor lambda is successful should not throw an exception") {
                shouldNotThrow<UnauthorizedException> {
                    policy.authorise("author-lambda-valid", entity)
                }
            }
            test("the hasAuthor lambda is unsuccessful should throw an exception") {
                shouldThrow<UnauthorizedException> {
                    policy.authorise("author-lambda-invalid", entity)
                }
            }
            test("implementing hasAuthor and the entity has the correct author should not throw an exception") {
                shouldNotThrow<UnauthorizedException> {
                    policy.authorise("author-interface", entity)
                }
            }
            test("implementing hasAuthor and the entity does not have the correct author should throw an exception") {
                shouldThrow<UnauthorizedException> {
                    policy.authorise("author-interface", ExampleEntity(createdBy = USER_INCORRECT))
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
