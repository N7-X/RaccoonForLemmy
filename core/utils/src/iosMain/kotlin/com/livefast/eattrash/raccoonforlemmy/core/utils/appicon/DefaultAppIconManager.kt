package com.livefast.eattrash.raccoonforlemmy.core.utils.appicon

class DefaultAppIconManager : AppIconManager {
    override val supportsMultipleIcons = false

    override fun changeIcon(variant: AppIconVariant) {
        // no-op
    }
}
