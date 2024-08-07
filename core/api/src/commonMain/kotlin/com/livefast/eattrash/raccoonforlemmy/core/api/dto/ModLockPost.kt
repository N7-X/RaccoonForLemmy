package com.livefast.eattrash.raccoonforlemmy.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ModLockPost(
    @SerialName("id") val id: ModlogItemId,
    @SerialName("locked") val locked: Boolean,
    @SerialName("when_") val date: String? = null,
)
