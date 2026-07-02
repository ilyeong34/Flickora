package com.ilyeong.flickora.core.data.oauth.model

import com.ilyeong.flickora.core.model.RequestToken
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class RequestTokenResponse(
    @SerialName("expires_at") val expiresAt: String,
    @SerialName("request_token") val requestToken: String,
    @SerialName("success") val success: Boolean
)

internal fun RequestTokenResponse.toDomain(): RequestToken {
    require(success) { "An unknown error occurred." }
    return RequestToken(requestToken = requestToken)
}
