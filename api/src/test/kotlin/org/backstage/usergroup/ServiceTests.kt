package org.backstage.usergroup

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.quarkiverse.test.junit.mockk.InjectMock
import io.quarkus.test.junit.QuarkusTest
import jakarta.inject.Inject
import org.backstage.assertThrowsHttpException
import org.backstage.auth.AuthService
import org.backstage.auth.UserGroupList
import org.backstage.expect
import org.backstage.faker
import org.junit.jupiter.api.Test
import java.util.*

@QuarkusTest
class UserGroupServiceTests {
    @InjectMock
    lateinit var authService: AuthService

    @Inject
    lateinit var service: UserGroupService

    @Test
    fun `the service should be injected`() {
        service.shouldNotBeNull()
    }

    @Test
    fun `listing the user groups, should return all the groups`() {
        every { authService.listUserGroups(any(), any()) } returns UserGroupList(
            totalItems = 100,
            groups = generateGroups(20),
        )

        val userGroups = service.list(pageIndex = 0, pageSize = 20)
        userGroups.page.expect(
            pageIndex = 0,
            pageSize = 20,
            totalPages = 5,
            totalItems = 100,
        )
    }

    @Test
    fun `listing user groups with too high a page size should have the size reduced`() {
        every { authService.listUserGroups(any(), any()) } returns UserGroupList(
            totalItems = 100,
            groups = generateGroups(50),
        )

        val userGroups = service.list(pageIndex = 0, pageSize = 100)

        userGroups.page.pageSize shouldBe AuthUserGroupService.MAX_PAGE_SIZE
    }

    @Test
    fun `listing user groups with a small page size should show that more pages are available`() {
        every { authService.listUserGroups(any(), any()) } returns UserGroupList(
            totalItems = 5,
            groups = generateGroups(2),
        )

        val userGroups = service.list(pageIndex = 0, pageSize = 2)

        with(userGroups.page) {
            totalPages shouldBe 3
        }
    }

    @Test
    fun `when getting a user group which exists, the user group should be returned`() {
        every { authService.getUserGroup(any()) } returns UserGroupFixtures.makeUserGroup(
            id = UserGroupFixtures.EXAMPLE_ID,
        )

        val userGroup = service.get(UserGroupFixtures.EXAMPLE_ID)

        userGroup.shouldNotBeNull()
    }

    @Test
    fun `when getting a group that doesn't exist an exception should be thrown`() {
        every { authService.getUserGroup(any()) } returns null

        assertThrowsHttpException(expectedMessage = "Could not find user group") {
            service.get(UserGroupFixtures.EXAMPLE_ID)
        }
    }

    private fun generateGroups(num: Int): Map<String, String> {
        val groups = mutableMapOf<String, String>()
        repeat(num) {
            groups[UUID.randomUUID().toString()] = faker.parksAndRec.cities()
        }
        return groups
    }
}
