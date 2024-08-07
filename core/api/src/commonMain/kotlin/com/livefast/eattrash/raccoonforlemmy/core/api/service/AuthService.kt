package com.livefast.eattrash.raccoonforlemmy.core.api.service

import com.livefast.eattrash.raccoonforlemmy.core.api.dto.LoginForm
import com.livefast.eattrash.raccoonforlemmy.core.api.dto.LoginResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST

interface AuthService {
    @POST("user/login")
    @Headers("Content-Type: application/json")
    suspend fun login(
        @Body form: LoginForm,
    ): LoginResponse
}
