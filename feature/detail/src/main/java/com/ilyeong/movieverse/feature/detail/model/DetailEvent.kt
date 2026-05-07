package com.ilyeong.movieverse.feature.detail.model

internal sealed interface DetailEvent {
    data class ShowMessage(val error: Throwable) : DetailEvent
}