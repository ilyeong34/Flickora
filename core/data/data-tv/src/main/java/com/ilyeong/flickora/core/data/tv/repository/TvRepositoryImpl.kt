package com.ilyeong.flickora.core.data.tv.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.ilyeong.flickora.core.data.tv.api.TvApiService
import com.ilyeong.flickora.core.data.tv.model.toDomain
import com.ilyeong.flickora.core.data.tv.paging.AiringTodayPagingSource
import com.ilyeong.flickora.core.data.tv.paging.OnTheAirPagingSource
import com.ilyeong.flickora.core.data.tv.paging.PopularPagingSource
import com.ilyeong.flickora.core.data.tv.paging.TopRatedPagingSource
import com.ilyeong.flickora.core.data.tv.paging.TrendingPagingSource
import com.ilyeong.flickora.core.data.tv.paging.TvReviewPagingSource
import com.ilyeong.flickora.core.model.Cast
import com.ilyeong.flickora.core.model.Review
import com.ilyeong.flickora.core.model.TimeWindow
import com.ilyeong.flickora.core.model.TvSeries
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

internal class TvRepositoryImpl @Inject constructor(
    private val apiService: TvApiService
) : TvRepository {

    override fun getTvDetail(tvSeriesId: Int) = flow<TvSeries> {
        val tvDetail = apiService.getTvDetail(tvSeriesId).toDomain()
        emit(tvDetail)
    }

    override fun getTvCast(tvSeriesId: Int) = flow<List<Cast>> {
        val tvCast = apiService.getTvAggregateCredits(tvSeriesId).toDomain()
        emit(tvCast)
    }

    override fun getTvRecommendationList(tvSeriesId: Int) = flow<List<TvSeries>> {
        val tvRecommendationList = apiService.getTvRecommendationList(tvSeriesId).toDomain()
        emit(tvRecommendationList)
    }

    override fun getTvSimilarList(tvSeriesId: Int) = flow<List<TvSeries>> {
        val tvSimilarList = apiService.getTvSimilarList(tvSeriesId).toDomain()
        emit(tvSimilarList)
    }

    override fun getTvReviewPaging(tvSeriesId: Int): Flow<PagingData<Review>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { TvReviewPagingSource(apiService, tvSeriesId) }
        ).flow
    }

    override fun getPopularTvPaging(maxPage: Int): Flow<PagingData<TvSeries>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { PopularPagingSource(apiService, maxPage) }
        ).flow
    }

    override fun getTopRatedTvPaging(maxPage: Int): Flow<PagingData<TvSeries>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { TopRatedPagingSource(apiService, maxPage) }
        ).flow
    }

    override fun getTrendingTvPaging(
        timeWindow: TimeWindow,
        maxPage: Int
    ): Flow<PagingData<TvSeries>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                TrendingPagingSource(
                    apiService = apiService,
                    timeWindow = timeWindow.name.lowercase(),
                    maxPage = maxPage
                )
            }
        ).flow
    }

    override fun getOnTheAirTvPaging(maxPage: Int): Flow<PagingData<TvSeries>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { OnTheAirPagingSource(apiService, maxPage) }
        ).flow
    }

    override fun getAiringTodayTvPaging(maxPage: Int): Flow<PagingData<TvSeries>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { AiringTodayPagingSource(apiService, maxPage) }
        ).flow
    }
}
