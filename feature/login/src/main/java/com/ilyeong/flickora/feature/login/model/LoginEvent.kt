package com.ilyeong.flickora.feature.login.model

internal sealed interface LoginEvent {
    data class NavigateToCustomTabs(val url: String) : LoginEvent
    data object NavigateToMain : LoginEvent
    data class ShowMessage(val error: Throwable) : LoginEvent
}