package com.ilyeong.flickora.feature.profile.model

import androidx.annotation.StringRes

internal sealed interface ProfileEvent {
    data object NavigateToLogin : ProfileEvent
    data class ShowMessage(@StringRes val message: Int) : ProfileEvent
}
