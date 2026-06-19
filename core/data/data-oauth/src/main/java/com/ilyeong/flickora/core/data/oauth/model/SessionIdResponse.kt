package com.ilyeong.flickora.core.data.oauth.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class SessionIdResponse(
    @SerialName("success") val success: Boolean,
    @SerialName("session_id") val sessionId: String,
)