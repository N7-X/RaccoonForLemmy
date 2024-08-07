package com.livefast.eattrash.raccoonforlemmy.domain.lemmy.data

data class AccountBansModel(
    val users: List<UserModel> = emptyList(),
    val communities: List<CommunityModel> = emptyList(),
    val instances: List<InstanceModel> = emptyList(),
)
