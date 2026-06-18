package com.ilyeong.movieverse.core.datastore.user

interface UserPreferenceDataSource {
    suspend fun getSessionId(): String
    suspend fun saveSessionId(sessionId: String)
    suspend fun isGuestMode(): Boolean
    suspend fun saveGuestMode(isGuest: Boolean)
    suspend fun saveAuthState(sessionId: String, isGuest: Boolean)
}
