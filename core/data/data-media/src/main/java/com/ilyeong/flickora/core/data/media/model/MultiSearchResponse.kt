package com.ilyeong.flickora.core.data.media.model

import com.ilyeong.flickora.core.model.Genre
import com.ilyeong.flickora.core.model.Media
import com.ilyeong.flickora.core.model.Movie
import com.ilyeong.flickora.core.model.TvSeries
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class MultiSearchResponse(
    @SerialName("page") val page: Int,
    @SerialName("results") val resultList: List<MultiSearchItemResponse> = emptyList(),
    @SerialName("total_pages") val totalPages: Int,
    @SerialName("total_results") val totalResults: Int
)

@Serializable
internal data class MultiSearchItemResponse(
    @SerialName("adult") val adult: Boolean = false,
    @SerialName("backdrop_path") val backdropPath: String = "",
    @SerialName("first_air_date") val firstAirDate: String = "",
    @SerialName("genre_ids") val genreIdList: List<Int> = emptyList(),
    @SerialName("id") val id: Int,
    @SerialName("media_type") val mediaType: String = "",
    @SerialName("name") val name: String = "",
    @SerialName("origin_country") val originCountry: List<String> = emptyList(),
    @SerialName("original_language") val originalLanguage: String = "",
    @SerialName("original_name") val originalName: String = "",
    @SerialName("original_title") val originalTitle: String = "",
    @SerialName("overview") val overview: String = "",
    @SerialName("popularity") val popularity: Double = 0.0,
    @SerialName("poster_path") val posterPath: String = "",
    @SerialName("release_date") val releaseDate: String = "",
    @SerialName("title") val title: String = "",
    @SerialName("video") val video: Boolean = false,
    @SerialName("vote_average") val voteAverage: Double = 0.0,
    @SerialName("vote_count") val voteCount: Int = 0
)

internal fun MultiSearchItemResponse.toDomain(): Media? {
    return when (mediaType) {
        "movie" -> toMovie()
        "tv" -> toTvSeries()
        else -> null
    }
}

private fun MultiSearchItemResponse.toMovie() = Movie(
    adult = adult,
    backdropPath = toImageUrl(backdropPath),
    collection = null,
    genreList = genreIdList.map { Genre(it, "") },
    id = id,
    originalLanguage = originalLanguage,
    originalTitle = originalTitle,
    overview = overview,
    popularity = popularity,
    posterPath = toImageUrl(posterPath),
    releaseDate = releaseDate,
    runtime = 0,
    spokenLanguageList = emptyList(),
    title = title,
    video = video,
    voteAverage = voteAverage,
    voteCount = voteCount,
    isInWatchlist = false,
)

private fun MultiSearchItemResponse.toTvSeries() = TvSeries(
    adult = adult,
    backdropPath = toImageUrl(backdropPath),
    genreList = genreIdList.map { Genre(it, "") },
    id = id,
    originCountry = originCountry,
    originalLanguage = originalLanguage,
    originalName = originalName,
    overview = overview,
    popularity = popularity,
    posterPath = toImageUrl(posterPath),
    firstAirDate = firstAirDate,
    name = name,
    voteAverage = voteAverage,
    voteCount = voteCount,
)

private fun toImageUrl(path: String): String {
    return when (path.isBlank()) {
        true -> ""
        false -> "https://image.tmdb.org/t/p/original/$path"
    }
}
