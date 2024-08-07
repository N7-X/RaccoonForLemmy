package com.livefast.eattrash.raccoonforlemmy.core.commonui.lemmyui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.livefast.eattrash.raccoonforlemmy.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforlemmy.core.commonui.components.VideoPlayer
import com.livefast.eattrash.raccoonforlemmy.core.l10n.messages.LocalStrings

@Composable
fun PostCardVideo(
    modifier: Modifier = Modifier,
    url: String,
    blurred: Boolean = false,
    autoLoadImages: Boolean = true,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    onOpen: (() -> Unit)? = null,
) {
    if (url.isEmpty()) {
        return
    }

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        if (blurred) {
            Column(
                modifier = Modifier.padding(vertical = Spacing.s),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.s),
            ) {
                Text(
                    text = LocalStrings.current.messageVideoNsfw,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Button(
                    onClick = {
                        onOpen?.invoke()
                    },
                ) {
                    Text(
                        text = LocalStrings.current.buttonLoad,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        } else {
            var shouldBeRendered by remember(autoLoadImages) { mutableStateOf(autoLoadImages) }
            var loading by remember { mutableStateOf(true) }
            if (shouldBeRendered) {
                VideoPlayer(
                    modifier = Modifier.fillMaxWidth(),
                    url = url,
                    onPlaybackStarted = {
                        loading = false
                    },
                )
                if (loading) {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .aspectRatio(16 / 9.0f)
                                .background(backgroundColor),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(25.dp),
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            } else {
                Button(
                    modifier = Modifier.padding(vertical = Spacing.s),
                    onClick = {
                        shouldBeRendered = true
                    },
                ) {
                    Text(
                        text = LocalStrings.current.buttonLoad,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}
