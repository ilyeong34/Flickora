package com.ilyeong.movieverse.core.network

import okhttp3.Interceptor

interface MovieverseNetwork {

    fun <T> create(
        baseUrl: String,
        service: Class<T>,
        interceptors: Set<Interceptor> = emptySet()
    ): T
}

inline fun <reified T> MovieverseNetwork.create(
    baseUrl: String,
    interceptors: Set<Interceptor> = emptySet()
): T {
    return create(baseUrl, T::class.java, interceptors)
}