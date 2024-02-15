package org.backstage.user

import org.backstage.faker
import org.backstage.usergroup.UserGroupFixtures
import java.time.LocalDate
import java.util.UUID

object UserFixtures {
    fun makeCreateRequest(
        username: String = faker.random.randomString(),
        email: String = faker.internet.safeEmail(),
        firstName: String = faker.name.firstName(),
        lastName: String = faker.name.lastName(),
        group: String = UserGroupFixtures.EXAMPLE_ID,
    ) = UserRequest.Create(
        username = username,
        email = email,
        firstName = firstName,
        lastName = lastName,
        group = group,
    )

    fun makeEntity(
        identityId: String = UUID.randomUUID().toString(),
        username: String = faker.random.randomString(),
        email: String = faker.internet.safeEmail(),
        firstName: String = faker.name.firstName(),
        lastName: String = faker.name.lastName(),
        nickname: String? = null,
        phone: String? = null,
        address: String? = null,
        toolColours: String? = null,
        dateOfBirth: LocalDate? = null,
        showEmail: Boolean = false,
        showPhone: Boolean = false,
        showAddress: Boolean = false,
        showAge: Boolean = false,
        groupId: String = UserGroupFixtures.EXAMPLE_ID,
        deleted: Boolean = false,
    ) = UserEntity(
        identityId = identityId,
        username = username,
        email = email,
        firstName = firstName,
        lastName = lastName,
        nickname = nickname,
        phone = phone,
        address = address,
        toolColours = toolColours,
        dateOfBirth = dateOfBirth,
        showEmail = showEmail,
        showPhone = showPhone,
        showAddress = showAddress,
        showAge = showAge,
        groupId = groupId,
    ).apply {
        this.deleted = deleted
    }

    val CREATE_REQUEST = UserRequest.Create(
        username = "jb123",
        email = "jb123@bath.test",
        firstName = "Joe",
        lastName = "Bloggs",
        group = UserGroupFixtures.EXAMPLE_ID,
    )
    val CREATE_REQUEST_JSON = """
        {
            "username": "jb123",
            "email": "jb123@bath.test",
            "firstName": "Joe",
            "lastName": "Bloggs",
            "group": "${UserGroupFixtures.EXAMPLE_ID}"
        }
    """.trimIndent()

    const val EXISTING_ENTITY_ID = 1L

    val RESPONSE_MINIMAL = UserResponse.Minimal(
        id = EXISTING_ENTITY_ID,
        email = "jb123@bath.test",
        name = "Joe Bloggs",
    )
    val RESPONSE_MINIMAL_JSON = """
        {
            "id": $EXISTING_ENTITY_ID,
            "email": "jb123@bath.test",
            "name": "Joe Bloggs"
        }
    """.trimIndent()
}
