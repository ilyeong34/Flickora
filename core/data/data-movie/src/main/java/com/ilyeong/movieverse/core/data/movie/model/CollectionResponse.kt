package com.ilyeong.movieverse.core.data.movie.model

import com.ilyeong.movieverse.core.model.Collection
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class CollectionResponse(
    @SerialName("backdrop_path") val backdropPath: String = "",
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("overview") val overview: String,
    @SerialName("parts") val partResponseList: List<PartResponse> = emptyList(),
    @SerialName("poster_path") val posterPath: String = ""
)

internal fun CollectionResponse.toDomain() = Collection(
    backdropPath = "https://image.tmdb.org/t/p/original/$backdropPath",
    id = id,
    name = name,
    overview = overview,
    partList = partResponseList.map { it.toDomain() },
    posterPath = posterPath
)