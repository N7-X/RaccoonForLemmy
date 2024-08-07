package com.livefast.eattrash.raccoonforlemmy.core.api.di

import com.livefast.eattrash.raccoonforlemmy.core.api.provider.DefaultServiceProvider
import com.livefast.eattrash.raccoonforlemmy.core.api.provider.ServiceProvider
import org.koin.core.qualifier.named
import org.koin.dsl.module

val coreApiModule =
    module {
        single<ServiceProvider>(named("default")) {
            DefaultServiceProvider()
        }
        single<ServiceProvider>(named("custom")) {
            DefaultServiceProvider()
        }
    }
