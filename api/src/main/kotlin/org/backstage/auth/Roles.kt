package org.backstage.auth

object Roles {
    object Committee {
        const val READ = "committee:read"
        const val EDIT = "committee:edit"
    }
    object Quotes {
        const val READ = "quote:read"
        const val CREATE = "quote:create"
        const val VOTE = "quote:vote"
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
