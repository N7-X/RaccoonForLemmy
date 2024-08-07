package com.livefast.eattrash.raccoonforlemmy.unit.ban.di

import com.livefast.eattrash.raccoonforlemmy.unit.ban.BanUserMviModel
import com.livefast.eattrash.raccoonforlemmy.unit.ban.BanUserViewModel
import org.koin.dsl.module

val banModule =
    module {
        factory<BanUserMviModel> { params ->
            BanUserViewModel(
                userId = params[0],
                communityId = params[1],
                newValue = params[2],
                postId = params[3],
                commentId = params[4],
                identityRepository = get(),
                communityRepository = get(),
                notificationCenter = get(),
            )
        }
    }
