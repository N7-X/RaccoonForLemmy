package com.github.diegoberaldin.raccoonforlemmy.unit.replies.di

import com.github.diegoberaldin.raccoonforlemmy.unit.replies.InboxRepliesMviModel
import com.github.diegoberaldin.raccoonforlemmy.unit.replies.InboxRepliesViewModel
import org.koin.dsl.module

val inboxRepliesModule =
    module {
        factory<InboxRepliesMviModel> {
            InboxRepliesViewModel(
                userRepository = get(),
                identityRepository = get(),
                siteRepository = get(),
                commentRepository = get(),
                themeRepository = get(),
                settingsRepository = get(),
                hapticFeedback = get(),
                coordinator = get(),
                notificationCenter = get(),
                lemmyValueCache = get(),
            )
        }
    }
