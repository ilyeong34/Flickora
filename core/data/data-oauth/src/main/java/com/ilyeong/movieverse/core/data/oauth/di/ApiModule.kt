package com.ilyeong.movieverse.core.data.oauth.di

import com.ilyeong.movieverse.core.data.oauth.BuildConfig
import com.ilyeong.movieverse.core.data.oauth.api.OAuthApiService
import com.ilyeong.movieverse.core.network.MovieverseNetwork
import com.ilyeong.movieverse.core.network.create
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import okhttp3.Interceptor
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object ApiModule {

    @Provides
    @Singleton
    @IntoSet
    fun provideInterceptor(): Interceptor =
        Interceptor { chain ->
            val originalRequest = chain.request()
            val newRequest = originalRequest.newBuilder()
                .header("accept", "application/json")
                .header("Authorization", "Bearer ${BuildConfig.TMDB_API_KEY}")
                .build()
            chain.proceed(newRequest)
        }

    @Provides
    @Singleton
    fun provideOAuthApiService(
        movieverseNetwork: MovieverseNetwork,
        interceptors: Provider<Set<@JvmSuppressWildcards Interceptor>>
    ): OAuthApiService = movieverseNetwork
        .create<OAuthApiService>(
            baseUrl = "https://api.themoviedb.org/3/",
            interceptors = interceptors.get()
        )

}
