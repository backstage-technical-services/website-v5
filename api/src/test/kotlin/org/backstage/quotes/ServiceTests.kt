package org.backstage.quotes

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotContainAll
import io.kotest.matchers.comparables.shouldBeGreaterThanOrEqualTo
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.quarkus.test.TestTransaction
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.security.TestSecurity
import jakarta.inject.Inject
import org.backstage.AuthHelpers
import org.backstage.assertThrowsHttpException
import org.backstage.expect
import org.backstage.users.UserFixtures
import org.backstage.users.UserRepository
import org.backstage.util.findByIdOrThrow
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
            this.votes.shouldBeEmpty()
        }
    }

    @Test
    fun `when upvoting a non-existent quote an exception should be thrown`() {
        assertThrowsHttpException {
            service.upvote(1000L)
        }
    }

    @Test
    fun `when upvoting a quote the vote should be persisted`() {
        val user = insertUser()
        val quoteId = QuoteFixtures.makeEntity(addedBy = user)
            .also { repository.persist(it) }
            .id!!

        val response = service.upvote(quoteId)

        response.action shouldBe QuoteVoteAction.ADDED
        with(repository.findById(quoteId)) {
            shouldNotBeNull()

            this.rating shouldBe 1
            this.votes shouldHaveSize 1
            with(this.votes.first()) {
                this.type shouldBe QuoteVoteType.UPVOTE
                this.user.id shouldBe user.id!!
            }
        }
    }

    @Test
    fun `when upvoting a quote the user's already downvoted, the upvote should be persisted`() {
        val user = insertUser()
        val quoteId = QuoteFixtures.makeEntity(addedBy = user, rating = QuoteVoteType.DOWNVOTE.weight)
            .also { quote ->
                QuoteVoteEntity(
                    type = QuoteVoteType.DOWNVOTE,
                    user = user,
                ).also { quote.votes.add(it) }
            }
            .also { repository.persist(it) }
            .id!!

        val response = service.upvote(quoteId)

        response.action shouldBe QuoteVoteAction.CHANGED
        with(repository.findById(quoteId)) {
            shouldNotBeNull()

            this.rating shouldBe 1
            this.votes shouldHaveSize 1
            with(this.votes.first()) {
                this.type shouldBe QuoteVoteType.UPVOTE
                this.user.id shouldBe user.id!!
            }
        }
    }

    @Test
    fun `when upvoting a quote the user's already upvoted the vote should be cleared`() {
        val user = insertUser()
        val otherUser = insertUser(identityId = AuthHelpers.EXAMPLE_IDENTITY_ID)
        val quoteId = QuoteFixtures.makeEntity(addedBy = user, rating = 2 * QuoteVoteType.UPVOTE.weight)
            .apply {
                QuoteVoteEntity(type = QuoteVoteType.UPVOTE, user = user)
                    .also { votes.add(it) }
                QuoteVoteEntity(type = QuoteVoteType.UPVOTE, user = otherUser)
                    .also { votes.add(it) }
            }
            .also { repository.persist(it) }
            .id!!

        val response = service.upvote(quoteId)

        response.action shouldBe QuoteVoteAction.REMOVED
        with(repository.findByIdOrThrow(quoteId)) {
            rating shouldBe 1
            votes shouldHaveSize 1
            with(votes.first()) {
                this.user.id shouldNotBe user.id
            }
        }
    }

    @Test
    fun `when downvoting a non-existent quote an exception should be thrown`() {
        assertThrowsHttpException {
            service.downvote(1000L)
        }
    }

    @Test
    fun `when downvoting a quote the downvote should be persisted`() {
        val user = insertUser()
        val quoteId = QuoteFixtures.makeEntity(addedBy = user)
            .also { repository.persist(it) }
            .id!!

        val response = service.downvote(quoteId)

        response.action shouldBe QuoteVoteAction.ADDED
        with(repository.findById(quoteId)) {
            shouldNotBeNull()

            this.rating shouldBe -1
            this.votes shouldHaveSize 1
            with(this.votes.first()) {
                this.type shouldBe QuoteVoteType.DOWNVOTE
                this.user.id shouldBe user.id!!
            }
        }
    }

    @Test
    fun `when downvoting a quote the user's already upvoted, the downvote should be persisted`() {
        val user = insertUser()
        val quoteId = QuoteFixtures.makeEntity(addedBy = user, rating = QuoteVoteType.UPVOTE.weight)
            .also { quote ->
                QuoteVoteEntity(
                    type = QuoteVoteType.UPVOTE,
                    user = user,
                ).also { quote.votes.add(it) }
            }
            .also { repository.persist(it) }
            .id!!

        val response = service.downvote(quoteId)

        response.action shouldBe QuoteVoteAction.CHANGED
        with(repository.findById(quoteId)) {
            shouldNotBeNull()

            this.rating shouldBe -1
            this.votes shouldHaveSize 1
            with(this.votes.first()) {
                this.type shouldBe QuoteVoteType.DOWNVOTE
                this.user.id shouldBe user.id!!
            }
        }
    }

    @Test
    fun `when downvoting a quote the user's already downvoted the vote should be cleared`() {
        val user = insertUser()
        val otherUser = insertUser(identityId = AuthHelpers.EXAMPLE_IDENTITY_ID)
        val quoteId = QuoteFixtures.makeEntity(
            addedBy = user,
            rating = QuoteVoteType.UPVOTE.weight + QuoteVoteType.DOWNVOTE.weight,
        )
            .apply {
                QuoteVoteEntity(type = QuoteVoteType.DOWNVOTE, user = user)
                    .also { votes.add(it) }
                QuoteVoteEntity(type = QuoteVoteType.UPVOTE, user = otherUser)
                    .also { votes.add(it) }
            }
            .also { repository.persist(it) }
            .id!!

        val response = service.downvote(quoteId)

        response.action shouldBe QuoteVoteAction.REMOVED
        with(repository.findByIdOrThrow(quoteId)) {
            rating shouldBe 1
            votes shouldHaveSize 1
            with(votes.first()) {
                this.user.id shouldNotBe user.id
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
    fun `when fetching a quote that the user has upvoted the userVote field should be UPVOTE`() {
        val user = insertUser()
        val quoteId = QuoteFixtures.makeEntity(addedBy = user)
            .also { repository.persist(it) }
            .id!!
        service.upvote(quoteId)

        with(service.get(quoteId)) {
            userVote shouldBe QuoteVoteType.UPVOTE
        }
    }

    @Test
    fun `when fetching a quote that the user has downvoted the userVote field should be DOWNVOTE`() {
        val user = insertUser()
        val quoteId = QuoteFixtures.makeEntity(addedBy = user)
            .also { repository.persist(it) }
            .id!!
        service.downvote(quoteId)

        with(service.get(quoteId)) {
            userVote shouldBe QuoteVoteType.DOWNVOTE
        }
    }

    @Test
    fun `when fetching a quote that's been voted by another user the userVote field should be null`() {
        val user = insertUser()
        val votedUser = insertUser(identityId = AuthHelpers.EXAMPLE_IDENTITY_ID)

        val quoteId = QuoteFixtures.makeEntity(addedBy = user)
            .apply {
                QuoteVoteEntity(type = QuoteVoteType.UPVOTE, user = votedUser)
                    .also { votes.add(it) }
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
