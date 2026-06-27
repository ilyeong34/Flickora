package com.ilyeong.flickora.core.data.tv.paging

import androidx.paging.PagingSource
import com.ilyeong.flickora.core.data.tv.api.TvApiService
import com.ilyeong.flickora.core.data.tv.model.AggregateCreditsResponse
import com.ilyeong.flickora.core.data.tv.model.AiringTodayResponse
import com.ilyeong.flickora.core.data.tv.model.OnTheAirResponse
import com.ilyeong.flickora.core.data.tv.model.PopularResponse
import com.ilyeong.flickora.core.data.tv.model.RecommendationListResponse
import com.ilyeong.flickora.core.data.tv.model.ReviewListResponse
import com.ilyeong.flickora.core.data.tv.model.SimilarListResponse
import com.ilyeong.flickora.core.data.tv.model.TopRatedResponse
import com.ilyeong.flickora.core.data.tv.model.TrendingResponse
import com.ilyeong.flickora.core.data.tv.model.TvDetailResponse
import com.ilyeong.flickora.core.data.tv.model.TvResponse
import com.ilyeong.flickora.core.data.tv.model.TvSeasonDetailResponse
import com.ilyeong.flickora.core.model.TvSeries
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TvHomePagingSourceTest {

    @Test
    fun topRated_load_mapsResponse_toDomainPage() = runTest {
        val apiService = FakeTvApiService(
            topRatedResponse = TopRatedResponse(
                page = 1,
                resultList = listOf(tvResponse(id = 11, name = "Breaking Bad")),
                totalPages = 2,
                totalResults = 1
            )
        )
        val pagingSource = TopRatedPagingSource(apiService)

        val page = pagingSource.loadRefresh() as PagingSource.LoadResult.Page

        assertEquals(11, page.data.first().id)
        assertEquals("Breaking Bad", page.data.first().name)
        assertEquals(null, page.prevKey)
        assertEquals(2, page.nextKey)
    }

    @Test
    fun trending_load_usesTimeWindow_andMapsResponse() = runTest {
        val apiService = FakeTvApiService(
            trendingResponse = TrendingResponse(
                page = 1,
                resultList = listOf(tvResponse(id = 12, name = "The Last of Us")),
                totalPages = 2,
                totalResults = 1
            )
        )
        val pagingSource = TrendingPagingSource(
            apiService = apiService,
            timeWindow = "week"
        )

        val page = pagingSource.loadRefresh() as PagingSource.LoadResult.Page

        assertEquals("week", apiService.requestedTimeWindow)
        assertEquals(12, page.data.first().id)
        assertEquals(2, page.nextKey)
    }

    @Test
    fun onTheAir_load_returnsNullNextKey_onLastPage() = runTest {
        val apiService = FakeTvApiService(
            onTheAirResponse = OnTheAirResponse(
                page = 1,
                resultList = listOf(tvResponse(id = 13, name = "Severance")),
                totalPages = 1,
                totalResults = 1
            )
        )
        val pagingSource = OnTheAirPagingSource(apiService)

        val page = pagingSource.loadRefresh() as PagingSource.LoadResult.Page

        assertEquals(13, page.data.first().id)
        assertEquals(null, page.nextKey)
    }

    @Test
    fun airingToday_load_returnsNullNextKey_whenMaxPageReached() = runTest {
        val apiService = FakeTvApiService(
            airingTodayResponse = AiringTodayResponse(
                page = 1,
                resultList = listOf(tvResponse(id = 14, name = "The Bear")),
                totalPages = 10,
                totalResults = 1
            )
        )
        val pagingSource = AiringTodayPagingSource(
            apiService = apiService,
            maxPage = 1
        )

        val page = pagingSource.loadRefresh() as PagingSource.LoadResult.Page

        assertEquals(14, page.data.first().id)
        assertEquals(null, page.nextKey)
    }

    @Test
    fun topRated_load_returnsError_whenApiFails() = runTest {
        val pagingSource = TopRatedPagingSource(
            FakeTvApiService(apiError = IllegalStateException("boom"))
        )

        val result = pagingSource.loadRefresh()

        assertTrue(result is PagingSource.LoadResult.Error)
    }

    private suspend fun PagingSource<Int, TvSeries>.loadRefresh(): PagingSource.LoadResult<Int, TvSeries> {
        return load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )
    }

    private fun tvResponse(
        id: Int,
        name: String
    ) = TvResponse(
        id = id,
        name = name,
        posterPath = "/poster.png",
        originalName = name
    )

    private class FakeTvApiService(
        private val topRatedResponse: TopRatedResponse = TopRatedResponse(
            page = 1,
            totalPages = 1,
            totalResults = 0
        ),
        private val trendingResponse: TrendingResponse = TrendingResponse(
            page = 1,
            totalPages = 1,
            totalResults = 0
        ),
        private val onTheAirResponse: OnTheAirResponse = OnTheAirResponse(
            page = 1,
            totalPages = 1,
            totalResults = 0
        ),
        private val airingTodayResponse: AiringTodayResponse = AiringTodayResponse(
            page = 1,
            totalPages = 1,
            totalResults = 0
        ),
        private val apiError: Exception? = null
    ) : TvApiService {
        var requestedTimeWindow: String? = null
            private set

        override suspend fun getTvDetail(tvSeriesId: Int): TvDetailResponse = error("unused")

        override suspend fun getTvSeasonDetail(
            tvSeriesId: Int,
            seasonNumber: Int
        ): TvSeasonDetailResponse = error("unused")

        override suspend fun getTvRecommendationList(
            tvSeriesId: Int,
            page: Int
        ): RecommendationListResponse = error("unused")

        override suspend fun getTvSimilarList(
            tvSeriesId: Int,
            page: Int
        ): SimilarListResponse = error("unused")

        override suspend fun getTvAggregateCredits(tvSeriesId: Int): AggregateCreditsResponse =
            error("unused")

        override suspend fun getTvReviewList(
            tvSeriesId: Int,
            page: Int
        ): ReviewListResponse = error("unused")

        override suspend fun getPopularTvList(page: Int): PopularResponse = error("unused")

        override suspend fun getTopRatedTvList(page: Int): TopRatedResponse {
            apiError?.let { throw it }
            return topRatedResponse
        }

        override suspend fun getTrendingTvList(
            timeWindow: String,
            page: Int
        ): TrendingResponse {
            apiError?.let { throw it }
            requestedTimeWindow = timeWindow
            return trendingResponse
        }

        override suspend fun getOnTheAirTvList(page: Int): OnTheAirResponse {
            apiError?.let { throw it }
            return onTheAirResponse
        }

        override suspend fun getAiringTodayTvList(page: Int): AiringTodayResponse {
            apiError?.let { throw it }
            return airingTodayResponse
        }
    }
}
