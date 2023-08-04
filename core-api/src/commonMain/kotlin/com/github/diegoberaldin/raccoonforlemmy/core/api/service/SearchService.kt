package com.github.diegoberaldin.raccoonforlemmy.core.api.service

import com.github.diegoberaldin.raccoonforlemmy.core.api.dto.ListingType
import com.github.diegoberaldin.raccoonforlemmy.core.api.dto.SearchResponse
import com.github.diegoberaldin.raccoonforlemmy.core.api.dto.SearchType
import com.github.diegoberaldin.raccoonforlemmy.core.api.dto.SortType
import de.jensklingenberg.ktorfit.Response
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query

interface SearchService {
    @GET("search")
    suspend fun search(
        @Query("q") q: String,
        @Query("community_id") communityId: Int? = null,
        @Query("community_name") communityName: String? = null,
        @Query("creator_id") creatorId: Int? = null,
        @Query("type_") type: SearchType? = null,
        @Query("sort") sort: SortType? = null,
        @Query("listing_type") listingType: ListingType? = null,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null,
        @Query("auth") auth: String? = null,
    ): Response<SearchResponse>
}
