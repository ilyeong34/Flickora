package com.ilyeong.flickora.core.data.user.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AvatarResponse(
    @SerialName("gravatar") val gravatarResponse: GravatarResponse,
    @SerialName("tmdb") val tmdbResponse: TmdbResponse
)