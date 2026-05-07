package com.ilyeong.movieverse.feature.search.model

import com.ilyeong.movieverse.core.model.Movie

internal sealed interface SearchUiState {
    data object Loading : SearchUiState
    data class Success(val trendMovieList: List<Movie>) : SearchUiState
    data object Failure : SearchUiState
}