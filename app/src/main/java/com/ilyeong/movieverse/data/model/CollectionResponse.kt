package com.ilyeong.movieverse.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CollectionResponse(
    @SerialName("backdrop_path") val backdropPath: String = "",
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("overview") val overview: String,
    @SerialName("parts") val partResponseList: List<PartResponse> = emptyList(),
    @SerialName("poster_path") val posterPath: String = ""
)

fun CollectionResponse.toDomain() = Collection(
    backdropPath = "https://image.tmdb.org/t/p/original/$backdropPath",
    id = id,
    name = name,
    overview = overview,
    partList = partResponseList.map { it.toDomain() },
    posterPath = posterPath
)