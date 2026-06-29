package com.ilyeong.flickora.feature.home

import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.ilyeong.flickora.core.data.media.repository.MediaRepository
import com.ilyeong.flickora.core.data.movie.repository.MovieRepository
import com.ilyeong.flickora.core.data.tv.repository.TvRepository
import com.ilyeong.flickora.core.data.user.repository.UserRepository
import com.ilyeong.flickora.core.model.Account
import com.ilyeong.flickora.core.model.AccountStates
import com.ilyeong.flickora.core.model.Credit
import com.ilyeong.flickora.core.model.Genre
import com.ilyeong.flickora.core.model.Media
import com.ilyeong.flickora.core.model.Movie
import com.ilyeong.flickora.core.model.Review
import com.ilyeong.flickora.core.model.TimeWindow
import com.ilyeong.flickora.core.model.TvSeries
import com.ilyeong.flickora.core.ui.common.diffutil.MediaDiffUtil
import com.ilyeong.flickora.feature.home.model.HomeUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun loadData_emitsSuccess_withRankingListLimitedTo10() = runTest {
        val bannerMovie = movieFixture()
        val bannerTv = tvSeriesFixture()
        val rankingFixtures = rankingFixtures()
        val viewModel = HomeViewModel(
            mediaRepository = FakeMediaRepository(
                dayList = listOf(bannerMovie, bannerTv),
                weekList = rankingFixtures
            ),
            movieRepository = FakeMovieRepository(),
            tvRepository = FakeTvRepository(bannerTv),
            userRepository = FakeUserRepository()
        )

        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is HomeUiState.Success)
        val success = viewModel.uiState.value as HomeUiState.Success
        assertEquals(2, success.bannerMediaList.size)
        assertEquals(bannerMovie as Media, success.bannerMediaList.first())
        assertEquals(bannerTv as Media, success.bannerMediaList.last())
        assertEquals(10, success.rankingMediaList.size)
        assertEquals(rankingFixtures.take(10), success.rankingMediaList)

        val differ = AsyncPagingDataDiffer(
            diffCallback = MediaDiffUtil,
            updateCallback = NoopListUpdateCallback,
            mainDispatcher = Dispatchers.Main,
            workerDispatcher = UnconfinedTestDispatcher(testScheduler)
        )

        differ.submitData(viewModel.popularTvPaging.first())
        advanceUntilIdle()

        assertEquals(bannerTv as Media, differ.snapshot().items.single())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun loadData_keepsSuccess_whenRankingListIsEmpty() = runTest {
        val bannerMovie = movieFixture()
        val bannerTv = tvSeriesFixture()
        val viewModel = HomeViewModel(
            mediaRepository = FakeMediaRepository(
                dayList = listOf(bannerMovie, bannerTv),
                weekList = emptyList()
            ),
            movieRepository = FakeMovieRepository(),
            tvRepository = FakeTvRepository(bannerTv),
            userRepository = FakeUserRepository()
        )

        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is HomeUiState.Success)
        val success = viewModel.uiState.value as HomeUiState.Success
        assertTrue(success.rankingMediaList.isEmpty())
    }

    private class FakeMediaRepository(
        private val dayList: List<Media>,
        private val weekList: List<Media>
    ) : MediaRepository {
        override fun searchMediaPaging(query: String): Flow<PagingData<Media>> =
            flowOf(PagingData.from(emptyList()))

        override fun getTrendingMediaList(timeWindow: TimeWindow): Flow<List<Media>> =
            flowOf(
                when (timeWindow) {
                    TimeWindow.DAY -> dayList
                    TimeWindow.WEEK -> weekList
                }
            )
    }

    private class FakeMovieRepository : MovieRepository {
        override fun getMovieDetail(movieId: Int): Flow<Movie> = unusedFlow()
        override fun getMovieCredit(movieId: Int): Flow<Credit> = unusedFlow()
        override fun getMovieRecommendationList(movieId: Int): Flow<List<Movie>> = flowOf(emptyList())
        override fun getMovieSimilarList(movieId: Int): Flow<List<Movie>> = flowOf(emptyList())
        override fun getMovieReviewPaging(movieId: Int): Flow<PagingData<Review>> =
            flowOf(PagingData.from(emptyList()))

        override fun getMovieListByGenrePaging(genreId: Int): Flow<PagingData<Movie>> =
            flowOf(PagingData.from(emptyList()))

        override fun getTopRatedMoviePaging(maxPage: Int): Flow<PagingData<Movie>> =
            flowOf(PagingData.from(emptyList()))

        override fun getUpcomingMoviePaging(maxPage: Int): Flow<PagingData<Movie>> =
            flowOf(PagingData.from(emptyList()))

        override fun getPopularMoviePaging(maxPage: Int): Flow<PagingData<Movie>> =
            flowOf(PagingData.from(emptyList()))

        override fun getNowPlayingMoviePaging(maxPage: Int): Flow<PagingData<Movie>> =
            flowOf(PagingData.from(emptyList()))

        override fun getTrendingMovieList(timeWindow: TimeWindow): Flow<List<Movie>> = flowOf(
            listOf(
                Movie(
                    adult = false,
                    collection = null,
                    backdropPath = "",
                    genreList = emptyList(),
                    id = 1,
                    originalLanguage = "en",
                    originalTitle = "Trending Movie",
                    overview = "",
                    popularity = 1.0,
                    posterPath = "",
                    releaseDate = "",
                    runtime = 0,
                    spokenLanguageList = emptyList(),
                    title = "Trending Movie",
                    video = false,
                    voteAverage = 0.0,
                    voteCount = 0,
                    isInWatchlist = false
                )
            )
        )

        override fun getTrendingMoviePaging(
            timeWindow: TimeWindow,
            maxPage: Int
        ): Flow<PagingData<Movie>> = flowOf(PagingData.from(emptyList()))

        override fun getMovieGenreList(): Flow<List<Genre>> = flowOf(
            listOf(Genre(1, "Drama"))
        )
    }

    private class FakeTvRepository(
        private val tvFixture: TvSeries
    ) : TvRepository {
        override fun getTvDetail(tvSeriesId: Int): Flow<TvSeries> = unusedFlow()

        override fun getTvCast(tvSeriesId: Int): Flow<List<com.ilyeong.flickora.core.model.Cast>> =
            flowOf(emptyList())

        override fun getTvRecommendationList(tvSeriesId: Int): Flow<List<TvSeries>> =
            flowOf(emptyList())

        override fun getTvSimilarList(tvSeriesId: Int): Flow<List<TvSeries>> =
            flowOf(emptyList())

        override fun getTvReviewPaging(tvSeriesId: Int): Flow<PagingData<Review>> =
            flowOf(PagingData.from(emptyList()))

        override fun getPopularTvPaging(maxPage: Int): Flow<PagingData<TvSeries>> =
            flowOf(PagingData.from(listOf(tvFixture)))

        override fun getTopRatedTvPaging(maxPage: Int): Flow<PagingData<TvSeries>> =
            flowOf(PagingData.from(emptyList()))

        override fun getTrendingTvPaging(
            timeWindow: TimeWindow,
            maxPage: Int
        ): Flow<PagingData<TvSeries>> = flowOf(PagingData.from(emptyList()))

        override fun getOnTheAirTvPaging(maxPage: Int): Flow<PagingData<TvSeries>> =
            flowOf(PagingData.from(emptyList()))

        override fun getAiringTodayTvPaging(maxPage: Int): Flow<PagingData<TvSeries>> =
            flowOf(PagingData.from(emptyList()))
    }

    private class FakeUserRepository : UserRepository {
        override fun getAccount(): Flow<Account> = unusedFlow()
        override fun getWatchlistMoviePaging(): Flow<PagingData<Movie>> =
            flowOf(PagingData.from(emptyList()))
        override fun getWatchlistTvPaging(): Flow<PagingData<TvSeries>> =
            flowOf(PagingData.from(emptyList()))

        override fun getMovieAccountStates(movieId: Int): Flow<AccountStates> = unusedFlow()
        override fun getTvAccountStates(tvSeriesId: Int): Flow<AccountStates> = unusedFlow()
        override fun addMovieToWatchlist(movie: Movie, watchlist: Boolean): Flow<Unit> = unusedFlow()
        override fun addTvToWatchlist(tvSeries: TvSeries, watchlist: Boolean): Flow<Unit> =
            unusedFlow()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    class MainDispatcherRule(
        private val dispatcher: TestDispatcher = UnconfinedTestDispatcher()
    ) : TestWatcher() {

        override fun starting(description: Description) {
            Dispatchers.setMain(dispatcher)
        }

        override fun finished(description: Description) {
            Dispatchers.resetMain()
        }
    }
}

private object NoopListUpdateCallback : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) = Unit
    override fun onRemoved(position: Int, count: Int) = Unit
    override fun onMoved(fromPosition: Int, toPosition: Int) = Unit
    override fun onChanged(position: Int, count: Int, payload: Any?) = Unit
}

private fun tvSeriesFixture() = TvSeries(
    adult = false,
    backdropPath = "",
    genreList = listOf(Genre(1, "Drama")),
    id = 7,
    originCountry = listOf("US"),
    originalLanguage = "en",
    originalName = "Breaking Bad",
    overview = "Overview",
    popularity = 100.0,
    posterPath = "https://image.tmdb.org/t/p/original//poster.png",
    firstAirDate = "2008-01-20",
    name = "Breaking Bad",
    voteAverage = 9.0,
    voteCount = 1000
)

private fun rankingFixtures(): List<Media> = buildList {
    add(movieFixture())
    add(tvSeriesFixture())
    add(
        movieFixture().copy(
            id = 12,
            originalTitle = "Ranking Movie 2",
            title = "Ranking Movie 2"
        )
    )
    add(
        tvSeriesFixture().copy(
            id = 13,
            originalName = "Ranking TV 2",
            name = "Ranking TV 2"
        )
    )
    add(
        movieFixture().copy(
            id = 14,
            originalTitle = "Ranking Movie 3",
            title = "Ranking Movie 3"
        )
    )
    add(
        tvSeriesFixture().copy(
            id = 15,
            originalName = "Ranking TV 3",
            name = "Ranking TV 3"
        )
    )
    add(
        movieFixture().copy(
            id = 16,
            originalTitle = "Ranking Movie 4",
            title = "Ranking Movie 4"
        )
    )
    add(
        tvSeriesFixture().copy(
            id = 17,
            originalName = "Ranking TV 4",
            name = "Ranking TV 4"
        )
    )
    add(
        movieFixture().copy(
            id = 18,
            originalTitle = "Ranking Movie 5",
            title = "Ranking Movie 5"
        )
    )
    add(
        tvSeriesFixture().copy(
            id = 19,
            originalName = "Ranking TV 5",
            name = "Ranking TV 5"
        )
    )
    add(
        movieFixture().copy(
            id = 20,
            originalTitle = "Ranking Movie 6",
            title = "Ranking Movie 6"
        )
    )
    add(
        tvSeriesFixture().copy(
            id = 21,
            originalName = "Ranking TV 6",
            name = "Ranking TV 6"
        )
    )
}

private fun movieFixture() = Movie(
    adult = false,
    collection = null,
    backdropPath = "",
    genreList = listOf(Genre(1, "Drama")),
    id = 11,
    originalLanguage = "en",
    originalTitle = "Trending Movie",
    overview = "Overview",
    popularity = 100.0,
    posterPath = "https://image.tmdb.org/t/p/original//poster.png",
    releaseDate = "2024-01-01",
    runtime = 120,
    spokenLanguageList = emptyList(),
    title = "Trending Movie",
    video = false,
    voteAverage = 8.5,
    voteCount = 1234,
    isInWatchlist = false
)

private fun <T> unusedFlow(): Flow<T> = flow {
    error("unused")
}
