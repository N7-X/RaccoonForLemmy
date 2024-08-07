package com.livefast.eattrash.raccoonforlemmy.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommunityView(
    @SerialName("community") val community: Community,
    @SerialName("subscribed") val subscribed: SubscribedType,
    @SerialName("blocked") val blocked: Boolean,
    @SerialName("counts") val counts: CommunityAggregates,
    @SerialName("banned_from_community") val bannedFromCommunity: Boolean? = null,
)
