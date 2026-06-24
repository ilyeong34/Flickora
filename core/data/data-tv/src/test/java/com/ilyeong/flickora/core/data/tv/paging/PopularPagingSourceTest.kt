package com.ilyeong.flickora.core.data.tv.paging

import androidx.paging.PagingSource
import com.ilyeong.flickora.core.data.tv.api.TvApiService
import com.ilyeong.flickora.core.data.tv.model.PopularResponse
import com.ilyeong.flickora.core.data.tv.model.TvResponse
import org.junit.Assert.assertEquals
import org.junit.Test

class PopularPagingSourceTest {

    @Test
    fun load_mapsPopularTvResponse_toDomainPage() = kotlinx.coroutines.test.runTest {
        val apiService = FakeTvApiService(
            response = PopularResponse(
                page = 1,
                resultList = listOf(
                    TvResponse(
                        id = 10,
                        name = "The Bear",
                        posterPath = "/poster.png",
                        originalName = "The Bear"
                    )
                ),
                totalPages = 1,
                totalResults = 1
            )
        )
        val pagingSource = PopularPagingSource(apiService)

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )

        val page = result as PagingSource.LoadResult.Page
        assertEquals(10, page.data.first().id)
        assertEquals("The Bear", page.data.first().name)
        assertEquals("https://image.tmdb.org/t/p/original//poster.png", page.data.first().posterPath)
        assertEquals(null, page.prevKey)
        assertEquals(null, page.nextKey)
    }

    private class FakeTvApiService(
        private val response: PopularResponse
    ) : TvApiService {
        override suspend fun getPopularTvList(page: Int): PopularResponse = response
    }
}
