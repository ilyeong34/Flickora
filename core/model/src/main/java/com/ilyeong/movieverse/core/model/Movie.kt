package com.ilyeong.movieverse.core.model

data class Movie(
    val adult: Boolean,
    val collection: Collection?,
    val backdropPath: String,
    val genreList: List<Genre>,
    val id: Int,
    val originalLanguage: String,
    val originalTitle: String,
    val overview: String,
    val popularity: Double,
    val posterPath: String,
    val releaseDate: String,
    val runtime: Int,
    val spokenLanguageList: List<SpokenLanguage>,
    val title: String,
    val video: Boolean,
    val voteAverage: Double,
    val voteCount: Int,
    val isInWatchlist: Boolean
)