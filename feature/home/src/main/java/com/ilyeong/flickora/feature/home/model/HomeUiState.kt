package com.ilyeong.flickora.feature.home.model

import com.ilyeong.flickora.core.model.Genre
import com.ilyeong.flickora.core.model.Media
import com.ilyeong.flickora.core.model.Movie

internal sealed interface HomeUiState {
    data object Loading : HomeUiState

    data class Success(
        val bannerMediaList: List<Media>,
        val rankingMovieList: List<Media>,
        val rankingTvList: List<Media>,
        val genreList: List<Genre>,
        val nowPlayingTrailerList: List<Movie>,
    ) : HomeUiState

    data object Failure : HomeUiState
}
