package com.ilyeong.movieverse.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ilyeong.movieverse.core.data.oauth.repository.OAuthRepository
import com.ilyeong.movieverse.core.data.user.repository.UserRepository
import com.ilyeong.movieverse.feature.profile.model.ProfileEvent
import com.ilyeong.movieverse.feature.profile.model.ProfileEvent.NavigateToLogin
import com.ilyeong.movieverse.feature.profile.model.ProfileUiState
import com.ilyeong.movieverse.feature.profile.model.ProfileUiState.Failure
import com.ilyeong.movieverse.feature.profile.model.ProfileUiState.Loading
import com.ilyeong.movieverse.feature.profile.model.ProfileUiState.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject
import com.ilyeong.movieverse.core.ui.R as UiR

@HiltViewModel
internal class ProfileViewModel @Inject constructor(
    private val oAuthRepository: OAuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState: MutableStateFlow<ProfileUiState> = MutableStateFlow(Loading)
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<ProfileEvent>()
    val events = _events.asSharedFlow()

    fun loadData() {
        if (_uiState.value is Success) return

        userRepository.getAccount()
            .onStart { _uiState.value = Loading }
            .onEach { _uiState.value = Success(it) }
            .catch { _uiState.value = Failure }
            .launchIn(viewModelScope)
    }

    fun logout() {
        oAuthRepository.logout()
            .onEach { _events.emit(NavigateToLogin) }
            .catch { _events.emit(ProfileEvent.ShowMessage(UiR.string.fail_login_message)) }
            .launchIn(viewModelScope)
    }
}
