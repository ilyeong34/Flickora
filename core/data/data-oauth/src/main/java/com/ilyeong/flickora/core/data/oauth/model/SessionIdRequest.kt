package com.ilyeong.flickora.core.data.oauth.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class SessionIdRequest(
    @SerialName("request_token") val requestToken: String,
)