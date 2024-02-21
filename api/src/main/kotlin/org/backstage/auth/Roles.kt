package org.backstage.auth

object Roles {
    object Awards {
        const val READ = "award:read"
        const val CREATE = "award:create"
        const val SUGGEST = "award:suggest"
        const val UPDATE = "award:edit"
        const val APPROVE = "award:approve"
        const val DELETE = "award:delete"
    }
    object Users {
        const val READ = "user:read"
        const val CREATE = "user:create"
    }
    object UserGroups {
        const val READ = "usergroup:read"
    }
}
