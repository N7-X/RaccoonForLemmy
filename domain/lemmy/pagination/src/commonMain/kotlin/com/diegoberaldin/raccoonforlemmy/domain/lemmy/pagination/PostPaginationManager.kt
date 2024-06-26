package com.diegoberaldin.raccoonforlemmy.domain.lemmy.pagination

import com.github.diegoberaldin.raccoonforlemmy.domain.lemmy.data.PostModel

interface PostPaginationManagerState

interface PostPaginationManager {
    val canFetchMore: Boolean
    val history: List<PostModel>

    fun reset(specification: PostPaginationSpecification? = null)

    suspend fun loadNextPage(): List<PostModel>

    fun extractState(): PostPaginationManagerState

    fun restoreState(state: PostPaginationManagerState)
}
