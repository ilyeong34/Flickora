package com.ilyeong.flickora.core.ui.common.model

import com.ilyeong.flickora.core.model.Movie
import com.ilyeong.flickora.core.model.TvSeries

data class PosterUiModel(
    val id: Int,
    val posterPath: String,
    val title: String,
    val overview: String = "",
    val voteAverage: Double = 0.0,
    val voteCount: Int = 0
)

fun Movie.toPosterUiModel() = PosterUiModel(
    id = id,
    posterPath = posterPath,
    title = title,
    overview = overview,
    voteAverage = voteAverage,
    voteCount = voteCount
)

fun TvSeries.toPosterUiModel() = PosterUiModel(
    id = id,
    posterPath = posterPath,
    title = name.ifBlank { originalName },
    overview = overview,
    voteAverage = voteAverage,
    voteCount = voteCount
)
