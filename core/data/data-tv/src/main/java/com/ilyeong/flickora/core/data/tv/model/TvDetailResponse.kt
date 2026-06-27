package com.ilyeong.flickora.core.data.tv.model

import com.ilyeong.flickora.core.model.TvSeries
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class TvDetailResponse(
    @SerialName("adult") val adult: Boolean = false,
    @SerialName("backdrop_path") val backdropPath: String = "",
    @SerialName("episode_run_time") val episodeRunTime: List<Int> = emptyList(),
    @SerialName("first_air_date") val firstAirDate: String = "",
    @SerialName("genres") val genreList: List<GenreResponse> = emptyList(),
    @SerialName("id") val id: Int,
    @SerialName("in_production") val inProduction: Boolean = false,
    @SerialName("last_air_date") val lastAirDate: String = "",
    @SerialName("name") val name: String = "",
    @SerialName("number_of_episodes") val numberOfEpisodes: Int = 0,
    @SerialName("number_of_seasons") val numberOfSeasons: Int = 0,
    @SerialName("origin_country") val originCountry: List<String> = emptyList(),
    @SerialName("original_language") val originalLanguage: String = "",
    @SerialName("original_name") val originalName: String = "",
    @SerialName("overview") val overview: String = "",
    @SerialName("popularity") val popularity: Double = 0.0,
    @SerialName("poster_path") val posterPath: String = "",
    @SerialName("seasons") val seasonList: List<TvSeasonSummaryResponse> = emptyList(),
    @SerialName("spoken_languages") val spokenLanguageList: List<SpokenLanguageResponse> = emptyList(),
    @SerialName("status") val status: String = "",
    @SerialName("vote_average") val voteAverage: Double = 0.0,
    @SerialName("vote_count") val voteCount: Int = 0
)

internal fun TvDetailResponse.toDomain() = TvSeries(
    adult = adult,
    backdropPath = "https://image.tmdb.org/t/p/original/$backdropPath",
    genreList = genreList.map { it.toDomain() },
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
    lastAirDate = lastAirDate,
    status = status,
    numberOfSeasons = numberOfSeasons,
    episodeRunTime = episodeRunTime,
    spokenLanguageList = spokenLanguageList.map { it.toDomain() },
    numberOfEpisodes = numberOfEpisodes,
    inProduction = inProduction,
)
