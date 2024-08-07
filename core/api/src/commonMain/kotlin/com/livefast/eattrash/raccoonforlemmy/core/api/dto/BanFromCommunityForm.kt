package com.livefast.eattrash.raccoonforlemmy.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BanFromCommunityForm(
    @SerialName("community_id") val communityId: CommunityId,
    @SerialName("person_id") val personId: PersonId,
    @SerialName("ban") val ban: Boolean,
    @SerialName("remove_data") val removeData: Boolean,
    @SerialName("reason") val reason: String?,
    @SerialName("expires") val expires: Long?,
    @SerialName("auth") val auth: String,
)
