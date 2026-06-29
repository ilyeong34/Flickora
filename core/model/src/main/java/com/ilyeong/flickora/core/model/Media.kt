package com.ilyeong.flickora.core.model

sealed class Media(
    open val id: Int,
    open val title: String,
    open val posterPath: String,
    open val overview: String,
    open val voteAverage: Double,
    open val voteCount: Int,
    open val isInWatchlist: Boolean
)
