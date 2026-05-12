package com.ilyeong.movieverse.feature.search.model

import com.ilyeong.movieverse.core.model.Movie

data class SearchUiState(
    val isLoading: Boolean = false,
    val isFailure: Boolean = false,
    val trendMovieList: List<Movie> = emptyList(),
    val query: String = "",
)