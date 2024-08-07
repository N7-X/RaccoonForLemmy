package com.livefast.eattrash.raccoonforlemmy.core.commonui.lemmyui.di

import com.livefast.eattrash.raccoonforlemmy.core.commonui.lemmyui.FabNestedScrollConnection
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual fun getFabNestedScrollConnection(): FabNestedScrollConnection = LemmyUiDiHelper.fabNestedScrollConnection

object LemmyUiDiHelper : KoinComponent {
    val fabNestedScrollConnection: FabNestedScrollConnection by inject()
}
