package org.backstage.awards

import org.backstage.faker
import org.backstage.user.UserEntity
import org.backstage.user.UserFixtures
import kotlin.random.Random

object AwardFixtures {
    fun makeCreateRequest(
        name: String = faker.funnyName.name(),
        description: String = faker.lorem.words(),
        recurring: Boolean = true
    ) = AwardRequest.Create(
        name = name,
        description = description,
        recurring = recurring,
    )

    fun makeEntity(
        name: String = faker.lorem.words(),
        description: String = faker.lorem.words(),
        recurring: Boolean = Random.nextBoolean(),
        suggestedBy: UserEntity? = null,
        approved: Boolean = Random.nextBoolean(),
        deleted: Boolean = false,
    ) = AwardEntity(
        name = name,
        description = description,
        recurring = recurring,
        suggestedBy = suggestedBy,
        approved = approved,
    ).apply {
        this.deleted = deleted
    }

    val CREATE_REQUEST = AwardRequest.Create(
        name = "New Award",
        description = "The description",
        recurring = true
    )
    val CREATE_REQUEST_JSON = """
        {
            "name": "New Award",
            "description": "The description",
            "recurring": true
        }
    """.trimIndent()

    val UPDATE_REQUEST = AwardRequest.Update(
        name = "Update Award",
        description = "The new description",
        recurring = false
    )
    val UPDATE_REQUEST_JSON = """
        {
            "name": "Update Award",
            "description": "The new description",
            "recurring": false
        }
    """.trimIndent()

    const val EXISTING_ENTITY_ID: Long = 1L
    const val NON_EXISTENT_ID: Long = 1000L

    val RESPONSE_FULL = AwardResponse.Full(
        id = EXISTING_ENTITY_ID,
        name = "Response Award",
        description = "The response description",
        recurring = false,
        suggestedBy = UserFixtures.RESPONSE_MINIMAL,
        approved = false
    )
    val RESPONSE_FULL_JSON = """
        {
            "id": $EXISTING_ENTITY_ID,
            "name": "Response Award",
            "description": "The response description",
            "recurring": false,
            "suggestedBy": ${UserFixtures.RESPONSE_MINIMAL_JSON},
            "approved": false
        }
    """.trimIndent()
}
