import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.detekt)
}

@OptIn(ExperimentalKotlinGradlePluginApi::class)
kotlin {
    applyDefaultHierarchyTemplate()
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_1_8)
        }
    }
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)

                implementation(libs.koin.core)
                implementation(libs.lyricist)
                implementation(libs.voyager.navigator)
                implementation(libs.voyager.screenmodel)
                implementation(libs.voyager.koin)
                implementation(libs.voyager.transition)
                implementation(libs.voyager.tab)
                implementation(libs.voyager.bottomsheet)

                implementation(projects.core.api)
                implementation(projects.core.appearance)
                implementation(projects.core.architecture)
                implementation(projects.core.commonui.components)
                implementation(projects.core.commonui.detailopenerApi)
                implementation(projects.core.commonui.detailopenerImpl)
                implementation(projects.core.commonui.lemmyui)
                implementation(projects.core.l10n)
                implementation(projects.core.navigation)
                implementation(projects.core.notifications)
                implementation(projects.core.persistence)
                implementation(projects.core.preferences)
                implementation(projects.core.resources)
                implementation(projects.core.utils)

                implementation(projects.domain.identity)
                implementation(projects.domain.inbox)
                implementation(projects.domain.lemmy.data)
                implementation(projects.domain.lemmy.pagination)
                implementation(projects.domain.lemmy.repository)

                implementation(projects.unit.accountsettings)
                implementation(projects.unit.acknowledgements)
                implementation(projects.unit.ban)
                implementation(projects.unit.chat)
                implementation(projects.unit.communitydetail)
                implementation(projects.unit.communityinfo)
                implementation(projects.unit.configurecontentview)
                implementation(projects.unit.configurenavbar)
                implementation(projects.unit.configureswipeactions)
                implementation(projects.unit.createcomment)
                implementation(projects.unit.createpost)
                implementation(projects.unit.drafts)
                implementation(projects.unit.drawer)
                implementation(projects.unit.editcommunity)
                implementation(projects.unit.instanceinfo)
                implementation(projects.unit.manageaccounts)
                implementation(projects.unit.manageban)
                implementation(projects.unit.medialist)
                implementation(projects.unit.managesubscriptions)
                implementation(projects.unit.moderatewithreason)
                implementation(projects.unit.filteredcontents)
                implementation(projects.unit.licences)
                implementation(projects.unit.modlog)
                implementation(projects.unit.multicommunity)
                implementation(projects.unit.postdetail)
                implementation(projects.unit.reportlist)
                implementation(projects.unit.selectcommunity)
                implementation(projects.unit.selectinstance)
                implementation(projects.unit.userdetail)
                implementation(projects.unit.userinfo)
                implementation(projects.unit.zoomableimage)

                api(projects.feature.home)
                api(projects.feature.inbox)
                api(projects.feature.search)
                api(projects.feature.profile)
                api(projects.feature.settings)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidUnitTest by getting
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by getting
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by getting
    }
}

android {
    namespace = "com.livefast.eattrash.raccoonforlemmy"
    compileSdk =
        libs.versions.android.targetSdk
            .get()
            .toInt()
    defaultConfig {
        minSdk =
            libs.versions.android.minSdk
                .get()
                .toInt()
    }
}
dependencies {
    implementation(project(":core:utils"))
}
