package com.ilyeong.flickora.core.data.user.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class GravatarResponse(
    @SerialName("hash") val hash: String = ""
)