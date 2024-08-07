import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.detekt)
    alias(libs.plugins.kotlinx.serialization)
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
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
            baseName = "core.persistence"
            isStatic = true
        }
    }

    sourceSets {
        val androidMain by getting {
            dependencies {
                implementation(libs.sqlcipher)
                implementation(libs.sqldelight.android)
            }
        }
        val iosMain by getting {
            dependencies {
                implementation(libs.sqldelight.native)
            }
        }
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.materialIconsExtended)
                implementation(libs.kotlinx.coroutines)
                implementation(libs.kotlinx.serialization.json)

                implementation(libs.koin.core)

                implementation(projects.core.appearance)
                implementation(projects.core.l10n)
                implementation(projects.core.preferences)
                implementation(projects.core.utils)
            }
        }
        val androidUnitTest by getting {
            dependencies {
                implementation(libs.kotlinx.coroutines.test)
                implementation(kotlin("test-junit"))
                implementation(libs.mockk)
                implementation(projects.core.testutils)
            }
        }
    }
}

android {
    namespace = "com.livefast.eattrash.raccoonforlemmy.core.persistence"
    compileSdk = libs.versions.android.targetSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}

sqldelight {
    databases {
        create("AppDatabase") {
            packageName.set("com.livefast.eattrash.raccoonforlemmy.core.persistence.entities")
            srcDirs.setFrom("src/commonMain/sqldelight")
        }
    }
    linkSqlite = true
}

allprojects {
    tasks.withType<Detekt> {
        setSource(files(project.projectDir))
        exclude("*")
    }
}
