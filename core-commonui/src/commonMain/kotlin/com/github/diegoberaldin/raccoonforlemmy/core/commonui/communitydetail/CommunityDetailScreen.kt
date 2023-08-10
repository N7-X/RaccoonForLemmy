package com.github.diegoberaldin.raccoonforlemmy.core.commonui.communitydetail

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.Icon
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.diegoberaldin.racconforlemmy.core.utils.onClick
import com.github.diegoberaldin.racconforlemmy.core.utils.toLocalPixel
import com.github.diegoberaldin.raccoonforlemmy.core.appearance.theme.Spacing
import com.github.diegoberaldin.raccoonforlemmy.core.architecture.bindToLifecycle
import com.github.diegoberaldin.raccoonforlemmy.core.commonui.di.getCommunityDetailScreenViewModel
import com.github.diegoberaldin.raccoonforlemmy.core.commonui.postdetail.PostDetailScreen
import com.github.diegoberaldin.raccoonforlemmy.domain.lemmy.data.CommunityModel
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

class CommunityDetailScreen(
    private val community: CommunityModel,
    private val onBack: () -> Unit,
) : Screen {
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
    @Composable
    override fun Content() {
        val model = rememberScreenModel { getCommunityDetailScreenViewModel(community) }
        model.bindToLifecycle(key)
        val uiState by model.uiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        Scaffold(
            modifier = Modifier.background(MaterialTheme.colorScheme.surface).padding(Spacing.xs),
            topBar =
            {
                val communityName = community.name
                val communityHost = community.host
                TopAppBar(
                    title = {
                        Text(
                            modifier = Modifier.padding(horizontal = Spacing.s),
                            text = buildString {
                                append(communityName)
                                if (communityHost.isNotEmpty()) {
                                    append("@$communityHost")
                                }
                            },
                        )
                    },
                    navigationIcon = {
                        Image(
                            modifier = Modifier.onClick {
                                onBack()
                            },
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                        )
                    },
                )
            },
        ) { padding ->
            val community = uiState.community
            val pullRefreshState = rememberPullRefreshState(uiState.refreshing, {
                model.reduce(CommunityDetailMviModel.Intent.Refresh)
            })
            Box(
                modifier = Modifier.pullRefresh(pullRefreshState),
            ) {
                var width by remember { mutableStateOf(0f) }
                LazyColumn(
                    modifier = Modifier.padding(padding).onGloballyPositioned {
                        width = it.size.toSize().width
                    },
                    verticalArrangement = Arrangement.spacedBy(Spacing.xs),
                ) {
                    item {
                        val communityIcon = community.icon.orEmpty()
                        val communityTitle = community.title

                        val iconSize = 50.dp
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                val banner = community.banner.orEmpty()
                                if (banner.isNotEmpty()) {
                                    val painterResource = asyncPainterResource(banner)
                                    KamelImage(
                                        modifier = Modifier.fillMaxWidth().aspectRatio(2.25f),
                                        resource = painterResource,
                                        contentScale = ContentScale.FillBounds,
                                        contentDescription = null,
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier.fillMaxWidth().aspectRatio(2.5f),
                                    )
                                }
                                Column(
                                    modifier = Modifier.graphicsLayer(translationY = -(iconSize / 2).toLocalPixel()),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
                                ) {
                                    if (communityIcon.isNotEmpty()) {
                                        val painterResource =
                                            asyncPainterResource(data = communityIcon)
                                        KamelImage(
                                            modifier = Modifier.padding(Spacing.xxxs).size(iconSize)
                                                .clip(RoundedCornerShape(iconSize / 2)),
                                            resource = painterResource,
                                            contentDescription = null,
                                            contentScale = ContentScale.FillBounds,
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier.padding(Spacing.xxxs)
                                                .size(iconSize)
                                                .background(
                                                    color = MaterialTheme.colorScheme.primary,
                                                    shape = RoundedCornerShape(iconSize / 2),
                                                ),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Text(
                                                text = community.name.firstOrNull()?.toString()
                                                    .orEmpty()
                                                    .uppercase(),
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = MaterialTheme.colorScheme.onPrimary,
                                            )
                                        }
                                    }
                                    Text(
                                        text = buildString {
                                            append(communityTitle)
                                        },
                                        style = MaterialTheme.typography.headlineSmall,
                                    )
                                }
                            }
                        }
                    }
                    items(uiState.posts, key = { it.id.toString() + it.myVote }) { post ->
                        val dismissState = rememberDismissState(
                            confirmStateChange = {
                                when (it) {
                                    DismissValue.DismissedToEnd -> {
                                        model.reduce(
                                            CommunityDetailMviModel.Intent.DownVotePost(
                                                post = post,
                                            ),
                                        )
                                    }

                                    DismissValue.DismissedToStart -> {
                                        model.reduce(
                                            CommunityDetailMviModel.Intent.UpVotePost(
                                                post = post,
                                            ),
                                        )
                                    }

                                    else -> Unit
                                }
                                false
                            },
                        )
                        var willDismissDirection: DismissDirection? by remember {
                            mutableStateOf(null)
                        }
                        val threshold = 0.15f
                        LaunchedEffect(Unit) {
                            snapshotFlow { dismissState.offset.value }.collect {
                                willDismissDirection = when {
                                    it > width * threshold -> DismissDirection.StartToEnd
                                    it < -width * threshold -> DismissDirection.EndToStart
                                    else -> null
                                }
                            }
                        }
                        LaunchedEffect(willDismissDirection) {
                            if (willDismissDirection != null) {
                                model.reduce(CommunityDetailMviModel.Intent.HapticIndication)
                            }
                        }
                        SwipeToDismiss(
                            state = dismissState,
                            directions = setOf(
                                DismissDirection.StartToEnd,
                                DismissDirection.EndToStart,
                            ),
                            dismissThresholds = {
                                FractionalThreshold(threshold)
                            },
                            background = {
                                val direction =
                                    dismissState.dismissDirection ?: return@SwipeToDismiss
                                val color by animateColorAsState(
                                    when (dismissState.targetValue) {
                                        DismissValue.Default -> Color.Transparent
                                        DismissValue.DismissedToEnd -> MaterialTheme.colorScheme.secondary
                                        DismissValue.DismissedToStart,
                                        -> MaterialTheme.colorScheme.secondary
                                    },
                                )
                                val alignment = when (direction) {
                                    DismissDirection.StartToEnd -> Alignment.CenterStart
                                    DismissDirection.EndToStart -> Alignment.CenterEnd
                                }
                                val icon = when (direction) {
                                    DismissDirection.StartToEnd -> Icons.Default.ThumbDown
                                    DismissDirection.EndToStart -> Icons.Default.ThumbUp
                                }

                                Box(
                                    Modifier.fillMaxSize().background(color)
                                        .padding(horizontal = 20.dp),
                                    contentAlignment = alignment,
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                    )
                                }
                            },
                        ) {
                            PostCard(
                                modifier = Modifier.onClick {
                                    navigator.push(
                                        PostDetailScreen(
                                            post = post,
                                            onBack = {
                                                navigator.pop()
                                            },
                                        ),
                                    )
                                },
                                post = post,
                                onUpVote = {
                                    model.reduce(
                                        CommunityDetailMviModel.Intent.UpVotePost(
                                            post = post,
                                            feedback = true,
                                        ),
                                    )
                                },
                                onDownVote = {
                                    model.reduce(
                                        CommunityDetailMviModel.Intent.DownVotePost(
                                            post = post,
                                            feedback = true,
                                        ),
                                    )
                                },
                                onSave = {
                                    model.reduce(
                                        CommunityDetailMviModel.Intent.SavePost(
                                            post = post,
                                            feedback = true,
                                        ),
                                    )
                                },
                            )
                        }
                    }
                    item {
                        if (!uiState.loading && !uiState.refreshing && uiState.canFetchMore) {
                            model.reduce(CommunityDetailMviModel.Intent.LoadNextPage)
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
                }

                PullRefreshIndicator(
                    refreshing = uiState.refreshing,
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter),
                    backgroundColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}