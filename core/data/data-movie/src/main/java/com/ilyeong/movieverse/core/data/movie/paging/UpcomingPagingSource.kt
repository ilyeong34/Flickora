package com.ilyeong.movieverse.core.data.movie.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ilyeong.movieverse.core.data.movie.api.MovieApiService
import com.ilyeong.movieverse.core.data.movie.model.toDomain
import com.ilyeong.movieverse.core.model.Movie

internal class UpcomingPagingSource(
    private val apiService: MovieApiService,
    private val maxPage: Int = Int.MAX_VALUE
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
            val response = apiService.getUpcomingMovieList(page)
            val prevPage = if (page == 1) null else page - 1
            val nextPage =
                if (response.resultList.isEmpty() || page >= response.totalPages || page >= maxPage) null else page + 1

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