package com.github.diegoberaldin.raccoonforlemmy.feature_profile.content.logged.comments

import com.github.diegoberaldin.raccoonforlemmy.core_architecture.MviModel
import com.github.diegoberaldin.raccoonforlemmy.domain_lemmy.data.CommentModel

interface ProfileCommentsMviModel :
    MviModel<ProfileCommentsMviModel.Intent, ProfileCommentsMviModel.UiState, ProfileCommentsMviModel.Effect> {

    sealed interface Intent {
        object Refresh : Intent
        object LoadNextPage : Intent
    }

    data class UiState(
        val refreshing: Boolean = false,
        val loading: Boolean = false,
        val canFetchMore: Boolean = true,
        val comments: List<CommentModel> = emptyList(),
    )

    sealed interface Effect
}
