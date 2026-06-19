package com.ilyeong.flickora.feature.profile.model

import com.ilyeong.flickora.core.model.Account

internal sealed interface ProfileUiState {
    data object Loading : ProfileUiState
    data class Success(val account: Account) : ProfileUiState
    data object Failure : ProfileUiState
}
