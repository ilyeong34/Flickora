package com.ilyeong.movieverse.feature.search.model

import com.ilyeong.movieverse.core.model.Movie

internal data class SearchUiState(
    val trendState: TrendState = TrendState.Loading,
    val query: String = "",
)

internal sealed interface TrendState {
    object Loading : TrendState
    data class Success(val movieList: List<Movie>) : TrendState
    object Failure : TrendState
}