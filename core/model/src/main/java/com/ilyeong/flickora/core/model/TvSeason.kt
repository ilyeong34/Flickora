package com.ilyeong.flickora.core.model

data class TvSeason(
    val id: Int,
    val name: String,
    val overview: String,
    val seasonNumber: Int,
    val episodeCount: Int,
    val airDate: String,
    val posterPath: String,
    val voteAverage: Double,
    val episodeList: List<TvEpisode>,
)

