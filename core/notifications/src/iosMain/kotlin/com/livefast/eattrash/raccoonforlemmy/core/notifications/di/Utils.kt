package com.livefast.eattrash.raccoonforlemmy.core.notifications.di

import com.livefast.eattrash.raccoonforlemmy.core.notifications.NotificationCenter
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual fun getNotificationCenter(): NotificationCenter = NotificationDiHelper.notificationCenter

object NotificationDiHelper : KoinComponent {
    val notificationCenter: NotificationCenter by inject()
}
