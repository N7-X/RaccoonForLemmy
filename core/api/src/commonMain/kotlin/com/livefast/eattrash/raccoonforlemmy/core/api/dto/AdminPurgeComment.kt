package com.livefast.eattrash.raccoonforlemmy.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AdminPurgeComment(
    @SerialName("id") val id: ModlogItemId,
    @SerialName("reason") val reason: String? = null,
    @SerialName("when_") val date: String? = null,
)
