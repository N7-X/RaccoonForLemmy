package com.github.diegoberaldin.raccoonforlemmy.unit.drawer.di

import com.github.diegoberaldin.raccoonforlemmy.unit.drawer.ModalDrawerMviModel
import com.github.diegoberaldin.raccoonforlemmy.unit.drawer.ModalDrawerViewModel
import com.github.diegoberaldin.raccoonforlemmy.unit.drawer.cache.DefaultSubscriptionsCache
import com.github.diegoberaldin.raccoonforlemmy.unit.drawer.cache.SubscriptionsCache
import org.koin.dsl.module

val drawerModule =
    module {
        factory<ModalDrawerMviModel> {
            ModalDrawerViewModel(
                apiConfigurationRepository = get(),
                siteRepository = get(),
                identityRepository = get(),
                accountRepository = get(),
                communityRepository = get(),
                multiCommunityRepository = get(),
                settingsRepository = get(),
                favoriteCommunityRepository = get(),
                notificationCenter = get(),
                communityPaginationManager = get(),
                subscriptionsCache = get(),
            )
        }
        single<SubscriptionsCache> {
            DefaultSubscriptionsCache(
                identityRepository = get(),
                communityPaginationManager = get(),
            )
        }
    }
