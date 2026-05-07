package com.ilyeong.movieverse.core.data.user.di

import com.ilyeong.movieverse.core.data.user.api.UserApiService
import com.ilyeong.movieverse.core.datastore.user.UserPreferenceDataSource
import com.ilyeong.movieverse.core.network.MovieverseNetwork
import com.ilyeong.movieverse.core.network.create
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object ApiModule {

    private fun createSessionInterceptor(
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
                .build()
            chain.proceed(newRequest)
        }

    @Provides
    @Singleton
    fun provideUserApiService(
        movieverseNetwork: MovieverseNetwork,
        userPreferenceDataSource: UserPreferenceDataSource
    ): UserApiService = movieverseNetwork
        .create<UserApiService>(
            additionalInterceptors = setOf(
                createSessionInterceptor(
                    userPreferenceDataSource
                )
            )
        )
}
