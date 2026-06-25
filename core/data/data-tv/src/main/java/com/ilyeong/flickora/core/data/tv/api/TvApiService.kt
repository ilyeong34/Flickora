package com.ilyeong.flickora.core.data.tv.api

import com.ilyeong.flickora.core.data.tv.model.AggregateCreditsResponse
import com.ilyeong.flickora.core.data.tv.model.RecommendationListResponse
import com.ilyeong.flickora.core.data.tv.model.PopularResponse
import com.ilyeong.flickora.core.data.tv.model.ReviewListResponse
import com.ilyeong.flickora.core.data.tv.model.SimilarListResponse
import com.ilyeong.flickora.core.data.tv.model.TvDetailResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

internal interface TvApiService {

    @GET("tv/{series_id}")
    suspend fun getTvDetail(
        @Path("series_id") tvSeriesId: Int
    ): TvDetailResponse

    @GET("tv/{series_id}/recommendations")
    suspend fun getTvRecommendationList(
        @Path("series_id") tvSeriesId: Int,
        @Query("page") page: Int = 1
    ): RecommendationListResponse

    @GET("tv/{series_id}/similar")
    suspend fun getTvSimilarList(
        @Path("series_id") tvSeriesId: Int,
        @Query("page") page: Int = 1
    ): SimilarListResponse

    @GET("tv/{series_id}/aggregate_credits")
    suspend fun getTvAggregateCredits(
        @Path("series_id") tvSeriesId: Int
    ): AggregateCreditsResponse

    @GET("tv/{series_id}/reviews")
    suspend fun getTvReviewList(
        @Path("series_id") tvSeriesId: Int,
        @Query("page") page: Int = 1
    ): ReviewListResponse

    @GET("tv/popular")
    suspend fun getPopularTvList(
        @Query("page") page: Int = 1
    ): PopularResponse
}
