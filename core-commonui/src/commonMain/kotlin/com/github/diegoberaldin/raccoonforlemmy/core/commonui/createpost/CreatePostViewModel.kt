package com.github.diegoberaldin.raccoonforlemmy.core.commonui.createpost

import cafe.adriel.voyager.core.model.ScreenModel
import com.github.diegoberaldin.raccoonforlemmy.core.appearance.repository.ThemeRepository
import com.github.diegoberaldin.raccoonforlemmy.core.architecture.DefaultMviModel
import com.github.diegoberaldin.raccoonforlemmy.core.architecture.MviModel
import com.github.diegoberaldin.raccoonforlemmy.core.persistence.repository.SettingsRepository
import com.github.diegoberaldin.raccoonforlemmy.core.utils.StringUtils.isValidUrl
import com.github.diegoberaldin.raccoonforlemmy.domain.identity.repository.IdentityRepository
import com.github.diegoberaldin.raccoonforlemmy.domain.lemmy.repository.PostRepository
import com.github.diegoberaldin.raccoonforlemmy.resources.MR.strings.message_invalid_field
import com.github.diegoberaldin.raccoonforlemmy.resources.MR.strings.message_missing_field
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class CreatePostViewModel(
    private val communityId: Int?,
    private val editedPostId: Int?,
    private val mvi: DefaultMviModel<CreatePostMviModel.Intent, CreatePostMviModel.UiState, CreatePostMviModel.Effect>,
    private val identityRepository: IdentityRepository,
    private val postRepository: PostRepository,
    private val themeRepository: ThemeRepository,
    private val settingsRepository: SettingsRepository,
) : ScreenModel,
    MviModel<CreatePostMviModel.Intent, CreatePostMviModel.UiState, CreatePostMviModel.Effect> by mvi {

    override fun onStarted() {
        mvi.onStarted()
        mvi.scope?.launch {
            themeRepository.postLayout.onEach { layout ->
                mvi.updateState { it.copy(postLayout = layout) }
            }.launchIn(this)
            settingsRepository.currentSettings.onEach { settings ->
                mvi.updateState {
                    it.copy(
                        separateUpAndDownVotes = settings.separateUpAndDownVotes,
                        autoLoadImages = settings.autoLoadImages,
                        fullHeightImages = settings.fullHeightImages,
                    )
                }
            }.launchIn(this)
        }
    }

    override fun reduce(intent: CreatePostMviModel.Intent) {
        when (intent) {
            is CreatePostMviModel.Intent.SetTitle -> {
                mvi.updateState {
                    it.copy(title = intent.value)
                }
            }

            is CreatePostMviModel.Intent.SetText -> {
                mvi.updateState {
                    it.copy(body = intent.value)
                }
            }


            is CreatePostMviModel.Intent.ChangeNsfw -> {
                mvi.updateState {
                    it.copy(nsfw = intent.value)
                }
            }


            is CreatePostMviModel.Intent.SetUrl -> {
                mvi.updateState {
                    it.copy(url = intent.value)
                }
            }

            is CreatePostMviModel.Intent.ImageSelected -> {
                loadImageAndObtainUrl(intent.value)
            }


            is CreatePostMviModel.Intent.ChangeSection -> mvi.updateState {
                it.copy(section = intent.value)
            }

            CreatePostMviModel.Intent.Send -> submit()
        }
    }

    private fun loadImageAndObtainUrl(bytes: ByteArray) {
        if (bytes.isEmpty()) {
            return
        }
        mvi.scope?.launch(Dispatchers.IO) {
            mvi.updateState { it.copy(loading = true) }
            val auth = identityRepository.authToken.value.orEmpty()
            val url = postRepository.uploadImage(auth, bytes)
            mvi.updateState {
                it.copy(
                    url = url.orEmpty(),
                    loading = false,
                )
            }
        }
    }

    private fun submit() {
        if (mvi.uiState.value.loading) {
            return
        }

        mvi.updateState {
            it.copy(
                titleError = null,
                urlError = null,
                bodyError = null,
            )
        }
        val title = uiState.value.title
        val body = uiState.value.body
        val url = uiState.value.url
        val nsfw = uiState.value.nsfw
        var valid = true
        if (title.isEmpty()) {
            mvi.updateState {
                it.copy(
                    titleError = message_missing_field.desc(),
                )
            }
            valid = false
        }
        if (body.isEmpty()) {
            mvi.updateState {
                it.copy(
                    bodyError = message_missing_field.desc(),
                )
            }
            valid = false
        }
        if (url.isNotEmpty() && !url.isValidUrl()) {
            mvi.updateState {
                it.copy(
                    urlError = message_invalid_field.desc(),
                )
            }
            valid = false
        }
        if (!valid) {
            return
        }

        mvi.updateState { it.copy(loading = true) }
        mvi.scope?.launch(Dispatchers.IO) {
            try {
                val auth = identityRepository.authToken.value.orEmpty()
                when {
                    communityId != null -> {
                        postRepository.create(
                            communityId = communityId,
                            title = title,
                            body = body,
                            url = url,
                            nsfw = nsfw,
                            auth = auth,
                        )
                    }

                    editedPostId != null -> {
                        postRepository.edit(
                            postId = editedPostId,
                            title = title,
                            body = body,
                            url = url,
                            nsfw = nsfw,
                            auth = auth,
                        )
                    }
                }
                mvi.emitEffect(CreatePostMviModel.Effect.Success)
            } catch (e: Throwable) {
                val message = e.message
                mvi.emitEffect(CreatePostMviModel.Effect.Failure(message))
            } finally {
                mvi.updateState { it.copy(loading = false) }
            }
        }
    }
}