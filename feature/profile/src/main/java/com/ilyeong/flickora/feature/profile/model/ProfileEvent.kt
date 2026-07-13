package com.ilyeong.flickora.feature.profile.model

internal sealed interface ProfileEvent {
    data object NavigateToLogin : ProfileEvent
    data class ShowMessage(val error: Throwable) : ProfileEvent
}
