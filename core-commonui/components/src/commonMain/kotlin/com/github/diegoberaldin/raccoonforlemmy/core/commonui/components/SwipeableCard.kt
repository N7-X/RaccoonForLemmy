package com.github.diegoberaldin.raccoonforlemmy.core.commonui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeableCard(
    modifier: Modifier = Modifier,
    directions: Set<DismissDirection> = setOf(
        DismissDirection.StartToEnd,
        DismissDirection.EndToStart,
    ),
    enabled: Boolean = true,
    content: @Composable () -> Unit,
    swipeContent: @Composable (DismissDirection) -> Unit,
    backgroundColor: @Composable (DismissValue) -> Color,
    onGestureBegin: (() -> Unit)? = null,
    onDismissToEnd: (() -> Unit)? = null,
    onDismissToStart: (() -> Unit)? = null,
) {
    if (enabled) {
        var width by remember { mutableStateOf(0f) }
        val dismissState = rememberDismissState(
            confirmStateChange = {
                when (it) {
                    DismissValue.DismissedToEnd -> {
                        onDismissToEnd?.invoke()
                    }

                    DismissValue.DismissedToStart -> {
                        onDismissToStart?.invoke()
                    }

                    else -> Unit
                }
                false
            },
        )

        val threshold = 0.15f
        LaunchedEffect(dismissState) {
            snapshotFlow { dismissState.offset.value }.map {
                when {
                    it > width * threshold -> DismissDirection.StartToEnd
                    it < -width * threshold -> DismissDirection.EndToStart
                    else -> null
                }
            }.stateIn(this).onEach { willDismissDirection ->
                if (willDismissDirection != null) {
                    onGestureBegin?.invoke()
                }
            }.launchIn(this)
        }
        SwipeToDismiss(
            modifier = modifier.onGloballyPositioned {
                width = it.size.toSize().width
            },
            state = dismissState,
            directions = directions,
            dismissThresholds = {
                FractionalThreshold(threshold)
            },
            background = {
                val direction =
                    dismissState.dismissDirection ?: return@SwipeToDismiss
                val bgColor by animateColorAsState(
                    backgroundColor(dismissState.targetValue),
                )
                val alignment = when (direction) {
                    DismissDirection.StartToEnd -> Alignment.CenterStart
                    DismissDirection.EndToStart -> Alignment.CenterEnd
                }
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(bgColor)
                        .padding(horizontal = 20.dp),
                    contentAlignment = alignment,
                ) {
                    swipeContent(direction)
                }
            },
        ) {
            content()
        }
    } else {
        content()
    }
}
