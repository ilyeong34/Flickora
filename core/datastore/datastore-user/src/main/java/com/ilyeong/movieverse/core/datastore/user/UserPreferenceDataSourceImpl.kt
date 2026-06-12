package com.ilyeong.movieverse.core.datastore.user

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Named

internal class UserPreferenceDataSourceImpl @Inject constructor(
    @Named("user") private val dataStore: DataStore<Preferences>
) : UserPreferenceDataSource {
    private val sessionIdKey = stringPreferencesKey("session_id")
    private val isGuestKey = booleanPreferencesKey("is_guest")

    override suspend fun getSessionId(): String =
        dataStore.data.map { preferences -> preferences[sessionIdKey] ?: "" }.first()

    override suspend fun saveSessionId(sessionId: String) {
        dataStore.edit { preferences -> preferences[sessionIdKey] = sessionId }
    }

    override suspend fun isGuestMode(): Boolean =
        dataStore.data.map { preferences -> preferences[isGuestKey] ?: true }.first()

    override suspend fun saveGuestMode(isGuest: Boolean) {
        dataStore.edit { preferences -> preferences[isGuestKey] = isGuest }
    }
}