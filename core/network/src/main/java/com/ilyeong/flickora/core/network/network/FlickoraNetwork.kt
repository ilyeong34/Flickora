package com.ilyeong.flickora.core.network

import okhttp3.Interceptor

interface FlickoraNetwork {

    fun <T> create(
        service: Class<T>,
        additionalInterceptors: Set<Interceptor> = emptySet()
    ): T
}

inline fun <reified T> FlickoraNetwork.create(
    additionalInterceptors: Set<Interceptor> = emptySet()
): T {
    return create(T::class.java, additionalInterceptors)
}