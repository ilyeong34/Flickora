package com.ilyeong.movieverse.core.datastore.user

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class UserPreferenceDataSourceImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : UserPreferenceDataSource {
    private val sessionIdKey = stringPreferencesKey("session_id")

    override suspend fun getSessionId(): String =
        dataStore.data.map { preferences -> preferences[sessionIdKey] ?: "" }.first()

    override suspend fun saveSessionId(sessionId: String) {
        dataStore.edit { preferences -> preferences[sessionIdKey] = sessionId }
    }
}