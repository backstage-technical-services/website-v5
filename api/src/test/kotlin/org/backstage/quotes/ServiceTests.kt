package org.backstage.quotes

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotContainAll
import io.kotest.matchers.comparables.shouldBeGreaterThanOrEqualTo
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.quarkus.test.TestTransaction
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.security.TestSecurity
import jakarta.inject.Inject
import org.backstage.AuthHelpers
import org.backstage.assertThrowsHttpException
import org.backstage.expect
import org.backstage.users.UserFixtures
import org.backstage.users.UserRepository
import org.junit.jupiter.api.Test

@QuarkusTest
@TestTransaction
class QuoteServicePaginationTests {
    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var repository: QuoteRepository

    @Inject
    lateinit var service: QuoteService

    @Test
    fun `listing the quotes should return all the existing quotes in the correct order`() {
        insertTestQuotes(100)

        val quotes = service.list(pageIndex = 0, pageSize = 20)

        quotes.items.shouldHaveSize(20)
        for (i in 1 until quotes.items.size) {
            quotes.items[i - 1].date shouldBeGreaterThanOrEqualTo quotes.items[i].date
        }

        quotes.page.expect(
            pageIndex = 0,
            pageSize = 20,
            totalPages = 5,
            totalItems = 100,
        )
    }

    @Test
    fun `listing quotes with too high a page size should have the size reduced`() {
        insertTestQuotes(20)

        val quotes = service.list(pageIndex = 0, pageSize = 100)

        quotes.page.pageSize shouldBe RepositoryQuoteService.MAX_PAGE_SIZE
    }

    @Test
    fun `listing quotes with a small page size should show that more pages are available`() {
        insertTestQuotes(5)

        val quotes = service.list(pageIndex = 0, pageSize = 2)

        with(quotes.page) {
            totalPages shouldBe 3
        }
    }

    @Test
    fun `fetching different pages should contain different results`() {
        insertTestQuotes(6)

        val pageOne = service.list(pageIndex = 0, pageSize = 3)
        val pageTwo = service.list(pageIndex = 1, pageSize = 3)

        pageOne.items shouldNotContainAll pageTwo.items
    }

    @Test
    fun `requesting an invalid page should return no items`() {
        insertTestQuotes(40)

        val quotes = service.list(10, 20)

        quotes.items.shouldBeEmpty()
    }

    private fun insertTestQuotes(num: Int) {
        val user = UserFixtures.makeEntity(identityId = AuthHelpers.EXAMPLE_IDENTITY_ID)
            .also { userRepository.persist(it) }

        repeat(num) {
            repository.persist(QuoteFixtures.makeEntity(addedBy = user))
        }
    }
}

@QuarkusTest
@TestSecurity(user = AuthHelpers.DEFAULT_USER_ID)
@TestTransaction
class QuoteServiceTests {
    @Inject
    lateinit var repository: QuoteRepository

    @Inject
    lateinit var service: QuoteService

    @Inject
    lateinit var userRepository: UserRepository

    @Test
    fun `the service should be injected`() {
        service.shouldNotBeNull()
    }

    @Test
    fun `when creating a quote it should be persisted`() {
        val user = insertUser()
        val request = QuoteFixtures.makeCreateRequest()

        val quoteId = service.create(request)

        quoteId.shouldNotBeNull()
        with(repository.findById(quoteId)) {
            shouldNotBeNull()

            this.culprit shouldBe request.culprit
            this.quote shouldBe request.quote
            this.date shouldBe request.date
            this.addedBy.id shouldBe user.id!!
            this.likes.shouldBeEmpty()
        }
    }

    @Test
    fun `when liking a non-existent quote an exception should be thrown`() {
        assertThrowsHttpException {
            service.like(1000L)
        }
    }

    @Test
    fun `when liking a quote the like should be persisted`() {
        val user = insertUser()
        val quoteId = QuoteFixtures.makeEntity(addedBy = user)
            .also { repository.persist(it) }
            .id!!

        service.like(quoteId)

        with(repository.findById(quoteId)) {
            shouldNotBeNull()

            this.rating shouldBe 1
            this.likes shouldHaveSize 1
            with(this.likes.first()) {
                this.type shouldBe QuoteLikeType.LIKE
                this.user.id shouldBe  user.id!!
            }
        }
    }

    @Test
    fun `when liking a quote the user's already disliked, the like should be persisted`() {
        val user = insertUser()
        val quoteId = QuoteFixtures.makeEntity(addedBy = user, rating = QuoteLikeType.DISLIKE.vote)
            .also { quote ->
                QuoteLikeEntity(
                    type = QuoteLikeType.DISLIKE,
                    user = user,
                ).also { quote.likes.add(it) }
            }
            .also { repository.persist(it) }
            .id!!

        service.like(quoteId)

        with(repository.findById(quoteId)) {
            shouldNotBeNull()

            this.rating shouldBe 1
            this.likes shouldHaveSize 1
            with(this.likes.first()) {
                this.type shouldBe QuoteLikeType.LIKE
                this.user.id shouldBe  user.id!!
            }
        }
    }

    @Test
    fun `when disliking a non-existent quote an exception should be thrown`() {
        assertThrowsHttpException {
            service.dislike(1000L)
        }
    }

    @Test
    fun `when disliking a quote the like should be persisted`() {
        val user = insertUser()
        val quoteId = QuoteFixtures.makeEntity(addedBy = user)
            .also { repository.persist(it) }
            .id!!

        service.dislike(quoteId)

        with(repository.findById(quoteId)) {
            shouldNotBeNull()

            this.rating shouldBe -1
            this.likes shouldHaveSize 1
            with(this.likes.first()) {
                this.type shouldBe QuoteLikeType.DISLIKE
                this.user.id shouldBe  user.id!!
            }
        }
    }

    @Test
    fun `when disliking a quote the user's already liked, the dislike should be persisted`() {
        val user = insertUser()
        val quoteId = QuoteFixtures.makeEntity(addedBy = user, rating = QuoteLikeType.LIKE.vote)
            .also { quote ->
                QuoteLikeEntity(
                    type = QuoteLikeType.LIKE,
                    user = user,
                ).also { quote.likes.add(it) }
            }
            .also { repository.persist(it) }
            .id!!

        service.dislike(quoteId)

        with(repository.findById(quoteId)) {
            shouldNotBeNull()

            this.rating shouldBe -1
            this.likes shouldHaveSize 1
            with(this.likes.first()) {
                this.type shouldBe QuoteLikeType.DISLIKE
                this.user.id shouldBe  user.id!!
            }
        }
    }

    @Test
    fun `when deleting a non-existent quote no exception should be thrown`() {
        shouldNotThrowAny {
            service.delete(1000L)
        }
    }

    @Test
    fun `when deleting a quote it should be removed from the database`() {
        val user = insertUser()
        val quoteId = QuoteFixtures.makeEntity(addedBy = user)
            .also { repository.persist(it) }
            .id!!

        service.delete(quoteId)

        repository.findById(quoteId).shouldBeNull()
    }

    @Test
    fun `when fetching a quote that doesn't exist an exception should be thrown`() {
        insertUser()

        assertThrowsHttpException {
            service.get(1000L)
        }
    }

    @Test
    fun `when fetching a quote that exists the properties should be mapped correctly`() {
        val user = insertUser()
        val quoteEntity = QuoteFixtures
            .makeEntity(addedBy = user)
            .also { repository.persist(it) }

        with(service.get(quoteEntity.id!!)) {
            id shouldBe quoteEntity.id!!
            culprit shouldBe quoteEntity.culprit
            quote shouldBe quoteEntity.quote
            date shouldBe quoteEntity.date
            rating shouldBe 0
            userVote.shouldBeNull()
        }
    }

    @Test
    fun `when fetching a quote that the user has liked the userVote field should be LIKED`() {
        val user = insertUser()
        val quoteId = QuoteFixtures.makeEntity(addedBy = user)
            .also { repository.persist(it) }
            .id!!
        service.like(quoteId)

        with(service.get(quoteId)) {
            userVote shouldBe QuoteLikeType.LIKE
        }
    }

    @Test
    fun `when fetching a quote that the user has disliked the userVote field should be DISLIKED`() {
        val user = insertUser()
        val quoteId = QuoteFixtures.makeEntity(addedBy = user)
            .also { repository.persist(it) }
            .id!!
        service.dislike(quoteId)

        with(service.get(quoteId)) {
            userVote shouldBe QuoteLikeType.DISLIKE
        }
    }

    @Test
    fun `when fetching a quote that's been liked by another user the userVote field should be null`() {
        val user = insertUser()
        val likedUser = insertUser(identityId = AuthHelpers.EXAMPLE_IDENTITY_ID)

        val quoteId = QuoteFixtures.makeEntity(addedBy = user)
            .apply {
                QuoteLikeEntity(type = QuoteLikeType.LIKE, user = likedUser)
                    .also { likes.add(it) }
            }
            .also { repository.persist(it) }
            .id!!

        with(service.get(quoteId)) {
            userVote.shouldBeNull()
        }
    }

    private fun insertUser(identityId: String = AuthHelpers.DEFAULT_USER_ID) = UserFixtures
        .makeEntity(identityId = identityId)
        .also { userRepository.persist(it) }
}
