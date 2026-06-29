package com.ilyeong.flickora.core.data.media.api

import com.ilyeong.flickora.core.data.media.model.MultiSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

internal interface MediaApiService {

    @GET("search/multi")
    suspend fun searchMediaList(
        @Query("query") query: String,
        @Query("page") page: Int = 1
    ): MultiSearchResponse
}
