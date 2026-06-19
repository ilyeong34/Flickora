package com.ilyeong.flickora.core.network.di

import com.ilyeong.flickora.core.network.BuildConfig
import com.ilyeong.flickora.core.network.FlickoraNetwork
import com.ilyeong.flickora.core.network.FlickoraNetworkImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
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

    /* Creating another @Provides Interceptor causes duplicates again. */
    @Provides
    @Singleton
    fun provideBaseInterceptor(): Interceptor =
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
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        baseInterceptor: Interceptor
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(baseInterceptor)
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
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(converterFactory)


    @Provides
    fun provideFlickoraNetwork(
        retrofitBuilder: Retrofit.Builder,
        okHttpClient: OkHttpClient
    ): FlickoraNetwork =
        FlickoraNetworkImpl(retrofitBuilder, okHttpClient)
}
