package com.livefast.eattrash.raccoonforlemmy.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ModRemovePost(
    @SerialName("id") val id: ModlogItemId,
    @SerialName("reason") val reason: String? = null,
    @SerialName("removed") val removed: Boolean,
    @SerialName("when_") val date: String? = null,
)
