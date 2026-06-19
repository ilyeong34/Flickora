package com.ilyeong.flickora.core.data.oauth.repository

import com.ilyeong.flickora.core.data.oauth.api.OAuthApiService
import com.ilyeong.flickora.core.data.oauth.model.SessionIdRequest
import com.ilyeong.flickora.core.data.oauth.model.toDomain
import com.ilyeong.flickora.core.datastore.user.UserPreferenceDataSource
import com.ilyeong.flickora.core.model.RequestToken
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

internal class OAuthRepositoryImpl @Inject constructor(
    private val apiService: OAuthApiService,
    private val userPreferenceDataSource: UserPreferenceDataSource
) : OAuthRepository {

    override fun isAuthenticated() = flow<Boolean> {
        val isAuthenticated = userPreferenceDataSource.isGuestMode() ||
                userPreferenceDataSource.getSessionId().isNotBlank()
        emit(isAuthenticated)
    }

    override fun createRequestToken() = flow<RequestToken> {
        val requestToken = apiService.createRequestToken().toDomain()
        emit(requestToken)
    }

    override fun createSessionId(requestToken: String) = flow<Unit> {
        val sessionIdResponse = apiService.createSessionId(SessionIdRequest(requestToken))
        require(sessionIdResponse.success) { "알 수 없는 오류가 발생했습니다." }
        userPreferenceDataSource.saveAuthState(sessionIdResponse.sessionId, false)
        emit(Unit)
    }

    override fun logout() = flow<Unit> {
        userPreferenceDataSource.saveAuthState("", false)
        emit(Unit)
    }

    override fun continueAsGuest() = flow<Unit> {
        userPreferenceDataSource.saveAuthState("", true)
        emit(Unit)
    }
}
