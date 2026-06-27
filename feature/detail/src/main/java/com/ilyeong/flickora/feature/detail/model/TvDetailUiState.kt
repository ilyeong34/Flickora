package com.ilyeong.flickora.feature.detail.model

import com.ilyeong.flickora.core.model.Cast
import com.ilyeong.flickora.core.model.TvSeries

internal sealed interface TvDetailUiState {
    data object Loading : TvDetailUiState

    data class Success(
        val tvSeries: TvSeries,
        val cast: List<Cast>,
        val recommendationList: List<TvSeries>,
        val similarList: List<TvSeries>,
        val selectedSeasonNumber: Int?,
    ) : TvDetailUiState

    data object Failure : TvDetailUiState
}
