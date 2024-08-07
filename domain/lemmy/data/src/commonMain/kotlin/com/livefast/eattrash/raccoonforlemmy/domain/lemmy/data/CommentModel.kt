package com.livefast.eattrash.raccoonforlemmy.domain.lemmy.data

import kotlin.jvm.Transient

data class CommentModel(
    val id: Long = 0,
    val originalUrl: String? = null,
    val postId: Long = 0,
    val postTitle: String? = null,
    val text: String? = null,
    val community: CommunityModel? = null,
    val creator: UserModel? = null,
    val score: Int = 0,
    val upvotes: Int = 0,
    val downvotes: Int = 0,
    val myVote: Int = 0,
    val saved: Boolean = false,
    val publishDate: String? = null,
    val updateDate: String? = null,
    val comments: Int? = null,
    val path: String = "",
    val distinguished: Boolean = false,
    val removed: Boolean = false,
    val deleted: Boolean = false,
    @Transient
    val expanded: Boolean = true,
    @Transient
    val visible: Boolean = true,
    @Transient
    val loadMoreButtonVisible: Boolean = false,
    val languageId: Long = 0,
) {
    val depth: Int get() = (path.split(".").size - 2).coerceAtLeast(0)

    val parentId: Long?
        get() = if (depth == 0) null else path.split(".").dropLast(1).lastOrNull()?.toLongOrNull()
}
