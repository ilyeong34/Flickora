package com.ilyeong.movieverse.feature.login.model

data class LoginUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)