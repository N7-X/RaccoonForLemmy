package com.livefast.eattrash.raccoonforlemmy.core.resources.di

import com.livefast.eattrash.raccoonforlemmy.core.resources.CoreResources
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual fun getCoreResources(): CoreResources {
    return CoreResourcesDiHelper.coreResources
}

object CoreResourcesDiHelper : KoinComponent {
    val coreResources: CoreResources by inject()
}
