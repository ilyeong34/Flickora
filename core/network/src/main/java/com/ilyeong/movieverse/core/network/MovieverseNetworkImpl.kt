package com.ilyeong.movieverse.core.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit

internal class MovieverseNetworkImpl(
    private val retrofit: Retrofit.Builder,
    private val baseOkHttpClient: OkHttpClient
) : MovieverseNetwork {

    override fun <T> create(
        baseUrl: String,
        service: Class<T>,
        interceptors: List<Interceptor>
    ): T {
        val client = baseOkHttpClient.newBuilder()
            .apply {
                interceptors.forEach { addInterceptor(it) }
            }
            .build()

        return retrofit
            .baseUrl(baseUrl)
            .client(client)
            .build()
            .create(service)
    }
}