package com.ilyeong.movieverse.feature.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ilyeong.movieverse.core.data.oauth.repository.OAuthRepository
import com.ilyeong.movieverse.feature.login.model.LoginEvent
import com.ilyeong.movieverse.feature.login.model.LoginUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class LoginViewModel @Inject constructor(
    private val oAuthRepository: OAuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<LoginEvent>()
    val events = _events.asSharedFlow()

    fun automaticallyLogin() {
        viewModelScope.launch {
            val verifySessionId = oAuthRepository.verifySessionId()
            if (verifySessionId) {
                _events.emit(LoginEvent.NavigateToMain)
            }
        }
    }

    fun createRequestToken() {
        oAuthRepository.createRequestToken()
            .onStart { _uiState.update { it.copy(isLoading = true) } }
            .onEach { _events.emit(LoginEvent.NavigateToCustomTabs("https://www.themoviedb.org/authenticate/${it.requestToken}?redirect_to=ilyeong://movieverse")) }
            .onCompletion { _uiState.update { it.copy(isLoading = false) } }
            .catch { _events.emit(LoginEvent.ShowMessage(it)) }
            .launchIn(viewModelScope)
    }

    fun createSessionId(requestToken: String) {
        oAuthRepository.createSessionId(requestToken)
            .onStart { _uiState.update { it.copy(isLoading = true) } }
            .onEach { _events.emit(LoginEvent.NavigateToMain) }
            .onCompletion { _uiState.update { it.copy(isLoading = false) } }
            .catch { _events.emit(LoginEvent.ShowMessage(it)) }
            .launchIn(viewModelScope)
    }
}