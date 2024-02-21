package org.backstage.users

import com.auth0.exception.APIException
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotContainAll
import io.kotest.matchers.comparables.shouldBeLessThanOrEqualTo
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.quarkiverse.test.junit.mockk.InjectMock
import io.quarkus.test.TestTransaction
import io.quarkus.test.junit.QuarkusTest
import jakarta.inject.Inject
import jakarta.ws.rs.core.Response.Status.CONFLICT
import jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR
import org.backstage.AuthHelpers
import org.backstage.assertThrowsHttpException
import org.backstage.auth.AuthService
import org.backstage.expect
import org.backstage.shouldBeValidId
import org.backstage.usergroups.UserGroupFixtures
import org.junit.jupiter.api.Test

@QuarkusTest
@TestTransaction
class UserServicePaginationTests {
    @Inject
    lateinit var repository: UserRepository

    @Inject
    lateinit var service: UserService

    @Test
    fun `listing the users, should return all the existing users in the correct order`() {
        insertTestUsers(100)

        val users = service.list(pageIndex = 0, pageSize = 20)

        users.items.shouldHaveSize(20)
        for (i in 1 until users.items.size) {
            users.items[i - 1].name shouldBeLessThanOrEqualTo users.items[i].name
        }

        users.page.expect(
            pageIndex = 0,
            pageSize = 20,
            totalPages = 5,
            totalItems = 100,
        )
    }

    @Test
    fun `listing users with too high a page size should have the size reduced`() {
        insertTestUsers(20)

        val users = service.list(pageIndex = 0, pageSize = 100)

        users.page.pageSize shouldBe RepositoryUserService.MAX_PAGE_SIZE
    }

    @Test
    fun `listing users with a small page size should show that more pages are available`() {
        insertTestUsers(5)

        val users = service.list(pageIndex = 0, pageSize = 2)

        with(users.page) {
            totalPages shouldBe 3
        }
    }

    @Test
    fun `fetching different pages should contain different results`() {
        insertTestUsers(6)

        val pageOne = service.list(pageIndex = 0, pageSize = 3)
        val pageTwo = service.list(pageIndex = 1, pageSize = 3)

        pageOne.items shouldNotContainAll pageTwo.items
    }

    @Test
    fun `requesting an invalid page should return no items`() {
        insertTestUsers(40)

        val users = service.list(10, 20)

        users.items.shouldBeEmpty()
    }

    private fun insertTestUsers(num: Int) {
        repeat(num) {
            repository.persist(UserFixtures.makeEntity())
        }
    }
}

@QuarkusTest
class UserServiceTests {
    @Inject
    lateinit var repository: UserRepository

    @Inject
    lateinit var service: UserService

    @InjectMock
    lateinit var authService: AuthService

    @Test
    fun `the service should be injected`() {
        service.shouldNotBeNull()
    }

    @Test
    @TestTransaction
    fun `when creating a user it should be persisted with the identity ID`() {
        every { authService.createUser(any()) } returns AuthHelpers.EXAMPLE_IDENTITY_ID
        every { authService.getUserGroup(UserGroupFixtures.EXAMPLE_ID) } returns UserGroupFixtures.DEFAULT_RESPONSE

        val request = UserFixtures.makeCreateRequest()
        val userId = service.create(request)

        userId.shouldBeValidId()
        with(repository.findById(userId)) {
            this.shouldNotBeNull()

            this.identityId shouldBe AuthHelpers.EXAMPLE_IDENTITY_ID
            this.username shouldBe request.username
            this.email shouldBe request.email
            this.firstName shouldBe request.firstName
            this.lastName shouldBe request.lastName
            this.nickname.shouldBeNull()
            this.phone.shouldBeNull()
            this.address.shouldBeNull()
            this.toolColours.shouldBeNull()
            this.dateOfBirth.shouldBeNull()
            this.showEmail.shouldBeFalse()
            this.showPhone.shouldBeFalse()
            this.showAddress.shouldBeFalse()
            this.showAge.shouldBeFalse()
            this.groupId shouldBe request.group
            this.deleted.shouldBeFalse()
        }
    }

    @Test
    @TestTransaction
    fun `when creating a user with a duplicate username an exception should be thrown`() {
        val username = "jb123"
        val request = UserFixtures.makeCreateRequest(username = username)
        repository.persist(UserFixtures.makeEntity(username = username))

        assertThrowsHttpException(
            expectedStatus = CONFLICT,
            expectedMessage = "A user with that username already exists",
        ) {
            service.create(request)
        }
    }

    @Test
    @TestTransaction
    fun `when creating a user with a duplicate email an exception should be thrown`() {
        val email = "jb123@bath.test"
        val request = UserFixtures.makeCreateRequest(email = email)
        repository.persist(UserFixtures.makeEntity(email = email))

        assertThrowsHttpException(
            expectedStatus = CONFLICT,
            expectedMessage = "A user with that email already exists",
        ) {
            service.create(request)
        }
    }

    @Test
    @TestTransaction
    fun `when creating a user and the auth service throws an exception, the exception should be translated to a 500`() {
        every { authService.createUser(any()) } throws APIException("", 400, Exception())

        val request = UserFixtures.makeCreateRequest()

        assertThrowsHttpException(
            expectedStatus = INTERNAL_SERVER_ERROR,
            expectedMessage = "An internal error occurred",
        ) {
            service.create(request)
        }
    }
}
