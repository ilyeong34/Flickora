package com.ilyeong.flickora.core.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit

internal class FlickoraNetworkImpl(
    private val retrofit: Retrofit.Builder,
    private val baseOkHttpClient: OkHttpClient
) : FlickoraNetwork {

    override fun <T> create(
        service: Class<T>,
        additionalInterceptors: Set<Interceptor>
    ): T {
        val client = baseOkHttpClient.newBuilder()
            .apply {
                additionalInterceptors.forEach { addInterceptor(it) }
            }
            .build()

        return retrofit
            .client(client)
            .build()
            .create(service)
    }
}
