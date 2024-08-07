package com.livefast.eattrash.raccoonforlemmy.core.api.service

import com.livefast.eattrash.raccoonforlemmy.core.api.dto.BlockInstanceForm
import com.livefast.eattrash.raccoonforlemmy.core.api.dto.BlockInstanceResponse
import com.livefast.eattrash.raccoonforlemmy.core.api.dto.GetSiteResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Query

interface SiteService {
    @GET("site")
    suspend fun get(
        @Header("Authorization") authHeader: String? = null,
        @Query("auth") auth: String? = null,
    ): GetSiteResponse

    @POST("site/block")
    @Headers("Content-Type: application/json")
    suspend fun block(
        @Header("Authorization") authHeader: String? = null,
        @Body form: BlockInstanceForm,
    ): BlockInstanceResponse
}
