package com.ilyeong.flickora.feature.detail.tvdetail.model

import com.ilyeong.flickora.core.model.TvSeries

sealed interface TvDetailUiState {
    data object Loading : TvDetailUiState
    data class Success(val tvSeries: TvSeries) : TvDetailUiState
    data object Failure : TvDetailUiState
}
