package com.livefast.eattrash.raccoonforlemmy.domain.lemmy.repository

import com.livefast.eattrash.raccoonforlemmy.core.utils.cache.LruCache
import com.livefast.eattrash.raccoonforlemmy.domain.lemmy.data.CommentModel
import com.livefast.eattrash.raccoonforlemmy.domain.lemmy.data.CommunityModel
import com.livefast.eattrash.raccoonforlemmy.domain.lemmy.data.PostModel
import com.livefast.eattrash.raccoonforlemmy.domain.lemmy.data.UserModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

internal class DefaultLemmyItemCache(
    private val postCache: LruCache<PostModel>,
    private val communityCache: LruCache<CommunityModel>,
    private val commentCache: LruCache<CommentModel>,
    private val userCache: LruCache<UserModel>,
) : LemmyItemCache {
    override suspend fun putPost(value: PostModel) =
        withContext(Dispatchers.IO) {
            postCache.put(value = value, key = value.id)
        }

    override suspend fun getPost(id: Long): PostModel? = withContext(Dispatchers.IO) { postCache.get(id) }

    override suspend fun putComment(value: CommentModel) =
        withContext(Dispatchers.IO) {
            commentCache.put(value = value, key = value.id)
        }

    override suspend fun getComment(id: Long): CommentModel? = withContext(Dispatchers.IO) { commentCache.get(id) }

    override suspend fun putCommunity(value: CommunityModel) =
        withContext(Dispatchers.IO) {
            communityCache.put(value = value, key = value.id)
        }

    override suspend fun getCommunity(id: Long): CommunityModel? = withContext(Dispatchers.IO) { communityCache.get(id) }

    override suspend fun putUser(value: UserModel) =
        withContext(Dispatchers.IO) {
            userCache.put(value = value, key = value.id)
        }

    override suspend fun getUser(id: Long): UserModel? = withContext(Dispatchers.IO) { userCache.get(id) }
}
