package com.ilyeong.flickora.core.network.di

import com.ilyeong.flickora.core.model.FlickoraError
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
import java.io.IOException
import javax.inject.Named
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

    /* Named bindings keep the base and error interceptors distinct. */
    @Provides
    @Singleton
    @Named("base")
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
    @Named("error")
    fun provideErrorInterceptor(): Interceptor =
        Interceptor { chain ->
            val response = try {
                chain.proceed(chain.request())
            } catch (_: IOException) {
                throw FlickoraError.Network()
            }

            if (response.isSuccessful) {
                response
            } else {
                val error = response.code.toFlickoraError()
                response.close()
                throw error
            }
        }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        @Named("base") baseInterceptor: Interceptor,
        @Named("error") errorInterceptor: Interceptor
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(baseInterceptor)
            .addInterceptor(errorInterceptor)
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

internal fun Int.toFlickoraError(): FlickoraError = when (this) {
    401, 403 -> FlickoraError.Authentication()
    404 -> FlickoraError.NotFound()
    in 500..599 -> FlickoraError.Server()
    else -> FlickoraError.Unknown()
}
