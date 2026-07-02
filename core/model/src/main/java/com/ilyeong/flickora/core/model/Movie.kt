package com.ilyeong.flickora.core.model

data class Movie(
    val adult: Boolean,
    val collection: Collection?,
    val backdropPath: String,
    val genreList: List<Genre>,
    override val id: Int,
    val originalLanguage: String,
    val originalTitle: String,
    override val overview: String,
    val popularity: Double,
    override val posterPath: String,
    val releaseDate: String,
    val runtime: Int,
    val spokenLanguageList: List<SpokenLanguage>,
    override val title: String,
    val video: Boolean,
    override val voteAverage: Double,
    override val voteCount: Int,
    override val isInWatchlist: Boolean,
    override val videos: List<MediaVideo> = emptyList()
) : Media(
    id = id,
    title = title,
    posterPath = posterPath,
    overview = overview,
    voteAverage = voteAverage,
    voteCount = voteCount,
    isInWatchlist = isInWatchlist,
    videos = videos
)
