package com.livefast.eattrash.raccoonforlemmy.unit.drawer.di

import com.livefast.eattrash.raccoonforlemmy.unit.drawer.cache.SubscriptionsCache
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual fun getSubscriptionsCache(): SubscriptionsCache = DrawerDiHelper.subscriptionsCache

internal object DrawerDiHelper : KoinComponent {
    val subscriptionsCache: SubscriptionsCache by inject()
}
