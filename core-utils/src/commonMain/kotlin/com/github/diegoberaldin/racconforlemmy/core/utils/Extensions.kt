package com.github.diegoberaldin.racconforlemmy.core.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import com.github.diegoberaldin.raccoonforlemmy.resources.MR
import dev.icerock.moko.resources.compose.stringResource

fun Modifier.onClick(onClick: () -> Unit): Modifier = composed {
    clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() },
    ) {
        onClick()
    }
}

@Composable
fun String.toLanguageName() = when (this) {
    "it" -> stringResource(MR.strings.language_it)
    else -> stringResource(MR.strings.language_en)
}

@Composable
fun Dp.toLocalPixel(): Float = with(LocalDensity.current) {
    value * density
}