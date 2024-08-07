package com.livefast.eattrash.raccoonforlemmy.domain.inbox.usecase

interface GetUnreadItemsUseCase {
    suspend fun getUnreadReplies(): Int

    suspend fun getUnreadMentions(): Int

    suspend fun getUnreadMessages(): Int
}
