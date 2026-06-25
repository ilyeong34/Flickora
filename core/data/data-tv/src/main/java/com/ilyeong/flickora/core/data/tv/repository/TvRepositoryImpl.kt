package com.ilyeong.flickora.core.data.tv.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.ilyeong.flickora.core.data.tv.api.TvApiService
import com.ilyeong.flickora.core.data.tv.model.toDomain
import com.ilyeong.flickora.core.data.tv.paging.PopularPagingSource
import com.ilyeong.flickora.core.data.tv.paging.TvReviewPagingSource
import com.ilyeong.flickora.core.model.Cast
import com.ilyeong.flickora.core.model.Review
import com.ilyeong.flickora.core.model.TvSeries
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

internal class TvRepositoryImpl @Inject constructor(
    private val apiService: TvApiService
) : TvRepository {

    override fun getTvDetail(tvSeriesId: Int) = flow<TvSeries> {
        emit(apiService.getTvDetail(tvSeriesId).toDomain())
    }

    override fun getTvCastPreview(tvSeriesId: Int) = flow<List<Cast>> {
        try {
            emit(apiService.getTvAggregateCredits(tvSeriesId).toDomain())
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            emit(emptyList())
        }
    }

    override fun getTvRecommendationList(tvSeriesId: Int) = flow<List<TvSeries>> {
        try {
            emit(apiService.getTvRecommendationList(tvSeriesId).toDomain())
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            emit(emptyList())
        }
    }

    override fun getTvSimilarList(tvSeriesId: Int) = flow<List<TvSeries>> {
        try {
            emit(apiService.getTvSimilarList(tvSeriesId).toDomain())
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            emit(emptyList())
        }
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
}
