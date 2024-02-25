package org.backstage

import io.github.serpro69.kfaker.Faker
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.every
import io.mockk.mockk
import io.quarkus.oidc.runtime.OidcJwtCallerPrincipal
import io.quarkus.security.identity.SecurityIdentity
import jakarta.transaction.Transactional
import jakarta.ws.rs.WebApplicationException
import jakarta.ws.rs.core.Response
import org.backstage.util.BaseEntity
import org.backstage.util.PageInfo
import java.security.Principal

val faker = Faker()

fun Long?.shouldBeValidId() {
    shouldNotBeNull()
    shouldBeGreaterThan(0)
}

object AuthHelpers {
    const val DEFAULT_USER_ID = "TEST_USER"
    const val EXAMPLE_IDENTITY_ID = "auth0|64de9e01a5bc96abe1c9f5cf"

    fun createMockedPrincipal(userId: String = DEFAULT_USER_ID): Principal =
        mockk<OidcJwtCallerPrincipal> {
            every { subject } returns userId
        }

    fun createMockedIdentity(userId: String? = DEFAULT_USER_ID): SecurityIdentity =
        mockk {
            every { isAnonymous } answers {
                when (userId) {
                    null -> true
                    else -> false
                }
            }

            every { principal } answers { userId?.let(::createMockedPrincipal) }
        }
}

fun PageInfo.expect(
    pageIndex: Int,
    pageSize: Int,
    totalItems: Long = pageSize.toLong(),
    totalPages: Int = 1
) {
    this.pageIndex shouldBe pageIndex
    this.pageSize shouldBe pageSize
    this.totalItems shouldBe totalItems
    this.totalPages shouldBe totalPages
}

@Transactional
fun <T> setUpTest(block: () -> T): T = block()

@Transactional
fun <T> cleanUpTest(block: () -> T): T = block()

// See https://github.com/hamcrest/JavaHamcrest/issues/220
fun BaseEntity.id(): Int = id!!.toInt()

fun assertThrowsHttpException(
    expectedStatus: Response.Status = Response.Status.NOT_FOUND,
    expectedMessage: String = "Could not find",
    block: () -> Any?,
) {
    val exception = shouldThrow<WebApplicationException>(block)

    exception.response.statusInfo shouldBe expectedStatus
    exception.message shouldContain expectedMessage
}
