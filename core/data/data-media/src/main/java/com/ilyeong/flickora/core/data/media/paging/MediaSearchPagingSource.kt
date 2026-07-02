package com.ilyeong.flickora.core.data.media.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ilyeong.flickora.core.data.media.api.MediaApiService
import com.ilyeong.flickora.core.data.media.model.toDomain
import com.ilyeong.flickora.core.model.Media

internal class MediaSearchPagingSource(
    private val apiService: MediaApiService,
    private val query: String
) : PagingSource<Int, Media>() {
    override fun getRefreshKey(state: PagingState<Int, Media>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Media> {
        val page = params.key ?: 1

        return try {
            val response = apiService.searchMediaList(query = query, page = page)
            val prevPage = if (page == 1) null else page - 1
            val mediaList = response.resultList.mapNotNull { it.toDomain() }
            val nextPage =
                if (response.resultList.isEmpty() || response.page == response.totalPages) null else page + 1

            LoadResult.Page(
                data = mediaList,
                prevKey = prevPage,
                nextKey = nextPage,
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
