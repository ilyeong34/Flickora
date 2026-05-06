package com.ilyeong.movieverse.core.network

import okhttp3.Interceptor

interface MovieverseNetwork {

    fun <T> create(baseUrl: String, service: Class<T>, interceptors: List<Interceptor>): T
}

inline fun <reified T> MovieverseNetwork.create(
    baseUrl: String,
    interceptors: List<Interceptor> = emptyList()
): T {
    return create(baseUrl, T::class.java, interceptors)
}