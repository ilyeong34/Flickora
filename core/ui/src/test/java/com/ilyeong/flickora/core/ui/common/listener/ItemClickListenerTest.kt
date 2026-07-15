package com.ilyeong.flickora.core.ui.common.listener

import com.ilyeong.flickora.core.model.Genre
import com.ilyeong.flickora.core.model.Media
import com.ilyeong.flickora.core.model.Movie
import com.ilyeong.flickora.core.model.TvSeries
import org.junit.Assert.assertSame
import org.junit.Test

class ItemClickListenerTest {

    @Test
    fun onItemClick_passesSameMovieInstance() {
        val movie = movieFixture()
        var clickedMedia: Media? = null
        val listener = ItemClickListener<Media> { media -> clickedMedia = media }

        listener.onItemClick(movie)

        assertSame(movie, clickedMedia)
    }

    @Test
    fun onItemClick_passesSameTvSeriesInstance() {
        val tvSeries = tvSeriesFixture()
        var clickedMedia: Media? = null
        val listener = ItemClickListener<Media> { media -> clickedMedia = media }

        listener.onItemClick(tvSeries)

        assertSame(tvSeries, clickedMedia)
    }

    private fun movieFixture() = Movie(
        adult = false,
        collection = null,
        backdropPath = "",
        genreList = emptyList(),
        id = 1,
        originalLanguage = "en",
        originalTitle = "Movie Original",
        overview = "Overview",
        popularity = 100.0,
        posterPath = "movie.png",
        releaseDate = "2020-01-01",
        runtime = 120,
        spokenLanguageList = emptyList(),
        title = "Movie Title",
        video = false,
        voteAverage = 8.5,
        voteCount = 1234,
        isInWatchlist = false
    )

    private fun tvSeriesFixture() = TvSeries(
        adult = false,
        backdropPath = "",
        genreList = listOf(Genre(1, "Drama")),
        id = 2,
        originCountry = listOf("US"),
        originalLanguage = "en",
        originalName = "TV Original",
        overview = "Overview",
        popularity = 100.0,
        posterPath = "tv.png",
        firstAirDate = "2008-01-20",
        name = "TV Title",
        voteAverage = 9.0,
        voteCount = 1000,
        isInWatchlist = false
    )
}
