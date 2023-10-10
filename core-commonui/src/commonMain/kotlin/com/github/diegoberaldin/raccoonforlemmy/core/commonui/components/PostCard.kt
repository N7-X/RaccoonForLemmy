package com.github.diegoberaldin.raccoonforlemmy.core.commonui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.github.diegoberaldin.raccoonforlemmy.core.appearance.data.PostLayout
import com.github.diegoberaldin.raccoonforlemmy.core.appearance.di.getThemeRepository
import com.github.diegoberaldin.raccoonforlemmy.core.appearance.theme.CornerSize
import com.github.diegoberaldin.raccoonforlemmy.core.appearance.theme.Spacing
import com.github.diegoberaldin.raccoonforlemmy.core.utils.toLocalPixel
import com.github.diegoberaldin.raccoonforlemmy.domain.lemmy.data.CommunityModel
import com.github.diegoberaldin.raccoonforlemmy.domain.lemmy.data.PostModel
import com.github.diegoberaldin.raccoonforlemmy.domain.lemmy.data.UserModel

@Composable
fun PostCard(
    modifier: Modifier = Modifier,
    post: PostModel,
    autoLoadImages: Boolean = true,
    hideAuthor: Boolean = false,
    postLayout: PostLayout = PostLayout.Card,
    separateUpAndDownVotes: Boolean = false,
    withOverflowBlurred: Boolean = true,
    blurNsfw: Boolean = true,
    options: List<String> = emptyList(),
    onOpenCommunity: ((CommunityModel) -> Unit)? = null,
    onOpenCreator: ((UserModel) -> Unit)? = null,
    onUpVote: (() -> Unit)? = null,
    onDownVote: (() -> Unit)? = null,
    onSave: (() -> Unit)? = null,
    onReply: (() -> Unit)? = null,
    onImageClick: ((String) -> Unit)? = null,
    onOptionSelected: ((Int) -> Unit)? = null,
) {
    val themeRepository = remember { getThemeRepository() }
    val fontScale by themeRepository.contentFontScale.collectAsState()
    CompositionLocalProvider(
        LocalDensity provides Density(
            density = LocalDensity.current.density,
            fontScale = fontScale,
        ),
    ) {
        Box(
            modifier = modifier.let {
                if (postLayout == PostLayout.Card) {
                    it.padding(horizontal = Spacing.xs).background(
                        color = MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp),
                        shape = RoundedCornerShape(CornerSize.l),
                    ).padding(Spacing.s)
                } else {
                    it
                }
            },
        ) {
            if (postLayout != PostLayout.Compact) {
                ExtendedPost(
                    post = post,
                    hideAuthor = hideAuthor,
                    backgroundColor = when (postLayout) {
                        PostLayout.Card -> MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp)
                        else -> MaterialTheme.colorScheme.background
                    },
                    withOverflowBlurred = withOverflowBlurred,
                    separateUpAndDownVotes = separateUpAndDownVotes,
                    autoLoadImages = autoLoadImages,
                    blurNsfw = blurNsfw,
                    options = options,
                    onOpenCommunity = onOpenCommunity,
                    onOpenCreator = onOpenCreator,
                    onUpVote = onUpVote,
                    onDownVote = onDownVote,
                    onSave = onSave,
                    onReply = onReply,
                    onImageClick = onImageClick,
                    onOptionSelected = onOptionSelected,
                )
            } else {
                CompactPost(
                    post = post,
                    hideAuthor = hideAuthor,
                    blurNsfw = blurNsfw,
                    separateUpAndDownVotes = separateUpAndDownVotes,
                    autoLoadImages = autoLoadImages,
                    options = options,
                    onOpenCommunity = onOpenCommunity,
                    onOpenCreator = onOpenCreator,
                    onUpVote = onUpVote,
                    onDownVote = onDownVote,
                    onSave = onSave,
                    onReply = onReply,
                    onImageClick = onImageClick,
                    onOptionSelected = onOptionSelected,
                )
            }
        }
    }
}

@Composable
private fun CompactPost(
    modifier: Modifier = Modifier,
    post: PostModel,
    autoLoadImages: Boolean = true,
    hideAuthor: Boolean,
    blurNsfw: Boolean,
    separateUpAndDownVotes: Boolean,
    options: List<String> = emptyList(),
    onOpenCommunity: ((CommunityModel) -> Unit)? = null,
    onOpenCreator: ((UserModel) -> Unit)? = null,
    onUpVote: (() -> Unit)? = null,
    onDownVote: (() -> Unit)? = null,
    onSave: (() -> Unit)? = null,
    onReply: (() -> Unit)? = null,
    onImageClick: ((String) -> Unit)? = null,
    onOptionSelected: ((Int) -> Unit)? = null,
) {
    Column(
        modifier = modifier.background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.spacedBy(Spacing.xxxs),
    ) {
        CommunityAndCreatorInfo(
            community = post.community,
            creator = post.creator.takeIf { !hideAuthor },
            onOpenCommunity = onOpenCommunity,
            onOpenCreator = onOpenCreator,
            autoLoadImages = autoLoadImages,
        )
        Row(
            modifier = Modifier.padding(horizontal = Spacing.s),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
        ) {
            PostCardTitle(
                modifier = Modifier.weight(0.75f),
                text = post.title,
                autoLoadImages = autoLoadImages,
            )
            PostCardImage(
                modifier = Modifier
                    .weight(0.25f)
                    .clip(RoundedCornerShape(CornerSize.s)),
                minHeight = Dp.Unspecified,
                maxHeight = Dp.Unspecified,
                imageUrl = post.imageUrl,
                autoLoadImages = autoLoadImages,
                loadButtonContent = @Composable {
                    Icon(imageVector = Icons.Default.Download, contentDescription = null)
                },
                blurred = blurNsfw && post.nsfw,
                onImageClick = onImageClick,
            )
        }
        PostCardFooter(
            comments = post.comments,
            separateUpAndDownVotes = separateUpAndDownVotes,
            score = post.score,
            upvotes = post.upvotes,
            downvotes = post.downvotes,
            upVoted = post.myVote > 0,
            downVoted = post.myVote < 0,
            saved = post.saved,
            onUpVote = onUpVote,
            onDownVote = onDownVote,
            onSave = onSave,
            onReply = onReply,
            date = post.publishDate,
            options = options,
            onOptionSelected = onOptionSelected,
        )
    }
}

@Composable
private fun ExtendedPost(
    modifier: Modifier = Modifier,
    post: PostModel,
    autoLoadImages: Boolean = true,
    hideAuthor: Boolean,
    blurNsfw: Boolean,
    separateUpAndDownVotes: Boolean,
    withOverflowBlurred: Boolean,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    options: List<String> = emptyList(),
    onOpenCommunity: ((CommunityModel) -> Unit)? = null,
    onOpenCreator: ((UserModel) -> Unit)? = null,
    onUpVote: (() -> Unit)? = null,
    onDownVote: (() -> Unit)? = null,
    onSave: (() -> Unit)? = null,
    onReply: (() -> Unit)? = null,
    onImageClick: ((String) -> Unit)? = null,
    onOptionSelected: ((Int) -> Unit)? = null,
) {
    Column(
        modifier = modifier.background(backgroundColor),
        verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
    ) {
        CommunityAndCreatorInfo(
            modifier = Modifier.padding(horizontal = Spacing.xxs),
            community = post.community,
            creator = post.creator.takeIf { !hideAuthor },
            onOpenCommunity = onOpenCommunity,
            onOpenCreator = onOpenCreator,
            autoLoadImages = autoLoadImages,
        )
        PostCardTitle(
            modifier = Modifier.padding(
                vertical = Spacing.xs,
                horizontal = Spacing.xs,
            ),
            text = post.title,
            autoLoadImages = autoLoadImages,
        )

        PostCardImage(
            modifier = Modifier.clip(RoundedCornerShape(CornerSize.xl)),
            imageUrl = post.imageUrl,
            blurred = blurNsfw && post.nsfw,
            onImageClick = onImageClick,
            autoLoadImages = autoLoadImages,
        )
        Box {
            val maxHeight = 200.dp
            val maxHeightPx = maxHeight.toLocalPixel()
            var textHeightPx by remember { mutableStateOf(0f) }
            PostCardBody(
                modifier = Modifier.let {
                    if (withOverflowBlurred) {
                        it.heightIn(max = maxHeight)
                    } else {
                        it
                    }
                }.padding(horizontal = Spacing.xs).onGloballyPositioned {
                    textHeightPx = it.size.toSize().height
                },
                text = post.text,
                autoLoadImages = autoLoadImages,
            )
            if (withOverflowBlurred && textHeightPx >= maxHeightPx) {
                Box(
                    modifier = Modifier.height(Spacing.xxl).fillMaxWidth()
                        .align(Alignment.BottomCenter).background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    backgroundColor,
                                ),
                            ),
                        ),
                )
            }
        }
        if (post.url != post.imageUrl) {
            PostLinkBanner(
                modifier = Modifier.padding(vertical = Spacing.xs),
                url = post.url?.takeIf { !it.looksLikeAnImage }.orEmpty(),
            )
        }
        PostCardFooter(
            comments = post.comments,
            separateUpAndDownVotes = separateUpAndDownVotes,
            score = post.score,
            upvotes = post.upvotes,
            downvotes = post.downvotes,
            upVoted = post.myVote > 0,
            downVoted = post.myVote < 0,
            saved = post.saved,
            onUpVote = onUpVote,
            onDownVote = onDownVote,
            onSave = onSave,
            onReply = onReply,
            date = post.publishDate,
            options = options,
            onOptionSelected = onOptionSelected,
        )
    }
}

private val PostModel.imageUrl: String
    get() = thumbnailUrl?.takeIf { it.isNotEmpty() } ?: run {
        url?.takeIf { it.looksLikeAnImage }
    }.orEmpty()

private val String.looksLikeAnImage: Boolean
    get() {
        val imageExtensions = listOf(".jpeg", ".jpg", ".png")
        return imageExtensions.any { this.endsWith(it) }
    }