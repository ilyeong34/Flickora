package com.ilyeong.flickora.feature.home.model

internal data class TrailerPlaybackState(
    val movieId: Int,
    val videoKey: String,
    val currentSecond: Float
)
