package com.livefast.eattrash.raccoonforlemmy.core.appearance.di

import com.livefast.eattrash.raccoonforlemmy.core.appearance.repository.ThemeRepository
import com.livefast.eattrash.raccoonforlemmy.core.appearance.theme.AppColorRepository
import com.livefast.eattrash.raccoonforlemmy.core.appearance.theme.BarColorProvider
import com.livefast.eattrash.raccoonforlemmy.core.appearance.theme.ColorSchemeProvider
import org.koin.core.module.Module

expect val nativeAppearanceModule: Module

expect fun getThemeRepository(): ThemeRepository

expect fun getColorSchemeProvider(): ColorSchemeProvider

expect fun getBarColorProvider(): BarColorProvider

expect fun getAppColorRepository(): AppColorRepository
