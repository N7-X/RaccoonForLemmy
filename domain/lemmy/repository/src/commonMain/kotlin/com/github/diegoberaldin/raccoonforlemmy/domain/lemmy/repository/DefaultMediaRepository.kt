package com.github.diegoberaldin.raccoonforlemmy.domain.lemmy.repository

import com.github.diegoberaldin.raccoonforlemmy.core.api.provider.ServiceProvider
import com.github.diegoberaldin.raccoonforlemmy.domain.lemmy.data.MediaModel
import com.github.diegoberaldin.raccoonforlemmy.domain.lemmy.repository.utils.toAuthHeader
import com.github.diegoberaldin.raccoonforlemmy.domain.lemmy.repository.utils.toModel
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class DefaultMediaRepository(
    private val services: ServiceProvider,
) : MediaRepository {
    override suspend fun uploadImage(
        auth: String,
        bytes: ByteArray,
    ): String? =
        withContext(Dispatchers.IO) {
            runCatching {
                val url = "https://${services.currentInstance}/pictrs/image"
                val multipart =
                    MultiPartFormDataContent(
                        formData {
                            append(
                                key = "images[]",
                                value = bytes,
                                headers =
                                    Headers.build {
                                        append(HttpHeaders.ContentType, "image/*")
                                        append(HttpHeaders.ContentDisposition, "filename=image.jpeg")
                                    },
                            )
                        },
                    )
                val images =
                    services.post.uploadImage(
                        url = url,
                        token = "jwt=$auth",
                        authHeader = auth.toAuthHeader(),
                        content = multipart,
                    )
                "$url/${images.files?.firstOrNull()?.file}"
            }.apply {
                exceptionOrNull()?.also {
                    it.printStackTrace()
                }
            }.getOrNull()
        }

    override suspend fun getAll(
        auth: String?,
        page: Int,
        limit: Int,
    ): List<MediaModel> =
        withContext(Dispatchers.IO) {
            runCatching {
                val response =
                    services.user.listMedia(
                        authHeader = auth.toAuthHeader(),
                        page = page,
                        limit = limit,
                    )
                response.images.map { it.toModel() }
            }.getOrElse { emptyList() }
        }

    override suspend fun delete(
        auth: String?,
        media: MediaModel,
    ) = withContext(Dispatchers.IO) {
        val url =
            "https://${services.currentInstance}/pictrs/image/delete/${media.deleteToken}/${media.alias}"
        services.post.deleteImage(
            url = url,
            token = "jwt=$auth",
            authHeader = auth.toAuthHeader(),
        )
    }
}
