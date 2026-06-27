package com.ilyeong.flickora.core.data.tv.model

import com.ilyeong.flickora.core.model.TvEpisode
import com.ilyeong.flickora.core.model.TvSeason
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class TvSeasonDetailResponse(
    @SerialName("air_date") val airDate: String = "",
    @SerialName("episodes") val episodeList: List<TvEpisodeResponse> = emptyList(),
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String = "",
    @SerialName("overview") val overview: String = "",
    @SerialName("poster_path") val posterPath: String = "",
    @SerialName("season_number") val seasonNumber: Int = 0,
    @SerialName("vote_average") val voteAverage: Double = 0.0,
)

@Serializable
internal data class TvEpisodeResponse(
    @SerialName("air_date") val airDate: String = "",
    @SerialName("episode_number") val episodeNumber: Int = 0,
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String = "",
    @SerialName("overview") val overview: String = "",
    @SerialName("runtime") val runtime: Int = 0,
    @SerialName("season_number") val seasonNumber: Int = 0,
    @SerialName("still_path") val stillPath: String = "",
    @SerialName("vote_average") val voteAverage: Double = 0.0,
)

internal fun TvSeasonDetailResponse.toDomain(
    summary: TvSeasonSummaryResponse
) = TvSeason(
    id = id,
    name = name.ifBlank { summary.name },
    overview = overview.ifBlank { summary.overview },
    seasonNumber = seasonNumber,
    episodeCount = summary.episodeCount,
    airDate = airDate.ifBlank { summary.airDate },
    posterPath = "https://image.tmdb.org/t/p/original/${posterPath.ifBlank { summary.posterPath }}",
    voteAverage = when (voteAverage == 0.0) {
        true -> summary.voteAverage
        false -> voteAverage
    },
    episodeList = episodeList.map { it.toDomain() },
)

internal fun TvEpisodeResponse.toDomain() = TvEpisode(
    id = id,
    name = name,
    overview = overview,
    seasonNumber = seasonNumber,
    episodeNumber = episodeNumber,
    airDate = airDate,
    runtime = runtime,
    stillPath = "https://image.tmdb.org/t/p/original/$stillPath",
    voteAverage = voteAverage,
)

