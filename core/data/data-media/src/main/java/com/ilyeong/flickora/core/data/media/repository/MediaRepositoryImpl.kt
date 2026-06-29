package com.ilyeong.flickora.core.data.media.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.ilyeong.flickora.core.data.media.api.MediaApiService
import com.ilyeong.flickora.core.data.media.model.toDomain
import com.ilyeong.flickora.core.data.media.paging.MediaSearchPagingSource
import com.ilyeong.flickora.core.model.Media
import com.ilyeong.flickora.core.model.TimeWindow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class MediaRepositoryImpl @Inject constructor(
    private val apiService: MediaApiService
) : MediaRepository {

    override fun searchMediaPaging(query: String): Flow<PagingData<Media>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { MediaSearchPagingSource(apiService, query) }
        ).flow
    }

    override fun getTrendingMediaList(timeWindow: TimeWindow): Flow<List<Media>> = flow {
        val trendingMediaList = apiService.getTrendingMediaList(timeWindow.name.lowercase())
            .resultList
            .mapNotNull { it.toDomain() }
        emit(trendingMediaList)
    }
}
