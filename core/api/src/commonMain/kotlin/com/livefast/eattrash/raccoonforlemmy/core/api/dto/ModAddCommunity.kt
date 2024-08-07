package com.livefast.eattrash.raccoonforlemmy.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ModAddCommunity(
    @SerialName("id") val id: ModlogItemId,
    @SerialName("removed") val removed: Boolean,
    @SerialName("when_") val date: String? = null,
)
