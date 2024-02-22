package org.backstage.auth

object Roles {
    object Quotes {
        const val READ = "quote:read"
        const val CREATE = "quote:create"
        const val LIKE = "quote:like"
        const val DELETE = "quote:delete"
    }
    object Users {
        const val READ = "user:read"
        const val CREATE = "user:create"
    }
    object UserGroups {
        const val READ = "usergroup:read"
    }
}
