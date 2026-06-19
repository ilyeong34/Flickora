package com.ilyeong.flickora.core.data.user.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ilyeong.flickora.core.data.user.api.UserApiService
import com.ilyeong.flickora.core.data.user.model.toDomain
import com.ilyeong.flickora.core.model.Movie

internal class WatchlistPagingSource(
    private val apiService: UserApiService
) : PagingSource<Int, Movie>() {
    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        val page = params.key ?: 1

        return try {
            val response = apiService.getWatchlist(page)
            val prevPage = if (page == 1) null else page - 1
            val nextPage =
                if (response.resultList.isEmpty() || response.totalPage == page) null else page + 1

            LoadResult.Page(
                data = response.resultList.map { it.toDomain() },
                prevKey = prevPage,
                nextKey = nextPage
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}