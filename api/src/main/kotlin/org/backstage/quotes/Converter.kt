package org.backstage.quotes

import org.backstage.users.UserEntity

object QuoteConverter {
    fun toDefaultResponse(quote: QuoteEntity, user: UserEntity?) = QuoteResponse.Default(
        id = quote.id!!,
        culprit = quote.culprit,
        quote = quote.quote,
        date = quote.date,
        rating = quote.rating,
        userVote = when (user) {
            null -> null
            else -> quote.votes.firstOrNull { vote -> vote.user.id == user.id }?.type
        }
    )
}
