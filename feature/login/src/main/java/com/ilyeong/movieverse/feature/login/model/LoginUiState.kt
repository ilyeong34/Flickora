package com.ilyeong.movieverse.feature.login.model

internal data class LoginUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)