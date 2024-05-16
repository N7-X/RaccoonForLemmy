package com.github.diegoberaldin.raccoonforlemmy.unit.moderatewithreason

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.github.diegoberaldin.raccoonforlemmy.core.appearance.theme.Spacing
import com.github.diegoberaldin.raccoonforlemmy.core.commonui.components.ProgressHud
import com.github.diegoberaldin.raccoonforlemmy.core.l10n.LocalXmlStrings
import com.github.diegoberaldin.raccoonforlemmy.core.navigation.di.getNavigationCoordinator
import com.github.diegoberaldin.raccoonforlemmy.core.navigation.getScreenModel
import com.github.diegoberaldin.raccoonforlemmy.core.utils.compose.onClick
import com.github.diegoberaldin.raccoonforlemmy.core.utils.compose.rememberCallback
import com.github.diegoberaldin.raccoonforlemmy.core.utils.toReadableMessage
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.core.parameter.parametersOf
import kotlin.time.Duration.Companion.seconds

class ModerateWithReasonScreen(
    private val actionId: Int,
    private val contentId: Long,
) : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val model =
            getScreenModel<ModerateWithReasonMviModel>(tag = "$actionId-$contentId") {
                parametersOf(actionId, contentId)
            }
        val uiState by model.uiState.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }
        val genericError = LocalXmlStrings.current.messageGenericError
        val successMessage = LocalXmlStrings.current.messageOperationSuccessful
        val navigationCoordinator = remember { getNavigationCoordinator() }
        val topAppBarState = rememberTopAppBarState()
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topAppBarState)
        val focusManager = LocalFocusManager.current

        LaunchedEffect(model) {
            model.effects.onEach {
                when (it) {
                    is ModerateWithReasonMviModel.Effect.Failure -> {
                        snackbarHostState.showSnackbar(it.message ?: genericError)
                    }

                    ModerateWithReasonMviModel.Effect.Success -> {
                        navigationCoordinator.showGlobalMessage(message = successMessage, delay = 1.seconds)
                        navigationCoordinator.popScreen()
                    }
                }
            }.launchIn(this)
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    scrollBehavior = scrollBehavior,
                    navigationIcon = {
                        Image(
                            modifier =
                                Modifier.padding(start = Spacing.s).onClick(
                                    onClick = {
                                        navigationCoordinator.popScreen()
                                    },
                                ),
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
                        )
                    },
                    title = {
                        val title =
                            when (uiState.action) {
                                is ModerateWithReasonAction.HideCommunity -> LocalXmlStrings.current.postActionHide
                                is ModerateWithReasonAction.PurgeComment -> LocalXmlStrings.current.adminActionPurge
                                is ModerateWithReasonAction.PurgeCommunity -> LocalXmlStrings.current.adminActionPurge
                                is ModerateWithReasonAction.PurgePost -> LocalXmlStrings.current.adminActionPurge
                                is ModerateWithReasonAction.PurgeUser -> LocalXmlStrings.current.adminActionPurge
                                is ModerateWithReasonAction.RemoveComment -> LocalXmlStrings.current.modActionRemove
                                is ModerateWithReasonAction.RemovePost -> LocalXmlStrings.current.modActionRemove
                                is ModerateWithReasonAction.ReportComment -> LocalXmlStrings.current.createReportTitleComment
                                is ModerateWithReasonAction.ReportPost -> LocalXmlStrings.current.createReportTitlePost
                            }
                        Text(
                            text = title,
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.titleMedium,
                        )
                    },
                    actions = {
                        IconButton(
                            modifier = Modifier.padding(horizontal = Spacing.xs),
                            content = {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Default.Send,
                                    contentDescription = null,
                                )
                            },
                            onClick =
                                rememberCallback(model) {
                                    focusManager.clearFocus()
                                    model.reduce(ModerateWithReasonMviModel.Intent.Submit)
                                },
                        )
                    },
                )
            },
            snackbarHost = {
                SnackbarHost(snackbarHostState) { data ->
                    Snackbar(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        snackbarData = data,
                    )
                }
            },
        ) { padding ->
            Column(
                modifier =
                    Modifier
                        .padding(padding)
                        .consumeWindowInsets(padding)
                        .navigationBarsPadding()
                        .imePadding(),
                verticalArrangement = Arrangement.spacedBy(Spacing.s),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                val commentFocusRequester = remember { FocusRequester() }
                TextField(
                    modifier =
                        Modifier
                            .focusRequester(commentFocusRequester)
                            .heightIn(min = 300.dp, max = 500.dp)
                            .fillMaxWidth(),
                    colors =
                        TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                        ),
                    label = {
                        Text(text = LocalXmlStrings.current.createReportPlaceholder)
                    },
                    textStyle = MaterialTheme.typography.bodyMedium,
                    value = uiState.text,
                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            autoCorrect = true,
                        ),
                    onValueChange = { value ->
                        model.reduce(ModerateWithReasonMviModel.Intent.SetText(value))
                    },
                    isError = uiState.textError != null,
                    supportingText = {
                        val error = uiState.textError
                        if (error != null) {
                            Text(
                                text = error.toReadableMessage(),
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    },
                )
                Spacer(Modifier.height(Spacing.xxl))
            }

            if (uiState.loading) {
                ProgressHud()
            }
        }
    }
}