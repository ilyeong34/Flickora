package com.ilyeong.flickora.feature.home.model

import com.ilyeong.flickora.core.model.Genre
import com.ilyeong.flickora.core.model.Media

internal sealed interface HomeUiState {
    data object Loading : HomeUiState

    data class Success(
        val bannerMediaList: List<Media>,
        val rankingMediaList: List<Media>,
        val genreList: List<Genre>,
    ) : HomeUiState

    data object Failure : HomeUiState
}
