package com.ilyeong.movieverse.core.data.movie.model

import com.ilyeong.movieverse.core.model.Collection
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class BelongsToCollectionResponse(
    @SerialName("backdrop_path") val backdropPath: String = "",
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("poster_path") val posterPath: String = "",
)

internal fun BelongsToCollectionResponse.toDomain() = Collection(
    backdropPath = "https://image.tmdb.org/t/p/original/$backdropPath",
    id = id,
    name = name,
    overview = "",
    partList = emptyList(),
    posterPath = "https://image.tmdb.org/t/p/original/$posterPath",
)