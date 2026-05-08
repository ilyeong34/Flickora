package com.ilyeong.movieverse.core.datastore.user.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DataStoreModule {

    private const val USER_PREFERENCES = "user_preferences"

    private val Context.userDataStore by preferencesDataStore(USER_PREFERENCES)

    @Provides
    @Singleton
    @Named("user")
    fun provideUserPreferenceDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> =
        context.userDataStore
}