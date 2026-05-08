package com.ilyeong.movieverse.feature.profile.model

import com.ilyeong.movieverse.core.model.Account

internal sealed interface ProfileUiState {
    data object Loading : ProfileUiState
    data class Success(val account: Account) : ProfileUiState
    data object Failure : ProfileUiState
}
