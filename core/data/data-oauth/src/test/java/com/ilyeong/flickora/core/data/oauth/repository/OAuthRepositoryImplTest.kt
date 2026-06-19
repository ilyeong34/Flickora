package com.ilyeong.flickora.core.data.oauth.repository

import com.ilyeong.flickora.core.data.oauth.api.OAuthApiService
import com.ilyeong.flickora.core.data.oauth.model.RequestTokenResponse
import com.ilyeong.flickora.core.data.oauth.model.SessionIdRequest
import com.ilyeong.flickora.core.data.oauth.model.SessionIdResponse
import com.ilyeong.flickora.core.datastore.user.UserPreferenceDataSource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class OAuthRepositoryImplTest {

    @Test
    fun createSessionId_clearsGuestModeAfterSavingSession() = runBlocking {
        val api = FakeOAuthApiService()
        val prefs = FakeUserPreferenceDataSource()
        val repository = OAuthRepositoryImpl(api, prefs)

        repository.createSessionId("request-token").first()

        assertEquals("session-123", prefs.sessionId)
        assertFalse(prefs.isGuestMode)
        assertEquals("request-token", api.lastSessionRequest?.requestToken)
    }

    private class FakeOAuthApiService : OAuthApiService {
        var lastSessionRequest: SessionIdRequest? = null

        override suspend fun createRequestToken(): RequestTokenResponse =
            error("createRequestToken is not used in this test")

        override suspend fun createSessionId(requestToken: SessionIdRequest): SessionIdResponse {
            lastSessionRequest = requestToken
            return SessionIdResponse(
                success = true,
                sessionId = "session-123"
            )
        }
    }

    private class FakeUserPreferenceDataSource : UserPreferenceDataSource {
        var sessionId: String = ""
        var isGuestMode: Boolean = true

        override suspend fun getSessionId(): String = sessionId
        override suspend fun saveSessionId(sessionId: String) {
            saveAuthState(sessionId, isGuestMode)
        }

        override suspend fun isGuestMode(): Boolean = isGuestMode

        override suspend fun saveGuestMode(isGuest: Boolean) {
            saveAuthState(sessionId, isGuest)
        }

        override suspend fun saveAuthState(sessionId: String, isGuest: Boolean) {
            this.sessionId = sessionId
            isGuestMode = isGuest
        }
    }
}
