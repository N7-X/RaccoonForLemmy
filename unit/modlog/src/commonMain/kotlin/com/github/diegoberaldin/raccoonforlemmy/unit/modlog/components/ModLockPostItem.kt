package com.github.diegoberaldin.raccoonforlemmy.unit.modlog.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.github.diegoberaldin.raccoonforlemmy.core.appearance.data.PostLayout
import com.github.diegoberaldin.raccoonforlemmy.core.utils.ellipsize
import com.github.diegoberaldin.raccoonforlemmy.domain.lemmy.data.ModlogItem
import com.github.diegoberaldin.raccoonforlemmy.domain.lemmy.data.UserModel
import com.github.diegoberaldin.raccoonforlemmy.resources.MR
import dev.icerock.moko.resources.compose.stringResource

@Composable
internal fun ModLockPostItem(
    item: ModlogItem.ModLockPost,
    modifier: Modifier = Modifier,
    autoLoadImages: Boolean = true,
    postLayout: PostLayout = PostLayout.Card,
    onOpenUser: ((UserModel) -> Unit)? = null,
) {
    InnerModlogItem(
        modifier = modifier,
        autoLoadImages = autoLoadImages,
        date = item.date,
        postLayout = postLayout,
        moderator = item.moderator,
        onOpenUser = onOpenUser,
        innerContent = {
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                        append(item.post?.title.ellipsize())
                    }
                    append(" ")
                    if (item.locked) {
                        append(stringResource(MR.strings.modlog_item_post_locked))
                    } else {
                        append(stringResource(MR.strings.modlog_item_post_unlocked))
                    }
                },
                style = MaterialTheme.typography.bodySmall,
            )
        },
    )
}