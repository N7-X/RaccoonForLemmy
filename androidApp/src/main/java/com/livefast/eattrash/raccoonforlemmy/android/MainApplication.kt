package com.livefast.eattrash.raccoonforlemmy.android

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.livefast.eattrash.raccoonforlemmy.core.utils.debug.CrashReportConfiguration
import com.livefast.eattrash.raccoonforlemmy.core.utils.debug.CrashReportWriter
import com.livefast.eattrash.raccoonforlemmy.core.utils.imagepreload.getCoilImageLoader
import com.livefast.eattrash.raccoonforlemmy.di.sharedHelperModule
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MainApplication :
    Application(),
    ImageLoaderFactory {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MainApplication)
            androidLogger()
            modules(
                sharedHelperModule,
            )
        }.apply {
            val crashReportWriter: CrashReportWriter by inject()
            val crashReportConfig: CrashReportConfiguration by inject()

            Thread.currentThread().apply {
                val original = uncaughtExceptionHandler
                setUncaughtExceptionHandler { t, exception ->
                    if (crashReportConfig.isEnabled()) {
                        val stackTrace = exception.stackTraceToString()
                        crashReportWriter.write(stackTrace)
                    }
                    original?.uncaughtException(t, exception)
                }
            }
        }
    }

    override fun newImageLoader(): ImageLoader = getCoilImageLoader(this)
}
