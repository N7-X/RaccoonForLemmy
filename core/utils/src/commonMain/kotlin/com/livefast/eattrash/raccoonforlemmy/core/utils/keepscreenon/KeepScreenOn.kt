package com.livefast.eattrash.raccoonforlemmy.core.utils.keepscreenon

import androidx.compose.runtime.Composable

interface KeepScreenOn {
    fun activate()

    fun deactivate()
}

@Composable
expect fun rememberKeepScreenOn(): KeepScreenOn
