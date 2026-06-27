package com.ilyeong.flickora.core.ui.common.model

import com.ilyeong.flickora.core.model.Genre
import com.ilyeong.flickora.core.model.Movie
import com.ilyeong.flickora.core.model.TvSeries
import org.junit.Assert.assertEquals
import org.junit.Test

class PosterUiModelTest {

    @Test
    fun movie_toPosterUiModel_mapsDisplayFields() {
        val movie = movieFixture()

        assertEquals(
            PosterUiModel(
                id = movie.id,
                posterPath = movie.posterPath,
                title = movie.title,
                overview = movie.overview,
                voteAverage = movie.voteAverage,
                voteCount = movie.voteCount
            ),
            movie.toPosterUiModel()
        )
    }

    @Test
    fun tvSeries_toPosterUiModel_fallsBackToOriginalName() {
        val tvSeries = tvSeriesFixture(
            name = "",
            originalName = "Breaking Bad"
        )

        assertEquals(
            PosterUiModel(
                id = tvSeries.id,
                posterPath = tvSeries.posterPath,
                title = "Breaking Bad",
                overview = tvSeries.overview,
                voteAverage = tvSeries.voteAverage,
                voteCount = tvSeries.voteCount
            ),
            tvSeries.toPosterUiModel()
        )
    }

    private fun movieFixture() = Movie(
        adult = false,
        collection = null,
        backdropPath = "",
        genreList = emptyList(),
        id = 42,
        originalLanguage = "en",
        originalTitle = "Movie Original",
        overview = "Overview",
        popularity = 100.0,
        posterPath = "https://image.tmdb.org/t/p/original/movie.png",
        releaseDate = "2020-01-01",
        runtime = 120,
        spokenLanguageList = emptyList(),
        title = "Movie Title",
        video = false,
        voteAverage = 8.5,
        voteCount = 1234,
        isInWatchlist = false
    )

    private fun tvSeriesFixture(
        name: String,
        originalName: String
    ) = TvSeries(
        adult = false,
        backdropPath = "",
        genreList = listOf(Genre(1, "Drama")),
        id = 7,
        originCountry = listOf("US"),
        originalLanguage = "en",
        originalName = originalName,
        overview = "Overview",
        popularity = 100.0,
        posterPath = "https://image.tmdb.org/t/p/original/tv.png",
        firstAirDate = "2008-01-20",
        name = name,
        voteAverage = 9.0,
        voteCount = 1000
    )
}
