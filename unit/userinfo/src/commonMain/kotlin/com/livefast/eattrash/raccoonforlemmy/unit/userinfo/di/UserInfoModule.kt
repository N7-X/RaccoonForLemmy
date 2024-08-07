package com.livefast.eattrash.raccoonforlemmy.unit.userinfo.di

import com.livefast.eattrash.raccoonforlemmy.unit.userinfo.UserInfoMviModel
import com.livefast.eattrash.raccoonforlemmy.unit.userinfo.UserInfoViewModel
import org.koin.dsl.module

val userInfoModule =
    module {
        factory<UserInfoMviModel> { params ->
            UserInfoViewModel(
                userId = params[0],
                username = params[1],
                otherInstance = params[2],
                userRepository = get(),
                settingsRepository = get(),
                siteRepository = get(),
                itemCache = get(),
            )
        }
    }
