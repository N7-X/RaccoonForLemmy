package com.livefast.eattrash.raccoonforlemmy.feature.profile.menu

import cafe.adriel.voyager.core.model.ScreenModel
import com.livefast.eattrash.raccoonforlemmy.core.architecture.MviModel

interface ProfileSideMenuMviModel :
    MviModel<ProfileSideMenuMviModel.Intent, ProfileSideMenuMviModel.State, ProfileSideMenuMviModel.Effect>,
    ScreenModel {
    sealed interface Intent

    data class State(
        val isModerator: Boolean = false,
        val canCreateCommunity: Boolean = false,
        val isBookmarksVisible: Boolean = true,
    )

    sealed interface Effect
}
