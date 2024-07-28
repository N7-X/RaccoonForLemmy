package com.diegoberaldin.raccoonforlemmy.domain.lemmy.pagination

import com.github.diegoberaldin.raccoonforlemmy.core.notifications.NotificationCenter
import com.github.diegoberaldin.raccoonforlemmy.core.notifications.NotificationCenterEvent
import com.github.diegoberaldin.raccoonforlemmy.core.persistence.repository.AccountRepository
import com.github.diegoberaldin.raccoonforlemmy.core.persistence.repository.DomainBlocklistRepository
import com.github.diegoberaldin.raccoonforlemmy.domain.identity.repository.IdentityRepository
import com.github.diegoberaldin.raccoonforlemmy.domain.lemmy.data.PostModel
import com.github.diegoberaldin.raccoonforlemmy.domain.lemmy.data.SearchResult
import com.github.diegoberaldin.raccoonforlemmy.domain.lemmy.data.SearchResultType
import com.github.diegoberaldin.raccoonforlemmy.domain.lemmy.data.SortType
import com.github.diegoberaldin.raccoonforlemmy.domain.lemmy.repository.CommunityRepository
import com.github.diegoberaldin.raccoonforlemmy.domain.lemmy.repository.PostRepository
import com.github.diegoberaldin.raccoonforlemmy.domain.lemmy.repository.UserRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext

internal class DefaultPostPaginationManager(
    private val identityRepository: IdentityRepository,
    private val accountRepository: AccountRepository,
    private val postRepository: PostRepository,
    private val communityRepository: CommunityRepository,
    private val userRepository: UserRepository,
    private val multiCommunityPaginator: MultiCommunityPaginator,
    private val domainBlocklistRepository: DomainBlocklistRepository,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    notificationCenter: NotificationCenter,
) : PostPaginationManager {
    override var canFetchMore: Boolean = true
        private set
    override val history: MutableList<PostModel> = mutableListOf()

    private var specification: PostPaginationSpecification? = null
    private var currentPage: Int = 1
    private var pageCursor: String? = null
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + dispatcher)
    private var blockedDomains: List<String>? = null

    init {
        notificationCenter
            .subscribe(NotificationCenterEvent.PostUpdated::class)
            .onEach { evt ->
                handlePostUpdate(evt.model)
            }.launchIn(scope)
    }

    override fun reset(specification: PostPaginationSpecification?) {
        this.specification = specification
        history.clear()
        canFetchMore = true
        currentPage = 1
        pageCursor = null
        multiCommunityPaginator.reset()
        blockedDomains = null
        (specification as? PostPaginationSpecification.MultiCommunity)?.also {
            multiCommunityPaginator.setCommunities(it.communityIds)
        }
    }

    override suspend fun loadNextPage(): List<PostModel> =
        withContext(Dispatchers.IO) {
            val specification = specification ?: return@withContext emptyList()
            val auth = identityRepository.authToken.value.orEmpty()
            if (blockedDomains == null) {
                val accountId = accountRepository.getActive()?.id
                blockedDomains = domainBlocklistRepository.get(accountId)
            }

            val result =
                when (specification) {
                    is PostPaginationSpecification.Listing -> {
                        val (itemList, nextPage) =
                            postRepository.getAll(
                                auth = auth,
                                page = currentPage,
                                pageCursor = pageCursor,
                                type = specification.listingType,
                                sort = specification.sortType,
                            ) ?: (null to null)
                        if (!itemList.isNullOrEmpty()) {
                            currentPage++
                        }
                        if (nextPage != null) {
                            pageCursor = nextPage
                        }
                        canFetchMore = itemList?.isEmpty() != true
                        itemList
                            .orEmpty()
                            .deduplicate()
                            .filterNsfw(specification.includeNsfw)
                            .filterDeleted()
                            .filterByUrlDomain()
                    }

                    is PostPaginationSpecification.Community -> {
                        val searching = !specification.query.isNullOrEmpty()
                        val (itemList, nextPage) =
                            if (searching) {
                                communityRepository
                                    .search(
                                        auth = auth,
                                        communityId = specification.id,
                                        page = currentPage,
                                        sortType = specification.sortType,
                                        resultType = SearchResultType.Posts,
                                        query = specification.query.orEmpty(),
                                    ).mapNotNull {
                                        (it as? SearchResult.Post)?.model
                                    } to null
                            } else {
                                postRepository.getAll(
                                    auth = auth,
                                    otherInstance = specification.otherInstance,
                                    communityId = specification.id,
                                    communityName = specification.name,
                                    page = currentPage,
                                    pageCursor = pageCursor,
                                    sort = specification.sortType,
                                ) ?: (null to null)
                            }
                        if (!itemList.isNullOrEmpty()) {
                            currentPage++
                        }
                        if (nextPage != null) {
                            pageCursor = nextPage
                        }
                        canFetchMore = itemList?.isEmpty() != true
                        itemList
                            .orEmpty()
                            .deduplicate()
                            .filterNsfw(specification.includeNsfw)
                            .filterDeleted(includeCurrentCreator = true)
                            .filterByUrlDomain()
                    }

                    is PostPaginationSpecification.MultiCommunity -> {
                        val itemList =
                            multiCommunityPaginator.loadNextPage(
                                auth = auth,
                                sort = specification.sortType,
                            )
                        canFetchMore = multiCommunityPaginator.canFetchMore
                        itemList
                            .deduplicate()
                            .filterNsfw(specification.includeNsfw)
                            .filterDeleted(includeCurrentCreator = true)
                            .filterByUrlDomain()
                    }

                    is PostPaginationSpecification.User -> {
                        val itemList =
                            userRepository.getPosts(
                                auth = auth,
                                id = specification.id,
                                username = specification.name,
                                otherInstance = specification.otherInstance,
                                page = currentPage,
                                sort = SortType.New,
                            )
                        if (!itemList.isNullOrEmpty()) {
                            currentPage++
                        }
                        canFetchMore = itemList?.isEmpty() != true
                        itemList
                            .orEmpty()
                            .deduplicate()
                            .filterNsfw(specification.includeNsfw)
                            .filterDeleted(includeCurrentCreator = specification.includeDeleted)
                            .filterByUrlDomain()
                    }

                    is PostPaginationSpecification.Votes -> {
                        val (itemList, nextPage) =
                            userRepository.getLikedPosts(
                                auth = auth,
                                page = currentPage,
                                pageCursor = pageCursor,
                                liked = specification.liked,
                                sort = SortType.New,
                            ) ?: (null to null)
                        if (!itemList.isNullOrEmpty()) {
                            currentPage++
                        }
                        if (nextPage != null) {
                            pageCursor = nextPage
                        }
                        canFetchMore = itemList?.isEmpty() != true
                        itemList
                            .orEmpty()
                            .deduplicate()
                            .filterDeleted(includeCurrentCreator = true)
                            .filterByUrlDomain()
                    }

                    is PostPaginationSpecification.Saved -> {
                        val itemList =
                            userRepository.getSavedPosts(
                                auth = auth,
                                page = currentPage,
                                sort = specification.sortType,
                                id = identityRepository.cachedUser?.id ?: 0,
                            )
                        if (!itemList.isNullOrEmpty()) {
                            currentPage++
                        }
                        canFetchMore = itemList?.isEmpty() != true
                        itemList
                            .orEmpty()
                            .deduplicate()
                            .filterDeleted()
                            .filterByUrlDomain()
                    }

                    is PostPaginationSpecification.Hidden -> {
                        val (itemList, nextPage) =
                            userRepository.getHiddenPosts(
                                auth = auth,
                                page = currentPage,
                                pageCursor = pageCursor,
                                sort = SortType.New,
                            ) ?: (null to null)
                        if (!itemList.isNullOrEmpty()) {
                            currentPage++
                        }
                        if (nextPage != null) {
                            pageCursor = nextPage
                        }
                        canFetchMore = itemList?.isEmpty() != true
                        itemList
                            .orEmpty()
                            .deduplicate()
                            .filterDeleted()
                            .filterByUrlDomain()
                    }
                }

            history.addAll(result)
            // returns a copy of the whole history
            history.map { it }
        }

    override fun extractState(): PostPaginationManagerState =
        DefaultPostPaginationManagerState(
            specification = specification,
            currentPage = currentPage,
            pageCursor = pageCursor,
            blockedDomains = blockedDomains,
            history = history,
        )

    override fun restoreState(state: PostPaginationManagerState) {
        (state as? DefaultPostPaginationManagerState)?.also {
            currentPage = it.currentPage
            specification = it.specification
            pageCursor = it.pageCursor
            history.clear()
            blockedDomains = it.blockedDomains
            history.addAll(it.history)
        }
    }

    private fun List<PostModel>.deduplicate(): List<PostModel> =
        filter { p1 ->
            // prevents accidental duplication
            history.none { p2 -> p2.id == p1.id }
        }

    private fun List<PostModel>.filterNsfw(includeNsfw: Boolean): List<PostModel> =
        if (includeNsfw) {
            this
        } else {
            filter { post -> !post.nsfw }
        }

    private fun List<PostModel>.filterDeleted(includeCurrentCreator: Boolean = false): List<PostModel> {
        val currentUserId = identityRepository.cachedUser?.id
        return filter { post ->
            !post.deleted || (includeCurrentCreator && post.creator?.id == currentUserId)
        }
    }

    private fun List<PostModel>.filterByUrlDomain(): List<PostModel> {
        return filter { post ->
            blockedDomains?.takeIf { it.isNotEmpty() }?.let { blockList ->
                blockList.none { domain -> post.url?.contains(domain) ?: true }
            } ?: true
        }
    }

    private fun handlePostUpdate(post: PostModel) {
        val index = history.indexOfFirst { it.id == post.id }.takeIf { it >= 0 } ?: return
        history.removeAt(index)
        history.add(index, post)
    }
}
