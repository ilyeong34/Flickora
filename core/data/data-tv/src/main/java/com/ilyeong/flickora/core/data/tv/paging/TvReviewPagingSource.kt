package com.ilyeong.flickora.core.data.tv.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ilyeong.flickora.core.data.tv.api.TvApiService
import com.ilyeong.flickora.core.data.tv.model.toDomain
import com.ilyeong.flickora.core.model.Review

internal class TvReviewPagingSource(
    private val apiService: TvApiService,
    private val tvSeriesId: Int
) : PagingSource<Int, Review>() {
    override fun getRefreshKey(state: PagingState<Int, Review>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Review> {
        val page = params.key ?: 1

        return try {
            val response = apiService.getTvReviewList(tvSeriesId, page)
            val prevPage = if (page == 1) null else page - 1
            val nextPage =
                if (response.reviewList.isEmpty() || page == response.totalPages) null else page + 1

            LoadResult.Page(
                data = response.reviewList.map { it.toDomain() },
                prevKey = prevPage,
                nextKey = nextPage
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
