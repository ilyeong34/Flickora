package com.ilyeong.flickora.core.data.tv.repository

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
import com.ilyeong.flickora.core.data.tv.model.TvEpisodeResponse
import com.ilyeong.flickora.core.data.tv.model.TvSeasonDetailResponse
import com.ilyeong.flickora.core.data.tv.model.TvSeasonSummaryResponse
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test

class TvRepositoryImplTest {

    @Test
    fun getTvDetail_fetchesAllSeasonDetails_andSortsBySeasonNumber() = runTest {
        val apiService = FakeTvApiService(
            detailResponse = tvDetailResponse(
                seasons = listOf(
                    seasonSummary(seasonNumber = 2, name = "Season 2"),
                    seasonSummary(seasonNumber = 1, name = "Season 1"),
                    seasonSummary(seasonNumber = 0, name = "Specials"),
                )
            ),
            seasonResponses = mapOf(
                0 to seasonDetail(seasonNumber = 0, name = "Specials"),
                1 to seasonDetail(seasonNumber = 1, name = "Season 1"),
                2 to seasonDetail(seasonNumber = 2, name = "Season 2"),
            )
        )
        val repository = TvRepositoryImpl(apiService)

        val tvSeries = repository.getTvDetail(tvSeriesId = 1).first()

        assertEquals(listOf(0, 1, 2), tvSeries.seasonList.map { it.seasonNumber })
        assertEquals(listOf("Specials", "Season 1", "Season 2"), tvSeries.seasonList.map { it.name })
        assertEquals(listOf(2, 1, 0), apiService.requestedSeasonNumbers)
    }

    @Test
    fun getTvDetail_returnsEmptySeasonList_whenSeasonSummaryIsEmpty() = runTest {
        val apiService = FakeTvApiService(
            detailResponse = tvDetailResponse(seasons = emptyList())
        )
        val repository = TvRepositoryImpl(apiService)

        val tvSeries = repository.getTvDetail(tvSeriesId = 1).first()

        assertEquals(emptyList<Int>(), tvSeries.seasonList.map { it.seasonNumber })
        assertEquals(emptyList<Int>(), apiService.requestedSeasonNumbers)
    }

    @Test
    fun getTvDetail_failsWholeDetail_whenAnySeasonDetailFails() = runTest {
        val apiService = FakeTvApiService(
            detailResponse = tvDetailResponse(
                seasons = listOf(
                    seasonSummary(seasonNumber = 1, name = "Season 1")
                )
            ),
            apiError = IllegalStateException("boom")
        )
        val repository = TvRepositoryImpl(apiService)

        try {
            repository.getTvDetail(tvSeriesId = 1).first()
            fail("Expected IllegalStateException")
        } catch (e: IllegalStateException) {
            assertEquals("boom", e.message)
        }
    }

    private class FakeTvApiService(
        private val detailResponse: TvDetailResponse,
        private val seasonResponses: Map<Int, TvSeasonDetailResponse> = emptyMap(),
        private val apiError: Exception? = null,
    ) : TvApiService {
        val requestedSeasonNumbers = mutableListOf<Int>()

        override suspend fun getTvDetail(tvSeriesId: Int): TvDetailResponse = detailResponse

        override suspend fun getTvSeasonDetail(
            tvSeriesId: Int,
            seasonNumber: Int
        ): TvSeasonDetailResponse {
            requestedSeasonNumbers += seasonNumber
            apiError?.let { throw it }
            return seasonResponses.getValue(seasonNumber)
        }

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

        override suspend fun getTopRatedTvList(page: Int): TopRatedResponse = error("unused")

        override suspend fun getTrendingTvList(
            timeWindow: String,
            page: Int
        ): TrendingResponse = error("unused")

        override suspend fun getOnTheAirTvList(page: Int): OnTheAirResponse = error("unused")

        override suspend fun getAiringTodayTvList(page: Int): AiringTodayResponse = error("unused")
    }

    private fun tvDetailResponse(
        seasons: List<TvSeasonSummaryResponse>
    ) = TvDetailResponse(
        id = 1,
        name = "Breaking Bad",
        originalName = "Breaking Bad",
        seasonList = seasons,
    )

    private fun seasonSummary(
        seasonNumber: Int,
        name: String
    ) = TvSeasonSummaryResponse(
        id = seasonNumber,
        name = name,
        seasonNumber = seasonNumber,
        episodeCount = 1,
    )

    private fun seasonDetail(
        seasonNumber: Int,
        name: String
    ) = TvSeasonDetailResponse(
        id = seasonNumber,
        name = name,
        seasonNumber = seasonNumber,
        episodeList = listOf(
            TvEpisodeResponse(
                id = seasonNumber,
                name = "Episode $seasonNumber",
                episodeNumber = 1,
                seasonNumber = seasonNumber,
            )
        )
    )
}
