package com.ilyeong.flickora.feature.detail.model

import com.ilyeong.flickora.core.model.Cast
import com.ilyeong.flickora.core.model.Movie

internal sealed interface MovieDetailUiState {

    data object Loading : MovieDetailUiState

    data class Success(
        val movie: Movie,
        val cast: List<Cast>,
        val collectionMovieList: List<Movie>,
        val movieRecommendationList: List<Movie>,
        val movieSimilarList: List<Movie>,
    ) : MovieDetailUiState

    data object Failure : MovieDetailUiState
}