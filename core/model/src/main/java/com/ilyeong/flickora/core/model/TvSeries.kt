package com.ilyeong.flickora.core.model

data class TvSeries(
    val adult: Boolean,
    val backdropPath: String,
    val genreList: List<Genre>,
    val id: Int,
    val originCountry: List<String>,
    val originalLanguage: String,
    val originalName: String,
    val overview: String,
    val popularity: Double,
    val posterPath: String,
    val firstAirDate: String,
    val name: String,
    val voteAverage: Double,
    val voteCount: Int,
    val lastAirDate: String = "",
    val status: String = "",
    val numberOfSeasons: Int = 0,
    val episodeRunTime: List<Int> = emptyList(),
    val spokenLanguageList: List<SpokenLanguage> = emptyList(),
    val numberOfEpisodes: Int = 0,
    val inProduction: Boolean = false,
    val seasonList: List<TvSeason> = emptyList(),
    val isInWatchlist: Boolean
)
