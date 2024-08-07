package com.livefast.eattrash.raccoonforlemmy.core.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LocalUserView(
    @SerialName("local_user") val localUser: LocalUser? = null,
    @SerialName("person") val person: Person,
    @SerialName("counts") val counts: PersonAggregates,
    @SerialName("local_user_vote_display_mode") val localUserVoteDisplayMode: LocalUserVoteDisplayMode? = null,
)
