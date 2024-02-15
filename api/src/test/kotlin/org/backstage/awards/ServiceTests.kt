package org.backstage.awards

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
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
import io.quarkus.test.security.TestSecurity
import jakarta.inject.Inject
import org.backstage.AuthHelpers
import org.backstage.assertThrowsHttpException
import org.backstage.auth.AuthService
import org.backstage.expect
import org.backstage.shouldBeValidId
import org.backstage.user.UserFixtures
import org.backstage.user.UserService
import org.backstage.usergroup.UserGroupFixtures
import org.backstage.util.findByIdOrThrow
import org.junit.jupiter.api.Test

@QuarkusTest
@TestTransaction
class AwardServicePaginationTests {
    @Inject
    lateinit var repository: AwardRepository

    @Inject
    lateinit var service: AwardService

    @Test
    fun `listing the awards, should return all the existing awards in the correct order`() {
        insertTestAwards(100)

        val awards = service.list(pageIndex = 0, pageSize = 20)

        awards.items.shouldHaveSize(20)
        for (i in 1 until awards.items.size) {
            awards.items[i - 1].name shouldBeLessThanOrEqualTo awards.items[i].name
        }

        awards.page.expect(
            pageIndex = 0,
            pageSize = 20,
            totalPages = 5,
            totalItems = 100,
        )
    }

    @Test
    fun `listing awards with too high a page size should have the size reduced`() {
        insertTestAwards(20)

        val awards = service.list(pageIndex = 0, pageSize = 100)

        awards.page.pageSize shouldBe RepositoryAwardService.MAX_PAGE_SIZE
    }

    @Test
    fun `listing awards with a small page size should show that more pages are available`() {
        insertTestAwards(5)

        val awards = service.list(pageIndex = 0, pageSize = 2)

        with(awards.page) {
            totalPages shouldBe 3
        }
    }

    @Test
    fun `fetching different pages should contain different results`() {
        insertTestAwards(6)

        val pageOne = service.list(pageIndex = 0, pageSize = 3)
        val pageTwo = service.list(pageIndex = 1, pageSize = 3)

        pageOne.items shouldNotContainAll pageTwo.items
    }

    @Test
    fun `requesting an invalid page should return no items`() {
        insertTestAwards(40)

        val awards = service.list(10, 20)

        awards.items.shouldBeEmpty()
    }

    private fun insertTestAwards(num: Int) {
        repeat(num) {
            repository.persist(AwardFixtures.makeEntity())
        }
    }
}

@QuarkusTest
@TestSecurity(user = AuthHelpers.DEFAULT_USER_ID)
class AwardServiceTests {
    @Inject
    lateinit var repository: AwardRepository

    @Inject
    lateinit var service: AwardService

    @Inject
    lateinit var userService: UserService

    @InjectMock
    lateinit var authService: AuthService

    @Test
    fun `the service should be injected`() {
        service.shouldNotBeNull()
    }

    @Test
    @TestTransaction
    fun `when creating an award it should be persisted as an approved award`() {
        val request = AwardFixtures.makeCreateRequest(recurring = true)
        val awardId = service.create(request)

        awardId.shouldBeValidId()
        with(repository.findById(awardId)) {
            this.shouldNotBeNull()

            this.name shouldBe request.name
            this.description shouldBe request.description
            this.recurring shouldBe request.recurring
            this.suggestedBy.shouldBeNull()
            this.approved.shouldBeTrue()
            this.deleted.shouldBeFalse()
        }
    }

    @Test
    @TestTransaction
    @TestSecurity(user = AuthHelpers.EXAMPLE_IDENTITY_ID)
    fun `when suggesting an award it should be persisted as a suggested award`() {
        every { authService.createUser(any()) } returns AuthHelpers.EXAMPLE_IDENTITY_ID
        every { authService.getUserGroup(any()) } returns UserGroupFixtures.DEFAULT_RESPONSE
        val suggestedByUserId = userService.create(UserFixtures.makeCreateRequest())

        val request = AwardFixtures.makeCreateRequest(recurring = true)
        val awardId = service.suggest(request)

        awardId.shouldBeValidId()
        with(repository.findById(awardId)) {
            shouldNotBeNull()

            this.name shouldBe request.name
            this.description shouldBe request.description
            this.recurring.shouldBeTrue()
            this.approved.shouldBeFalse()
            this.deleted.shouldBeFalse()

            with(this.suggestedBy) {
                shouldNotBeNull()
                id shouldBe suggestedByUserId
            }
        }
    }

    @Test
    @TestTransaction
    fun `when getting the details of an award the correct award should be returned`() {
        val awardEntity = AwardFixtures.makeEntity()
        repository.persist(awardEntity)

        with(service.get(awardEntity.id!!)) {
            name shouldBe awardEntity.name
            description shouldBe awardEntity.description
            recurring shouldBe awardEntity.recurring
            suggestedBy shouldBe awardEntity.suggestedBy
            approved shouldBe awardEntity.approved
        }
    }

    @Test
    fun `when getting an award that doesn't exist, an exception should be thrown`() {
        assertThrowsHttpException(expectedMessage = "Could not find award") {
            service.get(AwardFixtures.NON_EXISTENT_ID)
        }
    }

    @Test
    @TestTransaction
    fun `when updating the award name, only that should be updated`() {
        val originalAward = AwardFixtures.makeEntity(name = "Old name")
            .also { repository.persist(it) }

        service.update(originalAward.id!!, AwardRequest.Update(name = "New name"))

        with(repository.findByIdOrThrow(originalAward.id!!)) {
            name shouldBe "New name"
            description shouldBe originalAward.description
            recurring shouldBe originalAward.recurring
        }
    }

    @Test
    @TestTransaction
    fun `when updating the award description, only that should be updated`() {
        val originalAward = AwardFixtures.makeEntity(description = "Old description")
            .also { repository.persist(it) }

        service.update(originalAward.id!!, AwardRequest.Update(description = "New description"))

        with(repository.findByIdOrThrow(originalAward.id!!)) {
            name shouldBe originalAward.name
            description shouldBe "New description"
            recurring shouldBe originalAward.recurring
        }
    }

    @Test
    @TestTransaction
    fun `when updating the award recurring property, only that should be updated`() {
        val originalAward = AwardFixtures.makeEntity(recurring = true)
            .also { repository.persist(it) }

        service.update(originalAward.id!!, AwardRequest.Update(recurring = false))

        with(repository.findByIdOrThrow(originalAward.id!!)) {
            name shouldBe originalAward.name
            description shouldBe originalAward.description
            recurring shouldBe false
        }
    }

    @Test
    fun `when updating a non-existent award throw an exception should be thrown`() {
        assertThrowsHttpException(expectedMessage = "Could not find award") {
            service.update(AwardFixtures.NON_EXISTENT_ID, AwardFixtures.UPDATE_REQUEST)
        }
    }

    @Test
    @TestTransaction
    fun `when approving an award the property should be set`() {
        val entity = AwardFixtures.makeEntity(
            approved = false,
        ).also { repository.persist(it) }

        service.approve(entity.id!!)

        with(repository.findByIdOrThrow(entity.id!!)) {
            approved.shouldBeTrue()
        }
    }

    @Test
    @TestTransaction
    fun `when deleting an award it should no longer exist`() {
        val awardId = AwardFixtures.makeEntity()
            .also { repository.persist(it) }
            .id!!

        service.delete(awardId)

        repository.findById(awardId).shouldBeNull()
    }

    @Test
    fun `when deleting a non-existent award no exception should be thrown`() {
        shouldNotThrowAny {
            service.delete(AwardFixtures.NON_EXISTENT_ID)
        }
    }

    @Test
    @TestTransaction
    fun `when deleting an award that's already deleted, no exception should be thrown`() {
        val awardId = AwardFixtures.makeEntity(deleted = false)
            .also { repository.persist(it) }
            .id!!

        shouldNotThrowAny {
            service.delete(awardId)
        }
    }
}
