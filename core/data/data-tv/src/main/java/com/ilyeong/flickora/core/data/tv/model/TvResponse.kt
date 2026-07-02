package com.ilyeong.flickora.core.data.tv.model

import com.ilyeong.flickora.core.model.Genre
import com.ilyeong.flickora.core.model.TvSeries
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class TvResponse(
    @SerialName("adult") val adult: Boolean = false,
    @SerialName("backdrop_path") val backdropPath: String = "",
    @SerialName("genre_ids") val genreIdList: List<Int> = emptyList(),
    @SerialName("id") val id: Int,
    @SerialName("origin_country") val originCountry: List<String> = emptyList(),
    @SerialName("original_language") val originalLanguage: String = "",
    @SerialName("original_name") val originalName: String = "",
    @SerialName("overview") val overview: String = "",
    @SerialName("popularity") val popularity: Double = 0.0,
    @SerialName("poster_path") val posterPath: String = "",
    @SerialName("first_air_date") val firstAirDate: String = "",
    @SerialName("name") val name: String = "",
    @SerialName("vote_average") val voteAverage: Double = 0.0,
    @SerialName("vote_count") val voteCount: Int = 0
)

internal fun TvResponse.toDomain() = TvSeries(
    adult = adult,
    backdropPath = "https://image.tmdb.org/t/p/original/$backdropPath",
    genreList = genreIdList.map { Genre(it, "") },
    id = id,
    originCountry = originCountry,
    originalLanguage = originalLanguage,
    originalName = originalName,
    overview = overview,
    popularity = popularity,
    posterPath = "https://image.tmdb.org/t/p/original/$posterPath",
    firstAirDate = firstAirDate,
    name = name,
    voteAverage = voteAverage,
    voteCount = voteCount,
)
