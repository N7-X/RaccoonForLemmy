package com.github.diegoberaldin.raccoonforlemmy.feature.inbox.messages.list

import cafe.adriel.voyager.core.model.ScreenModel
import com.github.diegoberaldin.raccoonforlemmy.core.architecture.DefaultMviModel
import com.github.diegoberaldin.raccoonforlemmy.core.architecture.MviModel
import com.github.diegoberaldin.raccoonforlemmy.core.notifications.NotificationCenter
import com.github.diegoberaldin.raccoonforlemmy.core.notifications.NotificationCenterContractKeys
import com.github.diegoberaldin.raccoonforlemmy.domain.identity.repository.IdentityRepository
import com.github.diegoberaldin.raccoonforlemmy.domain.lemmy.repository.CommentRepository
import com.github.diegoberaldin.raccoonforlemmy.domain.lemmy.repository.PrivateMessageRepository
import com.github.diegoberaldin.raccoonforlemmy.domain.lemmy.repository.SiteRepository
import com.github.diegoberaldin.raccoonforlemmy.domain.lemmy.repository.UserRepository
import com.github.diegoberaldin.raccoonforlemmy.feature.inbox.InboxCoordinator
import com.github.diegoberaldin.raccoonforlemmy.feature.inbox.main.InboxMviModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class InboxMessagesViewModel(
    private val mvi: DefaultMviModel<InboxMessagesMviModel.Intent, InboxMessagesMviModel.UiState, InboxMessagesMviModel.Effect>,
    private val identityRepository: IdentityRepository,
    private val siteRepository: SiteRepository,
    private val messageRepository: PrivateMessageRepository,
    private val userRepository: UserRepository,
    private val coordinator: InboxCoordinator,
    private val notificationCenter: NotificationCenter,
) : ScreenModel,
    MviModel<InboxMessagesMviModel.Intent, InboxMessagesMviModel.UiState, InboxMessagesMviModel.Effect> by mvi {

    private var currentPage: Int = 1

    init {
        notificationCenter.addObserver({
            handleLogout()
        }, this::class.simpleName.orEmpty(), NotificationCenterContractKeys.Logout)
    }

    fun finalize() {
        notificationCenter.removeObserver(this::class.simpleName.orEmpty())
    }

    override fun onStarted() {
        mvi.onStarted()
        mvi.scope?.launch {
            coordinator.effects.onEach {
                when (it) {
                    InboxMviModel.Effect.Refresh -> refresh()
                }
            }.launchIn(this)
            coordinator.unreadOnly.onEach {
                if (it != uiState.value.unreadOnly) {
                    changeUnreadOnly(it)
                }
            }.launchIn(this)
            launch(Dispatchers.IO) {
                val auth = identityRepository.authToken.value.orEmpty()
                val currentUserId = siteRepository.getCurrentUser(auth)?.id ?: 0
                mvi.updateState { it.copy(currentUserId = currentUserId) }
            }
        }
    }

    override fun reduce(intent: InboxMessagesMviModel.Intent) {
        when (intent) {
            InboxMessagesMviModel.Intent.LoadNextPage -> loadNextPage()
            InboxMessagesMviModel.Intent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        currentPage = 1
        mvi.updateState { it.copy(canFetchMore = true, refreshing = true) }
        loadNextPage()
        updateUnreadItems()
    }

    private fun changeUnreadOnly(value: Boolean) {
        mvi.updateState { it.copy(unreadOnly = value) }
        refresh()
    }

    private fun loadNextPage() {
        val currentState = mvi.uiState.value
        if (!currentState.canFetchMore || currentState.loading) {
            mvi.updateState { it.copy(refreshing = false) }
            return
        }

        mvi.scope?.launch(Dispatchers.IO) {
            mvi.updateState { it.copy(loading = true) }
            val auth = identityRepository.authToken.value
            val refreshing = currentState.refreshing
            val unreadOnly = currentState.unreadOnly
            val itemList = messageRepository.getAll(
                auth = auth,
                page = currentPage,
                unreadOnly = unreadOnly,
            ).groupBy {
                val creatorId = it.creator?.id ?: 0
                val recipientId = it.recipient?.id ?: 0
                listOf(creatorId, recipientId).sorted().toString()
            }.mapNotNull {
                val messages = it.value.sortedBy { m -> m.publishDate }
                messages.lastOrNull()
            }
            currentPage++
            val canFetchMore = itemList.size >= CommentRepository.DEFAULT_PAGE_SIZE
            mvi.updateState {
                val newItems = if (refreshing) {
                    itemList
                } else {
                    it.chats + itemList
                }
                it.copy(
                    chats = newItems,
                    loading = false,
                    canFetchMore = canFetchMore,
                    refreshing = false,
                )
            }
        }
    }

    private fun updateUnreadItems() {
        mvi.scope?.launch(Dispatchers.IO) {
            val auth = identityRepository.authToken.value
            val unreadCount = if (!auth.isNullOrEmpty()) {
                val mentionCount =
                    userRepository.getMentions(auth, page = 1, limit = 50).count()
                val replyCount =
                    userRepository.getReplies(auth, page = 1, limit = 50).count()
                mentionCount + replyCount
            } else {
                0
            }
            mvi.emitEffect(InboxMessagesMviModel.Effect.UpdateUnreadItems(unreadCount))
        }
    }

    private fun handleLogout() {
        mvi.updateState { it.copy(chats = emptyList()) }
    }
}