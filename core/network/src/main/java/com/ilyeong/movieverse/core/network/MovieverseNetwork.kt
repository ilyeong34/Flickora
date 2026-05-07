package com.ilyeong.movieverse.core.network

import okhttp3.Interceptor

interface MovieverseNetwork {

    fun <T> create(
        service: Class<T>,
        additionalInterceptors: Set<Interceptor> = emptySet()
    ): T
}

inline fun <reified T> MovieverseNetwork.create(
    additionalInterceptors: Set<Interceptor> = emptySet()
): T {
    return create(T::class.java, additionalInterceptors)
}