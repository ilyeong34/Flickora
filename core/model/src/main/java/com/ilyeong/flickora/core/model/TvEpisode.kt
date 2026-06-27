package com.ilyeong.flickora.core.model

data class TvEpisode(
    val id: Int,
    val name: String,
    val overview: String,
    val seasonNumber: Int,
    val episodeNumber: Int,
    val airDate: String,
    val runtime: Int,
    val stillPath: String,
    val voteAverage: Double,
)

