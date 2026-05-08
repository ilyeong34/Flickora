package com.ilyeong.movieverse.feature.home.model

import com.ilyeong.movieverse.core.model.Genre
import com.ilyeong.movieverse.core.model.Movie

internal sealed interface HomeUiState {
    data object Loading : HomeUiState

    data class Success(
        val bannerMovieList: List<Movie>,
        val genreList: List<Genre>,
    ) : HomeUiState

    data object Failure : HomeUiState
}