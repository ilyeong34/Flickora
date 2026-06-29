package com.ilyeong.flickora.core.data.media.api

import com.ilyeong.flickora.core.data.media.model.MultiSearchResponse
import com.ilyeong.flickora.core.data.media.model.TrendingMediaResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

internal interface MediaApiService {

    @GET("search/multi")
    suspend fun searchMediaList(
        @Query("query") query: String,
        @Query("page") page: Int = 1
    ): MultiSearchResponse

    @GET("trending/all/{time_window}")
    suspend fun getTrendingMediaList(
        @Path("time_window") timeWindow: String,
        @Query("page") page: Int = 1
    ): TrendingMediaResponse
}
