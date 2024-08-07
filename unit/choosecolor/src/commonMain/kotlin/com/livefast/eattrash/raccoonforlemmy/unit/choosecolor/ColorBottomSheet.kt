package com.livefast.eattrash.raccoonforlemmy.unit.choosecolor

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.livefast.eattrash.raccoonforlemmy.core.appearance.di.getAppColorRepository
import com.livefast.eattrash.raccoonforlemmy.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforlemmy.core.appearance.theme.toColor
import com.livefast.eattrash.raccoonforlemmy.core.appearance.theme.toReadableName
import com.livefast.eattrash.raccoonforlemmy.core.commonui.components.BottomSheetHeader
import com.livefast.eattrash.raccoonforlemmy.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforlemmy.core.navigation.di.getNavigationCoordinator
import com.livefast.eattrash.raccoonforlemmy.core.notifications.NotificationCenterEvent
import com.livefast.eattrash.raccoonforlemmy.core.notifications.di.getNotificationCenter
import com.livefast.eattrash.raccoonforlemmy.core.persistence.di.getSettingsRepository
import com.livefast.eattrash.raccoonforlemmy.core.utils.compose.onClick

class ColorBottomSheet : Screen {
    @Composable
    override fun Content() {
        val navigationCoordinator = remember { getNavigationCoordinator() }
        val notificationCenter = remember { getNotificationCenter() }
        var customPickerDialogOpened by remember { mutableStateOf(false) }
        val settingsRepository = remember { getSettingsRepository() }
        val appColorRepository = remember { getAppColorRepository() }
        val customText = LocalStrings.current.settingsColorCustom

        val values: List<Pair<Color?, String>> =
            buildList {
                this +=
                    appColorRepository.getColors().map {
                        it.toColor() to it.toReadableName()
                    }
                this += null to customText
                this += null to LocalStrings.current.buttonReset
            }

        Column(
            modifier =
                Modifier.padding(
                    top = Spacing.s,
                    start = Spacing.s,
                    end = Spacing.s,
                    bottom = Spacing.m,
                ),
            verticalArrangement = Arrangement.spacedBy(Spacing.s),
        ) {
            BottomSheetHeader(LocalStrings.current.settingsCustomSeedColor)

            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
            ) {
                for (value in values) {
                    val text = value.second
                    val isChooseCustom = text == customText
                    Row(
                        modifier =
                            Modifier
                                .padding(
                                    horizontal = Spacing.s,
                                    vertical = Spacing.s,
                                ).fillMaxWidth()
                                .onClick(
                                    onClick = {
                                        if (!isChooseCustom) {
                                            notificationCenter.send(
                                                NotificationCenterEvent.ChangeColor(
                                                    value.first,
                                                ),
                                            )
                                            navigationCoordinator.hideBottomSheet()
                                        } else {
                                            customPickerDialogOpened = true
                                        }
                                    },
                                ),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = text,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                        Spacer(modifier = Modifier.weight(1f))

                        if (!isChooseCustom) {
                            Box(
                                modifier =
                                    Modifier
                                        .size(36.dp)
                                        .background(
                                            color = value.first ?: Color.Transparent,
                                            shape = CircleShape,
                                        ),
                            )
                        } else {
                            Image(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
                            )
                        }
                    }
                }
            }
        }

        if (customPickerDialogOpened) {
            val current =
                settingsRepository.currentSettings.value.customSeedColor
                    ?.let { Color(it) }
            ColorPickerDialog(
                initialValue = current ?: MaterialTheme.colorScheme.primary,
                onClose = {
                    customPickerDialogOpened = false
                },
                onSubmit = { color ->
                    notificationCenter.send(NotificationCenterEvent.ChangeColor(color))
                    navigationCoordinator.hideBottomSheet()
                },
            )
        }
    }
}
