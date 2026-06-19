package com.ilyeong.flickora.core.data.movie.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ilyeong.flickora.core.data.movie.api.MovieApiService
import com.ilyeong.flickora.core.data.movie.model.toDomain
import com.ilyeong.flickora.core.model.Movie

internal class GenreMoviePagingSource(
    private val movieApiService: MovieApiService,
    private val genreId: Int,
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
            val response = movieApiService.getMovieListByGenre(genreId, page)
            val prevPage = if (page == 1) null else page - 1
            val nextPage =
                if (response.discoverMovieList.isEmpty() || response.page == response.totalPages) null else page + 1

            LoadResult.Page(
                data = response.discoverMovieList.map { it.toDomain() },
                prevKey = prevPage,
                nextKey = nextPage
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

}