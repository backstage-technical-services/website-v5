package org.backstage.usergroup

import org.backstage.faker
import java.util.*

object UserGroupFixtures {
    fun makeUserGroup(
        id: String = UUID.randomUUID().toString(),
        name: String = faker.parksAndRec.characters(),
    ) = UserGroupResponse.Default(id = id, name = name)

    const val EXAMPLE_ID = "rol_wNLDErLC83rl5lnY"
    val DEFAULT_RESPONSE = UserGroupResponse.Default(
        id = EXAMPLE_ID,
        name = "Example User Group",
    )
    val DEFAULT_RESPONSE_JSON = """
        {
            "id": "$EXAMPLE_ID",
            "name": "Example User Group"
        }
    """.trimIndent()
}
