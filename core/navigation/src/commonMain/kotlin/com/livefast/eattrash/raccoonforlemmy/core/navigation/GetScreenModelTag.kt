package com.livefast.eattrash.raccoonforlemmy.core.navigation

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.mp.KoinPlatform.getKoin

@Composable
inline fun <reified T : ScreenModel> Screen.getScreenModel(
    tag: String,
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null,
): T {
    val koin = getKoin()
    return rememberScreenModel(tag = tag) { koin.get(qualifier, parameters) }
}
