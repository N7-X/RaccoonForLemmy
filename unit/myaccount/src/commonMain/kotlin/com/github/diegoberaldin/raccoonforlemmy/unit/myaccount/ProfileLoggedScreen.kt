package com.github.diegoberaldin.raccoonforlemmy.unit.myaccount

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.github.diegoberaldin.raccoonforlemmy.core.appearance.data.PostLayout
import com.github.diegoberaldin.raccoonforlemmy.core.appearance.theme.Spacing
import com.github.diegoberaldin.raccoonforlemmy.core.architecture.bindToLifecycle
import com.github.diegoberaldin.raccoonforlemmy.core.commonui.components.SectionSelector
import com.github.diegoberaldin.raccoonforlemmy.core.commonui.detailopener.api.getDetailOpener
import com.github.diegoberaldin.raccoonforlemmy.core.commonui.lemmyui.CommentCard
import com.github.diegoberaldin.raccoonforlemmy.core.commonui.lemmyui.CommentCardPlaceholder
import com.github.diegoberaldin.raccoonforlemmy.core.commonui.lemmyui.ModeratorZoneAction
import com.github.diegoberaldin.raccoonforlemmy.core.commonui.lemmyui.Option
import com.github.diegoberaldin.raccoonforlemmy.core.commonui.lemmyui.OptionId
import com.github.diegoberaldin.raccoonforlemmy.core.commonui.lemmyui.PostCard
import com.github.diegoberaldin.raccoonforlemmy.core.commonui.lemmyui.PostCardPlaceholder
import com.github.diegoberaldin.raccoonforlemmy.core.commonui.lemmyui.ProfileLoggedSection
import com.github.diegoberaldin.raccoonforlemmy.core.commonui.lemmyui.UserHeader
import com.github.diegoberaldin.raccoonforlemmy.core.commonui.lemmyui.toModeratorZoneAction
import com.github.diegoberaldin.raccoonforlemmy.core.commonui.modals.ModeratorZoneBottomSheet
import com.github.diegoberaldin.raccoonforlemmy.core.commonui.modals.ShareBottomSheet
import com.github.diegoberaldin.raccoonforlemmy.core.l10n.LocalXmlStrings
import com.github.diegoberaldin.raccoonforlemmy.core.navigation.TabNavigationSection
import com.github.diegoberaldin.raccoonforlemmy.core.navigation.di.getNavigationCoordinator
import com.github.diegoberaldin.raccoonforlemmy.core.notifications.NotificationCenterEvent
import com.github.diegoberaldin.raccoonforlemmy.core.notifications.di.getNotificationCenter
import com.github.diegoberaldin.raccoonforlemmy.core.persistence.di.getSettingsRepository
import com.github.diegoberaldin.raccoonforlemmy.core.utils.compose.rememberCallback
import com.github.diegoberaldin.raccoonforlemmy.core.utils.compose.rememberCallbackArgs
import com.github.diegoberaldin.raccoonforlemmy.domain.lemmy.data.CommentModel
import com.github.diegoberaldin.raccoonforlemmy.domain.lemmy.data.PostModel
import com.github.diegoberaldin.raccoonforlemmy.unit.drafts.DraftsScreen
import com.github.diegoberaldin.raccoonforlemmy.unit.managesubscriptions.ManageSubscriptionsScreen
import com.github.diegoberaldin.raccoonforlemmy.unit.moddedcontents.comments.ModdedCommentsScreen
import com.github.diegoberaldin.raccoonforlemmy.unit.moddedcontents.posts.ModdedPostsScreen
import com.github.diegoberaldin.raccoonforlemmy.unit.modlog.ModlogScreen
import com.github.diegoberaldin.raccoonforlemmy.unit.myaccount.components.ProfileShortcutSection
import com.github.diegoberaldin.raccoonforlemmy.unit.rawcontent.RawContentDialog
import com.github.diegoberaldin.raccoonforlemmy.unit.reportlist.ReportListScreen
import com.github.diegoberaldin.raccoonforlemmy.unit.saveditems.SavedItemsScreen
import com.github.diegoberaldin.raccoonforlemmy.unit.web.WebViewScreen
import com.github.diegoberaldin.raccoonforlemmy.unit.zoomableimage.ZoomableImageScreen
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

object ProfileLoggedScreen : Tab {

    override val options: TabOptions
        @Composable get() {
            return TabOptions(0u, "")
        }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun Content() {
        val model = getScreenModel<ProfileLoggedMviModel>()
        model.bindToLifecycle(key)
        val uiState by model.uiState.collectAsState()
        val user = uiState.user
        val notificationCenter = remember { getNotificationCenter() }
        val navigationCoordinator = remember { getNavigationCoordinator() }
        val lazyListState = rememberLazyListState()
        var rawContent by remember { mutableStateOf<Any?>(null) }
        val detailOpener = remember { getDetailOpener() }
        val settingsRepository = remember { getSettingsRepository() }
        val settings by settingsRepository.currentSettings.collectAsState()

        LaunchedEffect(navigationCoordinator) {
            navigationCoordinator.onDoubleTabSelection.onEach { section ->
                if (section == TabNavigationSection.Profile) {
                    lazyListState.scrollToItem(0)
                }
            }.launchIn(this)
        }
        LaunchedEffect(notificationCenter) {
            notificationCenter.subscribe(NotificationCenterEvent.PostCreated::class).onEach {
                model.reduce(ProfileLoggedMviModel.Intent.Refresh)
            }.launchIn(this)

            notificationCenter.subscribe(NotificationCenterEvent.CommentCreated::class).onEach {
                model.reduce(ProfileLoggedMviModel.Intent.Refresh)
            }.launchIn(this)

            notificationCenter.subscribe(NotificationCenterEvent.ModeratorZoneActionSelected::class)
                .onEach {
                    val action = it.value.toModeratorZoneAction()
                    when (action) {
                        ModeratorZoneAction.GlobalModLog -> {
                            navigationCoordinator.pushScreen(ModlogScreen())
                        }

                        ModeratorZoneAction.GlobalReports -> {
                            navigationCoordinator.pushScreen(ReportListScreen())
                        }

                        ModeratorZoneAction.ModeratedComments -> {
                            navigationCoordinator.pushScreen(ModdedCommentsScreen())
                        }

                        ModeratorZoneAction.ModeratedPosts -> {
                            navigationCoordinator.pushScreen(ModdedPostsScreen())
                        }
                    }
                }.launchIn(this)
        }

        if (user != null) {
            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = Spacing.xxxs),
                verticalArrangement = Arrangement.spacedBy(Spacing.s),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                val pullRefreshState = rememberPullRefreshState(
                    refreshing = uiState.refreshing,
                    onRefresh = rememberCallback(model) {
                        model.reduce(ProfileLoggedMviModel.Intent.Refresh)
                    },
                )
                Box(
                    modifier = Modifier.pullRefresh(pullRefreshState),
                ) {
                    LazyColumn(
                        state = lazyListState,
                    ) {
                        item {
                            UserHeader(
                                user = user,
                                autoLoadImages = uiState.autoLoadImages,
                                onOpenImage = rememberCallbackArgs { url ->
                                    navigationCoordinator.pushScreen(ZoomableImageScreen(url))
                                },
                            )
                        }
                        item {
                            ProfileShortcutSection(
                                modifier = Modifier.padding(bottom = Spacing.xs),
                                isMod = uiState.moderatedCommunityIds.isNotEmpty(),
                                onOpenSaved = rememberCallback {
                                    navigationCoordinator.pushScreen(SavedItemsScreen())
                                },
                                onOpenSubscriptions = rememberCallback {
                                    navigationCoordinator.pushScreen(ManageSubscriptionsScreen())
                                },
                                onOpenDrafts = rememberCallback {
                                    navigationCoordinator.pushScreen(DraftsScreen())
                                },
                                onOpenModeratorZone = rememberCallback {
                                    val screen = ModeratorZoneBottomSheet()
                                    navigationCoordinator.showBottomSheet(screen)
                                },
                            )
                        }
                        item {
                            HorizontalDivider()
                        }
                        item {
                            SectionSelector(
                                modifier = Modifier.padding(bottom = Spacing.xs),
                                titles = listOf(
                                    LocalXmlStrings.current.profileSectionPosts,
                                    LocalXmlStrings.current.profileSectionComments,
                                ),
                                currentSection = when (uiState.section) {
                                    ProfileLoggedSection.Comments -> 1
                                    else -> 0
                                },
                                onSectionSelected = rememberCallbackArgs(model) { idx ->
                                    val section = when (idx) {
                                        1 -> ProfileLoggedSection.Comments
                                        else -> ProfileLoggedSection.Posts
                                    }
                                    model.reduce(
                                        ProfileLoggedMviModel.Intent.ChangeSection(
                                            section
                                        )
                                    )
                                },
                            )
                        }
                        if (uiState.section == ProfileLoggedSection.Posts) {
                            if (uiState.posts.isEmpty() && uiState.loading && !uiState.initial) {
                                items(5) {
                                    PostCardPlaceholder(
                                        postLayout = uiState.postLayout,
                                    )
                                    if (uiState.postLayout != PostLayout.Card) {
                                        HorizontalDivider(modifier = Modifier.padding(vertical = Spacing.s))
                                    } else {
                                        Spacer(modifier = Modifier.height(Spacing.s))
                                    }
                                }
                            }
                            items(
                                items = uiState.posts,
                                key = { it.id.toString() + (it.updateDate ?: it.publishDate) },
                            ) { post ->
                                PostCard(
                                    post = post,
                                    postLayout = uiState.postLayout,
                                    limitBodyHeight = true,
                                    fullHeightImage = uiState.fullHeightImages,
                                    voteFormat = uiState.voteFormat,
                                    autoLoadImages = uiState.autoLoadImages,
                                    preferNicknames = uiState.preferNicknames,
                                    showScores = uiState.showScores,
                                    hideAuthor = true,
                                    blurNsfw = false,
                                    onClick = rememberCallback {
                                        detailOpener.openPostDetail(post)
                                    },
                                    onOpenCommunity = rememberCallbackArgs { community, instance ->
                                        detailOpener.openCommunityDetail(community, instance)
                                    },
                                    onOpenCreator = rememberCallbackArgs { user, instance ->
                                        detailOpener.openUserDetail(user, instance)
                                    },
                                    onOpenPost = rememberCallbackArgs { p, instance ->
                                        detailOpener.openPostDetail(p, instance)
                                    },
                                    onOpenWeb = rememberCallbackArgs { url ->
                                        navigationCoordinator.pushScreen(
                                            WebViewScreen(url)
                                        )
                                    },
                                    onOpenImage = rememberCallbackArgs { url ->
                                        navigationCoordinator.pushScreen(
                                            ZoomableImageScreen(url),
                                        )
                                    },
                                    onUpVote = rememberCallback(model) {
                                        model.reduce(
                                            ProfileLoggedMviModel.Intent.UpVotePost(
                                                id = post.id,
                                            )
                                        )
                                    },
                                    onDownVote = rememberCallback(model) {
                                        model.reduce(
                                            ProfileLoggedMviModel.Intent.DownVotePost(
                                                id = post.id,
                                            )
                                        )
                                    },
                                    onSave = rememberCallback(model) {
                                        model.reduce(
                                            ProfileLoggedMviModel.Intent.SavePost(
                                                id = post.id,
                                            )
                                        )
                                    },
                                    options = buildList {
                                        add(
                                            Option(
                                                OptionId.Share,
                                                LocalXmlStrings.current.postActionShare
                                            )
                                        )
                                        add(
                                            Option(
                                                OptionId.SeeRaw,
                                                LocalXmlStrings.current.postActionSeeRaw
                                            )
                                        )
                                        add(
                                            Option(
                                                OptionId.Edit,
                                                LocalXmlStrings.current.postActionEdit
                                            )
                                        )
                                        add(
                                            Option(
                                                OptionId.Delete,
                                                LocalXmlStrings.current.commentActionDelete
                                            )
                                        )
                                    },
                                    onOptionSelected = rememberCallbackArgs(model) { optionId ->
                                        when (optionId) {
                                            OptionId.Delete -> model.reduce(
                                                ProfileLoggedMviModel.Intent.DeletePost(post.id)
                                            )

                                            OptionId.Edit -> {
                                                detailOpener.openCreatePost(
                                                    editedPost = post,
                                                )
                                            }

                                            OptionId.SeeRaw -> {
                                                rawContent = post
                                            }

                                            OptionId.Share -> {
                                                val urls = listOfNotNull(
                                                    post.originalUrl,
                                                    "https://${uiState.instance}/post/${post.id}"
                                                ).distinct()
                                                if (urls.size == 1) {
                                                    model.reduce(
                                                        ProfileLoggedMviModel.Intent.Share(
                                                            urls.first()
                                                        )
                                                    )
                                                } else {
                                                    val screen = ShareBottomSheet(urls = urls)
                                                    navigationCoordinator.showBottomSheet(screen)
                                                }
                                            }

                                            else -> Unit
                                        }
                                    },
                                )
                                if (uiState.postLayout != PostLayout.Card) {
                                    HorizontalDivider(modifier = Modifier.padding(vertical = Spacing.s))
                                } else {
                                    Spacer(modifier = Modifier.height(Spacing.s))
                                }
                            }

                            if (uiState.posts.isEmpty() && !uiState.loading && !uiState.initial) {
                                item {
                                    Text(
                                        modifier = Modifier.fillMaxWidth()
                                            .padding(top = Spacing.xs),
                                        textAlign = TextAlign.Center,
                                        text = LocalXmlStrings.current.messageEmptyList,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onBackground,
                                    )
                                }
                            }
                        } else {
                            if (uiState.comments.isEmpty() && uiState.loading && uiState.initial) {
                                items(5) {
                                    CommentCardPlaceholder(hideAuthor = true)
                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = Spacing.xxxs),
                                        thickness = 0.25.dp
                                    )
                                }
                            }
                            items(
                                items = uiState.comments,
                                key = { it.id.toString() + (it.updateDate ?: it.publishDate) },
                            ) { comment ->
                                CommentCard(modifier = Modifier.background(MaterialTheme.colorScheme.background),
                                    comment = comment,
                                    voteFormat = uiState.voteFormat,
                                    autoLoadImages = uiState.autoLoadImages,
                                    showScores = uiState.showScores,
                                    hideCommunity = false,
                                    hideAuthor = true,
                                    hideIndent = true,
                                    onImageClick = rememberCallbackArgs { url ->
                                        navigationCoordinator.pushScreen(ZoomableImageScreen(url))
                                    },
                                    onOpenCommunity = rememberCallbackArgs { community, instance ->
                                        detailOpener.openCommunityDetail(community, instance)
                                    },
                                    onClick = rememberCallback {
                                        detailOpener.openPostDetail(
                                            post = PostModel(id = comment.postId),
                                            highlightCommentId = comment.id,
                                        )
                                    },
                                    onUpVote = rememberCallback(model) {
                                        model.reduce(
                                            ProfileLoggedMviModel.Intent.UpVoteComment(
                                                id = comment.id,
                                            )
                                        )
                                    },
                                    onDownVote = rememberCallback(model) {
                                        model.reduce(
                                            ProfileLoggedMviModel.Intent.DownVoteComment(
                                                id = comment.id,
                                            )
                                        )
                                    },
                                    onSave = rememberCallback(model) {
                                        model.reduce(
                                            ProfileLoggedMviModel.Intent.SaveComment(
                                                id = comment.id,
                                            )
                                        )
                                    },
                                    options = buildList {
                                        add(
                                            Option(
                                                OptionId.SeeRaw,
                                                LocalXmlStrings.current.postActionSeeRaw
                                            )
                                        )
                                        add(
                                            Option(
                                                OptionId.Edit,
                                                LocalXmlStrings.current.postActionEdit
                                            )
                                        )
                                        add(
                                            Option(
                                                OptionId.Delete,
                                                LocalXmlStrings.current.commentActionDelete
                                            )
                                        )
                                    },
                                    onOptionSelected = rememberCallbackArgs(model) { optionId ->
                                        when (optionId) {
                                            OptionId.Delete -> {
                                                model.reduce(
                                                    ProfileLoggedMviModel.Intent.DeleteComment(
                                                        comment.id
                                                    )
                                                )
                                            }

                                            OptionId.Edit -> {
                                                detailOpener.openReply(
                                                    editedComment = comment,
                                                )
                                            }

                                            OptionId.SeeRaw -> {
                                                rawContent = comment
                                            }

                                            else -> Unit
                                        }
                                    })
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = Spacing.xxxs),
                                    thickness = 0.25.dp
                                )
                            }

                            if (uiState.comments.isEmpty() && !uiState.loading && !uiState.initial) {
                                item {
                                    Text(
                                        modifier = Modifier.fillMaxWidth()
                                            .padding(top = Spacing.xs),
                                        textAlign = TextAlign.Center,
                                        text = LocalXmlStrings.current.messageEmptyList,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onBackground,
                                    )
                                }
                            }
                        }
                        item {
                            if (!uiState.loading && !uiState.refreshing && uiState.canFetchMore) {
                                if (settings.infiniteScrollEnabled) {
                                    model.reduce(ProfileLoggedMviModel.Intent.LoadNextPage)
                                } else {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(top = Spacing.s),
                                        horizontalArrangement = Arrangement.Center,
                                    ) {
                                        Button(
                                            onClick = rememberCallback(model) {
                                                model.reduce(ProfileLoggedMviModel.Intent.LoadNextPage)
                                            },
                                        ) {
                                            Text(
                                                text = if (uiState.section == ProfileLoggedSection.Posts) {
                                                    LocalXmlStrings.current.postListLoadMorePosts
                                                } else {
                                                    LocalXmlStrings.current.postDetailLoadMoreComments
                                                },
                                                style = MaterialTheme.typography.labelSmall,
                                            )
                                        }
                                    }
                                }
                            }
                            if (uiState.loading && !uiState.refreshing) {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(Spacing.xs),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(25.dp),
                                        color = MaterialTheme.colorScheme.primary,
                                    )
                                }
                            }
                        }
                        item {
                            Spacer(modifier = Modifier.height(Spacing.xxxl))
                        }
                    }

                    PullRefreshIndicator(
                        refreshing = uiState.refreshing,
                        state = pullRefreshState,
                        modifier = Modifier.align(Alignment.TopCenter),
                        backgroundColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }
        }

        if (rawContent != null) {
            when (val content = rawContent) {
                is PostModel -> {
                    RawContentDialog(
                        title = content.title,
                        publishDate = content.publishDate,
                        updateDate = content.updateDate,
                        url = content.url,
                        text = content.text,
                        upVotes = content.upvotes,
                        downVotes = content.downvotes,
                        onDismiss = rememberCallback {
                            rawContent = null
                        },
                        onQuote = rememberCallbackArgs { quotation ->
                            rawContent = null
                            if (quotation != null) {
                                detailOpener.openReply(
                                    originalPost = content,
                                    initialText = buildString {
                                        append("> ")
                                        append(quotation)
                                        append("\n\n")
                                    },
                                )
                            }
                        },
                    )
                }

                is CommentModel -> {
                    RawContentDialog(
                        text = content.text,
                        upVotes = content.upvotes,
                        downVotes = content.downvotes,
                        publishDate = content.publishDate,
                        updateDate = content.updateDate,
                        onDismiss = {
                            rawContent = null
                        },
                        onQuote = rememberCallbackArgs { quotation ->
                            rawContent = null
                            if (quotation != null) {
                                detailOpener.openReply(
                                    originalComment = content,
                                    initialText = buildString {
                                        append("> ")
                                        append(quotation)
                                        append("\n\n")
                                    },
                                )
                            }
                        },
                    )
                }
            }
        }
    }
}
