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
import androidx.compose.ui.text.font.FontFamily
import cafe.adriel.voyager.core.screen.Screen
import com.livefast.eattrash.raccoonforlemmy.core.appearance.data.UiFontFamily
import com.livefast.eattrash.raccoonforlemmy.core.appearance.data.toInt
import com.livefast.eattrash.raccoonforlemmy.core.appearance.data.toReadableName
import com.livefast.eattrash.raccoonforlemmy.core.appearance.data.toUiFontFamily
import com.livefast.eattrash.raccoonforlemmy.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforlemmy.core.commonui.components.BottomSheetHeader
import com.livefast.eattrash.raccoonforlemmy.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforlemmy.core.navigation.di.getNavigationCoordinator
import com.livefast.eattrash.raccoonforlemmy.core.notifications.NotificationCenterEvent
import com.livefast.eattrash.raccoonforlemmy.core.notifications.di.getNotificationCenter
import com.livefast.eattrash.raccoonforlemmy.core.resources.di.getCoreResources
import com.livefast.eattrash.raccoonforlemmy.core.utils.compose.onClick

private val defaultChoices: List<Int> =
    listOf(
        UiFontFamily.Poppins,
        UiFontFamily.NotoSans,
        UiFontFamily.CharisSIL,
        UiFontFamily.Default,
    ).map { it.toInt() }

class FontFamilyBottomSheet(
    private val values: List<Int> = defaultChoices,
    private val content: Boolean = false,
) : Screen {
    @Composable
    override fun Content() {
        val navigationCoordinator = remember { getNavigationCoordinator() }
        val notificationCenter = remember { getNotificationCenter() }
        val coreResources = remember { getCoreResources() }

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
            BottomSheetHeader(LocalStrings.current.settingsUiFontFamily)
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
            ) {
                for (value in values) {
                    val family = value.toUiFontFamily()
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
                                        val event =
                                            if (content) {
                                                NotificationCenterEvent.ChangeContentFontFamily(family)
                                            } else {
                                                NotificationCenterEvent.ChangeFontFamily(family)
                                            }
                                        notificationCenter.send(event)
                                        navigationCoordinator.hideBottomSheet()
                                    },
                                ),
                    ) {
                        val fontFamily =
                            when (family) {
                                UiFontFamily.NotoSans -> coreResources.notoSans
                                UiFontFamily.CharisSIL -> coreResources.charisSil
                                UiFontFamily.Poppins -> coreResources.poppins
                                else -> FontFamily.Default
                            }
                        Text(
                            text = family.toReadableName(),
                            style =
                                MaterialTheme.typography.bodyLarge.copy(
                                    fontFamily = fontFamily,
                                ),
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                }
            }
        }
    }
}
