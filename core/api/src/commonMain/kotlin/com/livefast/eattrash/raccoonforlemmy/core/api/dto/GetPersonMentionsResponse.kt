package com.livefast.eattrash.raccoonforlemmy.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetPersonMentionsResponse(
    @SerialName("mentions")
    val mentions: List<PersonMentionView>,
)
