package com.ilyeong.flickora.feature.detail.tvdetail

import androidx.paging.PagingData
import com.ilyeong.flickora.core.data.user.repository.UserRepository
import com.ilyeong.flickora.core.model.Account
import com.ilyeong.flickora.core.model.AccountStates
import com.ilyeong.flickora.core.data.tv.repository.TvRepository
import com.ilyeong.flickora.core.model.Cast
import com.ilyeong.flickora.core.model.Genre
import com.ilyeong.flickora.core.model.Review
import com.ilyeong.flickora.core.model.TimeWindow
import com.ilyeong.flickora.core.model.TvEpisode
import com.ilyeong.flickora.core.model.TvSeason
import com.ilyeong.flickora.core.model.TvSeries
import com.ilyeong.flickora.feature.detail.model.TvDetailUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class TvDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun loadData_selectsFirstNonSpecialSeasonByDefault() = runTest {
        val viewModel = TvDetailViewModel(
            FakeTvRepository(
                tvSeries = tvSeriesFixture(
                    seasonList = listOf(
                        tvSeasonFixture(seasonNumber = 0, name = "Specials"),
                        tvSeasonFixture(seasonNumber = 1, name = "Season 1"),
                    )
                )
            ),
            FakeUserRepository()
        )

        viewModel.loadData(tvSeriesId = 1)
        advanceUntilIdle()

        val state = viewModel.uiState.value as TvDetailUiState.Success
        assertEquals(1, state.selectedSeasonNumber)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun selectSeason_updatesOnlySelectedSeasonNumber() = runTest {
        val viewModel = TvDetailViewModel(
            FakeTvRepository(
                tvSeries = tvSeriesFixture(
                    seasonList = listOf(
                        tvSeasonFixture(seasonNumber = 1, name = "Season 1"),
                        tvSeasonFixture(seasonNumber = 2, name = "Season 2"),
                    )
                )
            ),
            FakeUserRepository()
        )

        viewModel.loadData(tvSeriesId = 1)
        advanceUntilIdle()
        viewModel.selectSeason(seasonNumber = 2)

        val state = viewModel.uiState.value as TvDetailUiState.Success
        assertEquals(2, state.selectedSeasonNumber)
        assertEquals(listOf("Season 1", "Season 2"), state.tvSeries.seasonList.map { it.name })
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun selectSeason_doesNothing_whenSameSeasonIsSelected() = runTest {
        val viewModel = TvDetailViewModel(
            FakeTvRepository(
                tvSeries = tvSeriesFixture(
                    seasonList = listOf(
                        tvSeasonFixture(seasonNumber = 1, name = "Season 1")
                    )
                )
            ),
            FakeUserRepository()
        )

        viewModel.loadData(tvSeriesId = 1)
        advanceUntilIdle()
        val before = viewModel.uiState.value

        viewModel.selectSeason(seasonNumber = 1)

        assertEquals(before, viewModel.uiState.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun addTvToWatchlist_togglesWatchlistState() = runTest {
        val userRepository = FakeUserRepository(initialWatchlist = false)
        val viewModel = TvDetailViewModel(
            FakeTvRepository(
                tvSeries = tvSeriesFixture(
                    seasonList = listOf(tvSeasonFixture(seasonNumber = 1, name = "Season 1"))
                )
            ),
            userRepository
        )

        viewModel.loadData(tvSeriesId = 1)
        advanceUntilIdle()

        val before = viewModel.uiState.value as TvDetailUiState.Success
        assertFalse(before.isInWatchlist)

        viewModel.addTvToWatchlist()
        advanceUntilIdle()

        val after = viewModel.uiState.value as TvDetailUiState.Success
        assertTrue(after.isInWatchlist)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun addTvToWatchlist_preservesLatestSeasonSelection_whenRequestCompletes() = runTest {
        val userRepository = FakeUserRepository(
            initialWatchlist = false,
            watchlistUpdateDelayMillis = 1_000
        )
        val viewModel = TvDetailViewModel(
            FakeTvRepository(
                tvSeries = tvSeriesFixture(
                    seasonList = listOf(
                        tvSeasonFixture(seasonNumber = 1, name = "Season 1"),
                        tvSeasonFixture(seasonNumber = 2, name = "Season 2"),
                    )
                )
            ),
            userRepository
        )

        viewModel.loadData(tvSeriesId = 1)
        advanceUntilIdle()

        viewModel.addTvToWatchlist()
        viewModel.selectSeason(seasonNumber = 2)
        advanceTimeBy(1_000)
        advanceUntilIdle()

        val after = viewModel.uiState.value as TvDetailUiState.Success
        assertEquals(2, after.selectedSeasonNumber)
        assertTrue(after.isInWatchlist)
    }

    private class FakeTvRepository(
        private val tvSeries: TvSeries
    ) : TvRepository {
        override fun getTvDetail(tvSeriesId: Int): Flow<TvSeries> = flowOf(tvSeries)

        override fun getTvCast(tvSeriesId: Int): Flow<List<Cast>> = flowOf(emptyList())

        override fun getTvRecommendationList(tvSeriesId: Int): Flow<List<TvSeries>> =
            flowOf(emptyList())

        override fun getTvSimilarList(tvSeriesId: Int): Flow<List<TvSeries>> =
            flowOf(emptyList())

        override fun getTvReviewPaging(tvSeriesId: Int): Flow<PagingData<Review>> =
            flowOf(PagingData.from(emptyList()))

        override fun getPopularTvPaging(maxPage: Int): Flow<PagingData<TvSeries>> =
            flowOf(PagingData.from(emptyList()))

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

    private class FakeUserRepository(
        private val initialWatchlist: Boolean = false,
        private val watchlistUpdateDelayMillis: Long = 0L
    ) : UserRepository {
        override fun getAccount(): Flow<Account> = unusedFlow()

        override fun getWatchlistMoviePaging(): Flow<PagingData<com.ilyeong.flickora.core.model.Movie>> =
            flowOf(PagingData.from(emptyList()))

        override fun getWatchlistTvPaging(): Flow<PagingData<TvSeries>> =
            flowOf(PagingData.from(emptyList()))

        override fun getMovieAccountStates(movieId: Int): Flow<AccountStates> = unusedFlow()

        override fun getTvAccountStates(tvSeriesId: Int): Flow<AccountStates> = flowOf(
            AccountStates(
                id = tvSeriesId,
                favorite = false,
                rated = null,
                watchlist = initialWatchlist
            )
        )

        override fun addMovieToWatchlist(
            movie: com.ilyeong.flickora.core.model.Movie,
            watchlist: Boolean
        ): Flow<Unit> = unusedFlow()

        override fun addTvToWatchlist(tvSeries: TvSeries, watchlist: Boolean): Flow<Unit> =
            kotlinx.coroutines.flow.flow {
                if (watchlistUpdateDelayMillis > 0) {
                    delay(watchlistUpdateDelayMillis)
                }
                emit(Unit)
            }
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

private fun tvSeriesFixture(
    seasonList: List<TvSeason>
) = TvSeries(
    adult = false,
    backdropPath = "",
    genreList = listOf(Genre(1, "Drama")),
    id = 1,
    originCountry = listOf("US"),
    originalLanguage = "en",
    originalName = "Breaking Bad",
    overview = "Overview",
    popularity = 100.0,
    posterPath = "",
    firstAirDate = "2008-01-20",
    name = "Breaking Bad",
    voteAverage = 9.0,
    voteCount = 1000,
    seasonList = seasonList,
)

private fun tvSeasonFixture(
    seasonNumber: Int,
    name: String
) = TvSeason(
    id = seasonNumber,
    name = name,
    overview = "",
    seasonNumber = seasonNumber,
    episodeCount = 1,
    airDate = "",
    posterPath = "",
    voteAverage = 0.0,
    episodeList = listOf(
        TvEpisode(
            id = seasonNumber,
            name = "Episode $seasonNumber",
            overview = "",
            seasonNumber = seasonNumber,
            episodeNumber = 1,
            airDate = "",
            runtime = 0,
            stillPath = "",
            voteAverage = 0.0,
        )
    ),
)

private fun <T> unusedFlow(): Flow<T> = emptyFlow()
