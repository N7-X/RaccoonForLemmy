package com.livefast.eattrash.raccoonforlemmy.unit.choosefont

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import com.livefast.eattrash.raccoonforlemmy.core.appearance.data.FontScale
import com.livefast.eattrash.raccoonforlemmy.core.appearance.data.scaleFactor
import com.livefast.eattrash.raccoonforlemmy.core.appearance.data.toFontScale
import com.livefast.eattrash.raccoonforlemmy.core.appearance.data.toReadableName
import com.livefast.eattrash.raccoonforlemmy.core.appearance.repository.ContentFontClass
import com.livefast.eattrash.raccoonforlemmy.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforlemmy.core.commonui.components.BottomSheetHeader
import com.livefast.eattrash.raccoonforlemmy.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforlemmy.core.navigation.di.getNavigationCoordinator
import com.livefast.eattrash.raccoonforlemmy.core.notifications.NotificationCenterEvent
import com.livefast.eattrash.raccoonforlemmy.core.notifications.di.getNotificationCenter
import com.livefast.eattrash.raccoonforlemmy.core.utils.compose.onClick

private val defaultChoices: List<Float> =
    listOf(
        FontScale.Largest,
        FontScale.Larger,
        FontScale.Large,
        FontScale.Normal,
        FontScale.Small,
        FontScale.Smaller,
        FontScale.Smallest,
    ).map { it.scaleFactor }

class FontScaleBottomSheet(
    private val values: List<Float> = defaultChoices,
    private val contentClass: ContentFontClass? = null,
) : Screen {
    @Composable
    override fun Content() {
        val navigationCoordinator = remember { getNavigationCoordinator() }
        val notificationCenter = remember { getNotificationCenter() }
        Column(
            modifier =
                Modifier
                    .padding(
                        top = Spacing.s,
                        start = Spacing.s,
                        end = Spacing.s,
                        bottom = Spacing.m,
                    ),
            verticalArrangement = Arrangement.spacedBy(Spacing.s),
        ) {
            val title =
                when (contentClass) {
                    ContentFontClass.Title -> LocalStrings.current.settingsTitleFontScale
                    ContentFontClass.Body -> LocalStrings.current.settingsContentFontScale
                    ContentFontClass.Comment -> LocalStrings.current.settingsCommentFontScale
                    ContentFontClass.AncillaryText -> LocalStrings.current.settingsAncillaryFontScale
                    else -> LocalStrings.current.settingsUiFontScale
                }
            BottomSheetHeader(title)
            Text(
                modifier =
                    Modifier.padding(
                        start = Spacing.s,
                        top = Spacing.s,
                        end = Spacing.s,
                    ),
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
            ) {
                for (value in values) {
                    val fontScale = value.toFontScale()
                    Row(
                        modifier =
                            Modifier
                                .padding(
                                    horizontal = Spacing.s,
                                    vertical = Spacing.s,
                                )
                                .fillMaxWidth()
                                .onClick(
                                    onClick = {
                                        notificationCenter.send(
                                            if (contentClass != null) {
                                                NotificationCenterEvent.ChangeContentFontSize(
                                                    value = value,
                                                    contentClass = contentClass,
                                                )
                                            } else {
                                                NotificationCenterEvent.ChangeUiFontSize(value)
                                            },
                                        )
                                        navigationCoordinator.hideBottomSheet()
                                    },
                                ),
                    ) {
                        val originalFontSize = MaterialTheme.typography.bodyLarge.fontSize
                        Text(
                            text = fontScale.toReadableName(),
                            style =
                                MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = originalFontSize * value,
                                ),
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                }
            }
        }
    }
}
