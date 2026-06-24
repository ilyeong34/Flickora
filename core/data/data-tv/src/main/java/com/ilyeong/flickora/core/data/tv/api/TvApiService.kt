package com.ilyeong.flickora.core.data.tv.api

import com.ilyeong.flickora.core.data.tv.model.PopularResponse
import retrofit2.http.GET
import retrofit2.http.Query

internal interface TvApiService {

    @GET("tv/popular")
    suspend fun getPopularTvList(
        @Query("page") page: Int = 1
    ): PopularResponse
}
