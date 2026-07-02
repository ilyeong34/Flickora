package com.ilyeong.flickora.core.data.media.repository

import com.ilyeong.flickora.core.data.media.api.MediaApiService
import com.ilyeong.flickora.core.data.media.model.MultiSearchItemResponse
import com.ilyeong.flickora.core.data.media.model.TrendingMediaResponse
import com.ilyeong.flickora.core.model.Media
import com.ilyeong.flickora.core.model.Movie
import com.ilyeong.flickora.core.model.TimeWindow
import com.ilyeong.flickora.core.model.TvSeries
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class MediaRepositoryImplTest {

    @Test
    fun getTrendingMediaList_filtersNonMediaAndMapsMovieAndTv() = runBlocking {
        val apiService = FakeMediaApiService(
            trendingResponse = TrendingMediaResponse(
                page = 1,
                totalPages = 1,
                totalResults = 3,
                resultList = listOf(
                    movieResponse(),
                    tvResponse(),
                    personResponse()
                )
            )
        )

        val repository = MediaRepositoryImpl(apiService)

        val result = repository.getTrendingMediaList(TimeWindow.DAY).first()

        assertEquals(2, result.size)
        assertEquals(Movie::class, result[0]::class)
        assertEquals(TvSeries::class, result[1]::class)
    }

    private class FakeMediaApiService(
        private val trendingResponse: TrendingMediaResponse
    ) : MediaApiService {
        override suspend fun searchMediaList(
            query: String,
            page: Int
        ) = error("unused")

        override suspend fun getTrendingMediaList(
            timeWindow: String,
            page: Int
        ) = trendingResponse
    }

    private fun movieResponse() = MultiSearchItemResponse(
        id = 1,
        mediaType = "movie",
        title = "Movie",
        originalTitle = "Movie",
        posterPath = "/movie.png"
    )

    private fun tvResponse() = MultiSearchItemResponse(
        id = 2,
        mediaType = "tv",
        name = "Tv",
        originalName = "Tv",
        firstAirDate = "2024-01-01",
        posterPath = "/tv.png"
    )

    private fun personResponse() = MultiSearchItemResponse(
        id = 3,
        mediaType = "person",
        name = "Person"
    )
}
