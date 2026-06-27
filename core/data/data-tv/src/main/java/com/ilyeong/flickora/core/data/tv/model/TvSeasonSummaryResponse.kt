package com.ilyeong.flickora.core.data.tv.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class TvSeasonSummaryResponse(
    @SerialName("air_date") val airDate: String = "",
    @SerialName("episode_count") val episodeCount: Int = 0,
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String = "",
    @SerialName("overview") val overview: String = "",
    @SerialName("poster_path") val posterPath: String = "",
    @SerialName("season_number") val seasonNumber: Int = 0,
    @SerialName("vote_average") val voteAverage: Double = 0.0,
)

