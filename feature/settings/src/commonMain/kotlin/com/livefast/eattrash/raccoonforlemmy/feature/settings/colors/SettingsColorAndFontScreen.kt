package com.livefast.eattrash.raccoonforlemmy.feature.settings.colors

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.livefast.eattrash.raccoonforlemmy.core.appearance.data.UiTheme
import com.livefast.eattrash.raccoonforlemmy.core.appearance.data.toFontScale
import com.livefast.eattrash.raccoonforlemmy.core.appearance.data.toIcon
import com.livefast.eattrash.raccoonforlemmy.core.appearance.data.toReadableName
import com.livefast.eattrash.raccoonforlemmy.core.appearance.di.getColorSchemeProvider
import com.livefast.eattrash.raccoonforlemmy.core.appearance.di.getThemeRepository
import com.livefast.eattrash.raccoonforlemmy.core.appearance.theme.Spacing
import com.livefast.eattrash.raccoonforlemmy.core.commonui.lemmyui.SettingsRow
import com.livefast.eattrash.raccoonforlemmy.core.commonui.lemmyui.SettingsSwitchRow
import com.livefast.eattrash.raccoonforlemmy.core.commonui.modals.CustomBottomSheet
import com.livefast.eattrash.raccoonforlemmy.core.l10n.messages.LocalStrings
import com.livefast.eattrash.raccoonforlemmy.core.navigation.di.getNavigationCoordinator
import com.livefast.eattrash.raccoonforlemmy.core.notifications.NotificationCenterEvent
import com.livefast.eattrash.raccoonforlemmy.core.notifications.di.getNotificationCenter
import com.livefast.eattrash.raccoonforlemmy.core.utils.compose.onClick
import com.livefast.eattrash.raccoonforlemmy.core.utils.compose.rememberCallback
import com.livefast.eattrash.raccoonforlemmy.core.utils.compose.rememberCallbackArgs
import com.livefast.eattrash.raccoonforlemmy.feature.settings.ui.components.SettingsColorRow
import com.livefast.eattrash.raccoonforlemmy.feature.settings.ui.components.SettingsMultiColorRow
import com.livefast.eattrash.raccoonforlemmy.unit.choosecolor.ColorBottomSheet
import com.livefast.eattrash.raccoonforlemmy.unit.choosecolor.CommentBarThemeBottomSheet
import com.livefast.eattrash.raccoonforlemmy.unit.choosecolor.VoteThemeBottomSheet
import com.livefast.eattrash.raccoonforlemmy.unit.choosefont.FontFamilyBottomSheet
import com.livefast.eattrash.raccoonforlemmy.unit.choosefont.FontScaleBottomSheet
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class SettingsColorAndFontScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val model = getScreenModel<SettingsColorAndFontMviModel>()
        val uiState by model.uiState.collectAsState()
        val navigationCoordinator = remember { getNavigationCoordinator() }
        val notificationCenter = remember { getNotificationCenter() }
        val topAppBarState = rememberTopAppBarState()
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
        val themeRepository = remember { getThemeRepository() }
        val scrollState = rememberScrollState()
        val colorSchemeProvider = remember { getColorSchemeProvider() }
        val defaultTheme =
            if (isSystemInDarkTheme()) {
                UiTheme.Dark
            } else {
                UiTheme.Light
            }
        val sheetState = rememberModalBottomSheetState()
        val sheetScope = rememberCoroutineScope()
        var uiFontSizeWorkaround by remember { mutableStateOf(true) }
        var bottomSheetIsOpen by remember { mutableStateOf(false) }

        LaunchedEffect(themeRepository) {
            themeRepository.uiFontScale
                .drop(1)
                .onEach {
                    uiFontSizeWorkaround = false
                    delay(50)
                    uiFontSizeWorkaround = true
                }.launchIn(this)
        }

        if (!uiFontSizeWorkaround) {
            return
        }

        Scaffold(
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
            topBar = {
                TopAppBar(
                    scrollBehavior = scrollBehavior,
                    title = {
                        Text(
                            modifier = Modifier.padding(horizontal = Spacing.s),
                            text = LocalStrings.current.settingsColorsAndFonts,
                            style = MaterialTheme.typography.titleMedium,
                        )
                    },
                    navigationIcon = {
                        if (navigationCoordinator.canPop.value) {
                            Image(
                                modifier =
                                    Modifier.onClick(
                                        onClick = {
                                            navigationCoordinator.popScreen()
                                        },
                                    ),
                                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
                            )
                        }
                    },
                )
            },
        ) { padding ->
            Box(
                modifier =
                    Modifier
                        .padding(
                            top = padding.calculateTopPadding(),
                        ).nestedScroll(scrollBehavior.nestedScrollConnection),
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(Spacing.xs),
                ) {
                    // theme
                    SettingsRow(
                        title = LocalStrings.current.settingsUiTheme,
                        value = uiState.uiTheme.toReadableName(),
                        onTap = {
                            bottomSheetIsOpen = true
                        }
                    )

                    // dynamic colors
                    if (uiState.supportsDynamicColors) {
                        SettingsSwitchRow(
                            title = LocalStrings.current.settingsDynamicColors,
                            value = uiState.dynamicColors,
                            onValueChanged =
                                rememberCallbackArgs(model) { value ->
                                    model.reduce(
                                        SettingsColorAndFontMviModel.Intent.ChangeDynamicColors(value),
                                    )
                                },
                        )
                    }
                    // random color
                    SettingsSwitchRow(
                        title = LocalStrings.current.settingsItemRandomThemeColor,
                        subtitle = LocalStrings.current.settingsSubtitleRandomThemeColor,
                        value = uiState.randomColor,
                        onValueChanged =
                            rememberCallbackArgs(model) { value ->
                                model.reduce(
                                    SettingsColorAndFontMviModel.Intent.ChangeRandomColor(value),
                                )
                            },
                    )

                    // custom scheme seed color
                    SettingsColorRow(
                        title = LocalStrings.current.settingsCustomSeedColor,
                        value =
                            uiState.customSeedColor ?: colorSchemeProvider
                                .getColorScheme(
                                    theme = uiState.uiTheme ?: defaultTheme,
                                    dynamic = uiState.dynamicColors,
                                ).primary,
                        onTap =
                            rememberCallback {
                                val sheet = ColorBottomSheet()
                                navigationCoordinator.showBottomSheet(sheet)
                            },
                    )

                    if (uiState.isLogged) {
                        // action colors
                        SettingsColorRow(
                            title = LocalStrings.current.settingsUpvoteColor,
                            value = uiState.upVoteColor ?: MaterialTheme.colorScheme.primary,
                            onTap =
                                rememberCallback {
                                    val screen =
                                        VoteThemeBottomSheet(
                                            actionType = 0,
                                        )
                                    navigationCoordinator.showBottomSheet(screen)
                                },
                        )
                        SettingsColorRow(
                            title = LocalStrings.current.settingsDownvoteColor,
                            value = uiState.downVoteColor ?: MaterialTheme.colorScheme.tertiary,
                            onTap =
                                rememberCallback {
                                    val screen =
                                        VoteThemeBottomSheet(
                                            actionType = 1,
                                        )
                                    navigationCoordinator.showBottomSheet(screen)
                                },
                        )
                        SettingsColorRow(
                            title = LocalStrings.current.settingsReplyColor,
                            value = uiState.replyColor ?: MaterialTheme.colorScheme.secondary,
                            onTap =
                                rememberCallback {
                                    val screen =
                                        VoteThemeBottomSheet(
                                            actionType = 2,
                                        )
                                    navigationCoordinator.showBottomSheet(screen)
                                },
                        )
                        SettingsColorRow(
                            title = LocalStrings.current.settingsSaveColor,
                            value =
                                uiState.saveColor
                                    ?: MaterialTheme.colorScheme.secondaryContainer,
                            onTap =
                                rememberCallback {
                                    val screen =
                                        VoteThemeBottomSheet(
                                            actionType = 3,
                                        )
                                    navigationCoordinator.showBottomSheet(screen)
                                },
                        )
                    }

                    // comment bar theme
                    val commentBarColors =
                        themeRepository.getCommentBarColors(uiState.commentBarTheme)
                    SettingsMultiColorRow(
                        title = LocalStrings.current.settingsCommentBarTheme,
                        values = commentBarColors,
                        onTap =
                            rememberCallback {
                                val screen = CommentBarThemeBottomSheet()
                                navigationCoordinator.showBottomSheet(screen)
                            },
                    )

                    // font family
                    SettingsRow(
                        title = LocalStrings.current.settingsUiFontFamily,
                        value = uiState.uiFontFamily.toReadableName(),
                        onTap =
                            rememberCallback {
                                val sheet = FontFamilyBottomSheet()
                                navigationCoordinator.showBottomSheet(sheet)
                            },
                    )

                    // font scale
                    SettingsRow(
                        title = LocalStrings.current.settingsUiFontScale,
                        value = uiState.uiFontScale.toFontScale().toReadableName(),
                        onTap =
                            rememberCallback {
                                val sheet = FontScaleBottomSheet()
                                navigationCoordinator.showBottomSheet(sheet)
                            },
                    )
                }
            }
        }

        // Placing theme list here for neatness & code review
        val uiThemeList = listOf(
            UiTheme.Light,
            UiTheme.Dark,
            UiTheme.Black,
            null,
        )

        CustomBottomSheet(
            isOpen = bottomSheetIsOpen,
            sheetScope = sheetScope,
            sheetState = sheetState,
            onDismiss = {
                bottomSheetIsOpen = false
            },
            onSelection = { selection ->
                notificationCenter.send(
                    NotificationCenterEvent.ChangeTheme(uiThemeList[selection]),
                )
            },
            headerText = LocalStrings.current.settingsUiTheme,
            contentText = listOf(
                uiThemeList[0].toReadableName(),
                uiThemeList[1].toReadableName(),
                uiThemeList[2].toReadableName(),
                uiThemeList[3].toReadableName(),
            ),
            contentPostTextIcon = listOf(
                uiThemeList[0]?.toIcon(),
                uiThemeList[1]?.toIcon(),
                uiThemeList[2]?.toIcon(),
            )
        )
    }
}
