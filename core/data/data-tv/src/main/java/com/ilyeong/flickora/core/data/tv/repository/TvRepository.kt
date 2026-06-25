package com.ilyeong.flickora.core.data.tv.repository

import androidx.paging.PagingData
import com.ilyeong.flickora.core.model.Cast
import com.ilyeong.flickora.core.model.Review
import com.ilyeong.flickora.core.model.TvSeries
import kotlinx.coroutines.flow.Flow

interface TvRepository {
    fun getTvDetail(tvSeriesId: Int): Flow<TvSeries>
    fun getTvCast(tvSeriesId: Int): Flow<List<Cast>>
    fun getTvRecommendationList(tvSeriesId: Int): Flow<List<TvSeries>>
    fun getTvSimilarList(tvSeriesId: Int): Flow<List<TvSeries>>
    fun getTvReviewPaging(tvSeriesId: Int): Flow<PagingData<Review>>
    fun getPopularTvPaging(maxPage: Int = Int.MAX_VALUE): Flow<PagingData<TvSeries>>
}
