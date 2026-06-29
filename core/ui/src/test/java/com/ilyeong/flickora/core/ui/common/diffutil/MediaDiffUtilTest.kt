package com.ilyeong.flickora.core.ui.common.diffutil

import com.ilyeong.flickora.core.model.Genre
import com.ilyeong.flickora.core.model.Movie
import com.ilyeong.flickora.core.model.TvSeries
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MediaDiffUtilTest {

    @Test
    fun areItemsTheSame_returnsTrue_forSameMediaTypeAndId() {
        assertTrue(
            MediaDiffUtil.areItemsTheSame(
                movieFixture(id = 1, title = "Before"),
                movieFixture(id = 1, title = "After")
            )
        )
    }

    @Test
    fun areItemsTheSame_returnsFalse_forDifferentMediaTypeWithSameId() {
        assertFalse(
            MediaDiffUtil.areItemsTheSame(
                movieFixture(id = 1),
                tvSeriesFixture(id = 1)
            )
        )
    }

    @Test
    fun tvSeriesTitle_usesOriginalName_whenNameIsBlank() {
        val tvSeries = tvSeriesFixture(name = "", originalName = "Breaking Bad")

        assertTrue(tvSeries.title == "Breaking Bad")
    }

    private fun movieFixture(
        id: Int,
        title: String = "Movie Title"
    ) = Movie(
        adult = false,
        collection = null,
        backdropPath = "",
        genreList = emptyList(),
        id = id,
        originalLanguage = "en",
        originalTitle = "Movie Original",
        overview = "Overview",
        popularity = 100.0,
        posterPath = "https://image.tmdb.org/t/p/original/movie.png",
        releaseDate = "2020-01-01",
        runtime = 120,
        spokenLanguageList = emptyList(),
        title = title,
        video = false,
        voteAverage = 8.5,
        voteCount = 1234,
        isInWatchlist = false
    )

    private fun tvSeriesFixture(
        id: Int = 7,
        name: String = "TV Title",
        originalName: String = "TV Original"
    ) = TvSeries(
        adult = false,
        backdropPath = "",
        genreList = listOf(Genre(1, "Drama")),
        id = id,
        originCountry = listOf("US"),
        originalLanguage = "en",
        originalName = originalName,
        overview = "Overview",
        popularity = 100.0,
        posterPath = "https://image.tmdb.org/t/p/original/tv.png",
        firstAirDate = "2008-01-20",
        name = name,
        voteAverage = 9.0,
        voteCount = 1000,
        isInWatchlist = false
    )
}
