package com.ilyeong.movieverse.core.network.di

import com.ilyeong.movieverse.core.network.MovieverseNetwork
import com.ilyeong.movieverse.core.network.MovieverseNetworkImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

    @Provides
    @Singleton
    fun provideConverterFactory(
        json: Json
    ): Converter.Factory =
        json.asConverterFactory("application/json".toMediaType())

    @Provides
    @Singleton
    fun provideRetrofitBuilder(
        converterFactory: Converter.Factory
    ): Retrofit.Builder =
        Retrofit.Builder()
            .addConverterFactory(converterFactory)


    @Provides
    fun provideMovieverseNetwork(
        retrofitBuilder: Retrofit.Builder,
        okHttpClient: OkHttpClient
    ): MovieverseNetwork =
        MovieverseNetworkImpl(retrofitBuilder, okHttpClient)
}