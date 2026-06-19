package com.ilyeong.flickora.feature.detail.model

import com.ilyeong.flickora.core.model.Cast
import com.ilyeong.flickora.core.model.Movie

internal sealed interface DetailUiState {

    data object Loading : DetailUiState

    data class Success(
        val movie: Movie,
        val cast: List<Cast>,
        val collectionMovieList: List<Movie>,
        val movieRecommendationList: List<Movie>,
        val movieSimilarList: List<Movie>,
    ) : DetailUiState

    data object Failure : DetailUiState
}