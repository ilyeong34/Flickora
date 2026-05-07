package com.ilyeong.movieverse.core.data.user.di

import com.ilyeong.movieverse.core.data.user.BuildConfig
import com.ilyeong.movieverse.core.data.user.api.UserApiService
import com.ilyeong.movieverse.core.datastore.user.UserPreferenceDataSource
import com.ilyeong.movieverse.core.network.MovieverseNetwork
import com.ilyeong.movieverse.core.network.create
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.internal.Provider
import dagger.multibindings.IntoSet
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object ApiModule {

    @Provides
    @Singleton
    @IntoSet
    fun provideSessionInterceptor(
        userPreferenceDataSource: UserPreferenceDataSource
    ): Interceptor =
        Interceptor { chain ->
            val originalRequest = chain.request()
            val originalUrl = originalRequest.url

            val sessionId = runBlocking { userPreferenceDataSource.getSessionId() }

            val newUrl = originalUrl.newBuilder()
                .addQueryParameter("session_id", sessionId)
                .build()

            val newRequest = originalRequest.newBuilder()
                .url(newUrl)
                .header("accept", "application/json")
                .header("Authorization", "Bearer ${BuildConfig.TMDB_API_KEY}")
                .build()
            chain.proceed(newRequest)
        }

    @Provides
    @Singleton
    fun provideUserApiService(
        movieverseNetwork: MovieverseNetwork,
        interceptors: Provider<Set<@JvmSuppressWildcards Interceptor>>
    ): UserApiService = movieverseNetwork
        .create<UserApiService>(BuildConfig.TMDB_API_KEY, interceptors.get())
}