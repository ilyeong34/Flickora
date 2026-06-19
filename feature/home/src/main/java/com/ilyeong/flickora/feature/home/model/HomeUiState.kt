package com.ilyeong.flickora.feature.home.model

import com.ilyeong.flickora.core.model.Genre
import com.ilyeong.flickora.core.model.Movie

internal sealed interface HomeUiState {
    data object Loading : HomeUiState

    data class Success(
        val bannerMovieList: List<Movie>,
        val genreList: List<Genre>,
    ) : HomeUiState

    data object Failure : HomeUiState
}