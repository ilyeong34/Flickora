package com.ilyeong.flickora.core.data.tv.model

import com.ilyeong.flickora.core.model.Genre
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class GenreResponse(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
)

internal fun GenreResponse.toDomain() = Genre(
    id = id,
    name = name
)
