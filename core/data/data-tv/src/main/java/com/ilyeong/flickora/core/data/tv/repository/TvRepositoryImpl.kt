package com.ilyeong.flickora.core.data.tv.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.ilyeong.flickora.core.data.tv.api.TvApiService
import com.ilyeong.flickora.core.data.tv.paging.PopularPagingSource
import com.ilyeong.flickora.core.model.TvSeries
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class TvRepositoryImpl @Inject constructor(
    private val apiService: TvApiService
) : TvRepository {

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
