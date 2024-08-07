package com.livefast.eattrash.raccoonforlemmy.domain.lemmy.data

data class PersonMentionModel(
    val id: Long = 0,
    val post: PostModel,
    val creator: UserModel,
    val comment: CommentModel,
    val community: CommunityModel,
    val score: Int = 0,
    val upvotes: Int = 0,
    val downvotes: Int = 0,
    val myVote: Int = 0,
    val saved: Boolean = false,
    val isCommentReply: Boolean = false,
    val publishDate: String? = null,
    val read: Boolean = false,
)
