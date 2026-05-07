package com.ilyeong.movieverse.core.data.movie.model

import com.ilyeong.movieverse.core.model.Genre
import com.ilyeong.movieverse.core.model.Movie
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class PartResponse(
    @SerialName("adult") val adult: Boolean,
    @SerialName("backdrop_path") val backdropPath: String = "",
    @SerialName("genre_ids") val genreIdList: List<Int>,
    @SerialName("id") val id: Int,
    @SerialName("media_type") val mediaType: String,
    @SerialName("original_language") val originalLanguage: String,
    @SerialName("original_title") val originalTitle: String,
    @SerialName("overview") val overview: String,
    @SerialName("popularity") val popularity: Double,
    @SerialName("poster_path") val posterPath: String = "",
    @SerialName("release_date") val releaseDate: String,
    @SerialName("title") val title: String,
    @SerialName("video") val video: Boolean,
    @SerialName("vote_average") val voteAverage: Double,
    @SerialName("vote_count") val voteCount: Int
)

internal fun PartResponse.toDomain() = Movie(
    adult = adult,
    backdropPath = "https://image.tmdb.org/t/p/original/$backdropPath",
    collection = null,
    genreList = genreIdList.map { Genre(it, "") },
    id = id,
    originalLanguage = originalLanguage,
    originalTitle = originalTitle,
    overview = overview,
    popularity = popularity,
    posterPath = "https://image.tmdb.org/t/p/original/$posterPath",
    releaseDate = releaseDate,
    runtime = 0,
    spokenLanguageList = emptyList(),
    title = title,
    video = video,
    voteAverage = voteAverage,
    voteCount = voteCount,
    isInWatchlist = false,
)