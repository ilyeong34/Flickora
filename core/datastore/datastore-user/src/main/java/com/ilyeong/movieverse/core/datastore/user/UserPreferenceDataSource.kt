package com.ilyeong.movieverse.core.datastore.user

interface UserPreferenceDataSource {
    suspend fun getSessionId(): String
    suspend fun saveSessionId(sessionId: String)
}