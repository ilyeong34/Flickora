package com.ilyeong.flickora.core.model

data class TvSeries(
    val adult: Boolean,
    val backdropPath: String,
    val genreList: List<Genre>,
    override val id: Int,
    val originCountry: List<String>,
    val originalLanguage: String,
    val originalName: String,
    override val overview: String,
    val popularity: Double,
    override val posterPath: String,
    val firstAirDate: String,
    val name: String,
    override val voteAverage: Double,
    override val voteCount: Int,
    val lastAirDate: String = "",
    val status: String = "",
    val numberOfSeasons: Int = 0,
    val episodeRunTime: List<Int> = emptyList(),
    val spokenLanguageList: List<SpokenLanguage> = emptyList(),
    val numberOfEpisodes: Int = 0,
    val inProduction: Boolean = false,
    val seasonList: List<TvSeason> = emptyList(),
    override val isInWatchlist: Boolean = false,
    override val videos: List<MediaVideo> = emptyList()
) : Media(
    id = id,
    title = name.ifBlank { originalName },
    posterPath = posterPath,
    overview = overview,
    voteAverage = voteAverage,
    voteCount = voteCount,
    isInWatchlist = isInWatchlist,
    videos = videos
)
