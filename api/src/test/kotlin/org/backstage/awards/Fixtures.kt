package org.backstage.awards

import org.backstage.AuthHelpers
import org.backstage.faker
import java.util.*
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
        suggestedBy: String? = when (Random.nextBoolean()) {
            true -> UUID.randomUUID().toString()
            false -> null
        },
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
    val ENTITY = AwardEntity(
        name = "Unpersisted Award",
        description = "The description",
        recurring = false,
        suggestedBy = AuthHelpers.DEFAULT_USER_ID,
        approved = false
    )
    val HYDRATED_ENTITY = AwardEntity(
        name = "Existing Award",
        description = "The existing award",
        recurring = true,
        suggestedBy = AuthHelpers.DEFAULT_USER_ID,
        approved = true
    ).apply {
        id = EXISTING_ENTITY_ID
    }

    val RESPONSE_FULL = AwardResponse.Full(
        id = EXISTING_ENTITY_ID,
        name = "Response Award",
        description = "The response description",
        recurring = false,
        suggestedBy = AuthHelpers.DEFAULT_USER_ID,
        approved = false
    )
    val RESPONSE_FULL_JSON = """
        {
            "id": $EXISTING_ENTITY_ID,
            "name": "Response Award",
            "description": "The response description",
            "recurring": false,
            "suggestedBy": "${AuthHelpers.DEFAULT_USER_ID}",
            "approved": false
        }
    """.trimIndent()
}
