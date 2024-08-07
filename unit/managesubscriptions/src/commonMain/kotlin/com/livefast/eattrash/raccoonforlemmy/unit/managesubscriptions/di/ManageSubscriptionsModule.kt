package com.livefast.eattrash.raccoonforlemmy.unit.managesubscriptions.di

import com.livefast.eattrash.raccoonforlemmy.unit.managesubscriptions.ManageSubscriptionsMviModel
import com.livefast.eattrash.raccoonforlemmy.unit.managesubscriptions.ManageSubscriptionsViewModel
import com.livefast.eattrash.raccoonforlemmy.unit.multicommunity.di.multiCommunityModule
import org.koin.dsl.module

val manageSubscriptionsModule =
    module {
        includes(multiCommunityModule)

        factory<ManageSubscriptionsMviModel> {
            ManageSubscriptionsViewModel(
                identityRepository = get(),
                communityRepository = get(),
                accountRepository = get(),
                multiCommunityRepository = get(),
                hapticFeedback = get(),
                notificationCenter = get(),
                settingsRepository = get(),
                favoriteCommunityRepository = get(),
                communityPaginationManager = get(),
            )
        }
    }
