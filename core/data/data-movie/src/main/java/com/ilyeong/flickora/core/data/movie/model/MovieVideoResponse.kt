package com.ilyeong.flickora.core.data.movie.model

import com.ilyeong.flickora.core.model.MediaVideo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class MovieVideoListResponse(
    @SerialName("id") val id: Int,
    @SerialName("results") val results: List<MovieVideoResponse> = emptyList()
)

@Serializable
internal data class MovieVideoResponse(
    @SerialName("id") val id: String = "",
    @SerialName("key") val key: String = "",
    @SerialName("name") val name: String = "",
    @SerialName("site") val site: String = "",
    @SerialName("type") val type: String = "",
    @SerialName("official") val official: Boolean = false
)

internal fun MovieVideoResponse.toDomain() = MediaVideo(
    id = id,
    key = key,
    name = name,
    site = site,
    type = type,
    official = official
)
