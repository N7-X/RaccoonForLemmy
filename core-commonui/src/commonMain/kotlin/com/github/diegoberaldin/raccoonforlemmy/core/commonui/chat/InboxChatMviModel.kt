package com.github.diegoberaldin.raccoonforlemmy.core.commonui.chat

import com.github.diegoberaldin.raccoonforlemmy.core.architecture.MviModel
import com.github.diegoberaldin.raccoonforlemmy.domain.lemmy.data.PrivateMessageModel

interface InboxChatMviModel :
    MviModel<InboxChatMviModel.Intent, InboxChatMviModel.UiState, InboxChatMviModel.SideEffect> {
    sealed interface Intent {
        object LoadNextPage : Intent
        data class SetNewMessageContent(val value: String) : Intent
        object SubmitNewMessage : Intent
    }

    data class UiState(
        val refreshing: Boolean = false,
        val loading: Boolean = false,
        val canFetchMore: Boolean = true,
        val currentUserId: Int? = null,
        val otherUserName: String = "",
        val otherUserAvatar: String? = null,
        val messages: List<PrivateMessageModel> = emptyList(),
        val newMessageContent: String = "",
    )

    sealed interface SideEffect
}