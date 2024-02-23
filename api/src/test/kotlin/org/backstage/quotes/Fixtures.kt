package org.backstage.quotes

import org.backstage.faker
import org.backstage.users.UserEntity
import org.backstage.users.UserFixtures
import java.time.LocalDateTime
import kotlin.random.Random

object QuoteFixtures {
    fun makeCreateRequest(
        culprit: String = faker.name.neutralFirstName(),
        quote: String = faker.quote.fortuneCookie(),
        date: LocalDateTime = LocalDateTime.now(),
    ) = QuoteRequest.Create(
        culprit = culprit,
        quote = quote,
        date = date,
    )

    fun makeEntity(
        culprit: String = faker.name.neutralFirstName(),
        quote: String = faker.quote.fortuneCookie(),
        date: LocalDateTime = LocalDateTime.now(),
        addedBy: UserEntity = UserFixtures.makeEntity()
            .apply {
                id = Random.nextLong(1, 5000)
            },
        rating: Int = 0,
        deleted: Boolean = false,
    ) = QuoteEntity(
        culprit = culprit,
        quote = quote,
        date = date,
        addedBy = addedBy,
        rating = rating,
    ).apply {
        this.deleted = deleted
    }

    private const val EXAMPLE_ID = 1L
    private const val EXAMPLE_CULPRIT = "Phil Connors"
    private const val EXAMPLE_QUOTE = "Ned, I would love to stand here and talk with you—but I’m not going to."
    val CREATE_REQUEST = makeCreateRequest(
        culprit = EXAMPLE_CULPRIT,
        quote = EXAMPLE_QUOTE,
        date = LocalDateTime.of(2018, 1, 2, 10, 55)
    )
    val CREATE_REQUEST_JSON = """
        {
            "culprit": "$EXAMPLE_CULPRIT",
            "quote": "$EXAMPLE_QUOTE",
            "date": "2018-01-02 10:55:00"
        }
    """.trimIndent()

    val RESPONSE_DEFAULT = QuoteResponse.Default(
        id = EXAMPLE_ID,
        culprit = EXAMPLE_CULPRIT,
        quote = EXAMPLE_QUOTE,
        date = LocalDateTime.of(2018, 1, 2, 10, 55, 31),
        rating = 4,
        likes = 5,
        dislikes = 1,
    )
    val RESPONSE_DEFAULT_JSON = """
        {
            "id": $EXAMPLE_ID,
            "culprit": "$EXAMPLE_CULPRIT",
            "quote": "$EXAMPLE_QUOTE",
            "date": "2018-01-02 10:55:31",
            "rating": 4,
            "likes": 5,
            "dislikes": 1
        }
    """.trimIndent()
}
