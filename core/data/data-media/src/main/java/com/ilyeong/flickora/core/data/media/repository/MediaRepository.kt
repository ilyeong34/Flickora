package com.ilyeong.flickora.core.data.media.repository

import androidx.paging.PagingData
import com.ilyeong.flickora.core.model.Media
import com.ilyeong.flickora.core.model.TimeWindow
import kotlinx.coroutines.flow.Flow

interface MediaRepository {

    fun searchMediaPaging(query: String): Flow<PagingData<Media>>
    fun getTrendingMediaList(timeWindow: TimeWindow): Flow<List<Media>>
}
