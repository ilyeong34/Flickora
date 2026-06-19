package com.ilyeong.flickora.core.data.user.di

import com.ilyeong.flickora.core.data.user.api.UserApiService
import com.ilyeong.flickora.core.datastore.user.UserPreferenceDataSource
import com.ilyeong.flickora.core.network.FlickoraNetwork
import com.ilyeong.flickora.core.network.create
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
        flickoraNetwork: FlickoraNetwork,
        userPreferenceDataSource: UserPreferenceDataSource
    ): UserApiService = flickoraNetwork
        .create<UserApiService>(
            additionalInterceptors = setOf(
                createSessionInterceptor(
                    userPreferenceDataSource
                )
            )
        )
}
