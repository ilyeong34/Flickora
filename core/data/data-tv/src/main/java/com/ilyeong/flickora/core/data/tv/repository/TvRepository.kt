package com.ilyeong.flickora.core.data.tv.repository

import androidx.paging.PagingData
import com.ilyeong.flickora.core.model.TvSeries
import kotlinx.coroutines.flow.Flow

interface TvRepository {
    fun getPopularTvPaging(maxPage: Int = Int.MAX_VALUE): Flow<PagingData<TvSeries>>
}
