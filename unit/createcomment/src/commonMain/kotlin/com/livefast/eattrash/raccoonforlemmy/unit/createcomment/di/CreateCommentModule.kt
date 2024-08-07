package com.livefast.eattrash.raccoonforlemmy.unit.createcomment.di

import com.livefast.eattrash.raccoonforlemmy.unit.createcomment.CreateCommentMviModel
import com.livefast.eattrash.raccoonforlemmy.unit.createcomment.CreateCommentViewModel
import org.koin.dsl.module

val createCommentModule =
    module {
        factory<CreateCommentMviModel> { params ->
            CreateCommentViewModel(
                postId = params[0],
                parentId = params[1],
                editedCommentId = params[2],
                draftId = params[3],
                identityRepository = get(),
                commentRepository = get(),
                postRepository = get(),
                mediaRepository = get(),
                siteRepository = get(),
                themeRepository = get(),
                settingsRepository = get(),
                notificationCenter = get(),
                itemCache = get(),
                accountRepository = get(),
                draftRepository = get(),
                communityPreferredLanguageRepository = get(),
                lemmyValueCache = get(),
            )
        }
    }
