package com.ilyeong.flickora.core.data.user.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class TmdbResponse(
    @SerialName("avatar_path") val avatarPath: String = ""
)