package com.ilyeong.movieverse.core.data.user.repository

import androidx.paging.PagingData
import com.ilyeong.movieverse.core.data.user.datasource.UserLocalDataSource
import com.ilyeong.movieverse.core.data.user.datasource.UserRemoteDataSource
import com.ilyeong.movieverse.core.data.user.model.WatchlistPostRequest
import com.ilyeong.movieverse.core.datastore.user.UserPreferenceDataSource
import com.ilyeong.movieverse.core.model.Account
import com.ilyeong.movieverse.core.model.AccountStates
import com.ilyeong.movieverse.core.model.Movie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UserRepositoryImplTest {

    @Test
    fun guest_addMovieToWatchlist_updatesLocalStore_andPreservesInsertOrder() = runBlocking {
        val prefs = FakeUserPreferenceDataSource(isGuest = true)
        val local = FakeUserLocalDataSource()
        val remote = FakeUserRemoteDataSource()
        val repository = UserRepositoryImpl(prefs, local, remote)

        val firstAdded = movie(5, "first", "first overview")
        val secondAdded = movie(2, "second", "second overview")

        repository.addMovieToWatchlist(firstAdded, true).first()
        repository.addMovieToWatchlist(secondAdded, true).first()
        repository.addMovieToWatchlist(firstAdded.copy(title = "first-updated"), true).first()

        assertTrue(local.isInWatchlist(5))
        assertTrue(local.isInWatchlist(2))
        assertEquals(listOf(5, 2), local.watchlistIds())
        assertEquals("first-updated", local.movie(5)?.title)
    }

    @Test
    fun guest_removeMovieFromWatchlist_updatesLocalStore() = runBlocking {
        val prefs = FakeUserPreferenceDataSource(isGuest = true)
        val local = FakeUserLocalDataSource()
        val remote = FakeUserRemoteDataSource()
        val repository = UserRepositoryImpl(prefs, local, remote)

        val movie = movie(1, "first", "first overview")
        repository.addMovieToWatchlist(movie, true).first()
        repository.addMovieToWatchlist(movie, false).first()

        assertFalse(local.isInWatchlist(1))
        assertEquals(emptyList<Int>(), local.watchlistIds())
    }

    @Test
    fun guest_movieAccountStates_reflectsLocalWatchlist() = runBlocking {
        val prefs = FakeUserPreferenceDataSource(isGuest = true)
        val local = FakeUserLocalDataSource()
        val remote = FakeUserRemoteDataSource()
        val repository = UserRepositoryImpl(prefs, local, remote)

        val movie = movie(7, "guest", "guest overview")
        repository.addMovieToWatchlist(movie, true).first()

        val states = repository.getMovieAccountStates(7).first()

        assertEquals(
            AccountStates(id = 7, favorite = false, rated = null, watchlist = true),
            states
        )
    }

    @Test
    fun nonGuest_addMovieToWatchlist_usesApi() = runBlocking {
        val prefs = FakeUserPreferenceDataSource(isGuest = false)
        val local = FakeUserLocalDataSource()
        val remote = FakeUserRemoteDataSource()
        val repository = UserRepositoryImpl(prefs, local, remote)

        repository.addMovieToWatchlist(movie(9, "remote", "remote overview"), true).first()

        assertEquals(1, remote.addMovieCalls.size)
        assertEquals(9, remote.addMovieCalls.single().mediaId)
        assertEquals(0, local.watchlistIds().size)
    }

    @Test
    fun guest_getAccount_usesLocalDatasource() = runBlocking {
        val prefs = FakeUserPreferenceDataSource(isGuest = true)
        val local = FakeUserLocalDataSource()
        val remote = FakeUserRemoteDataSource()
        val repository = UserRepositoryImpl(prefs, local, remote)

        val account = repository.getAccount().first()

        assertEquals("Guest", account.name)
        assertEquals(0, remote.getAccountCalls)
    }

    @Test
    fun guest_then_login_getAccount_switchesToRemoteDatasource() = runBlocking {
        val prefs = FakeUserPreferenceDataSource(isGuest = true)
        val local = FakeUserLocalDataSource()
        val remote = FakeUserRemoteDataSource()
        val repository = UserRepositoryImpl(prefs, local, remote)

        val guestAccount = repository.getAccount().first()
        prefs.saveGuestMode(false)
        prefs.saveSessionId("session-123")
        val remoteAccount = repository.getAccount().first()

        assertEquals("Guest", guestAccount.name)
        assertEquals("Remote", remoteAccount.name)
        assertEquals(1, remote.getAccountCalls)
    }

    private fun movie(id: Int, title: String, overview: String): Movie = Movie(
        adult = false,
        collection = null,
        backdropPath = "",
        genreList = emptyList(),
        id = id,
        originalLanguage = "",
        originalTitle = title,
        overview = overview,
        popularity = 0.0,
        posterPath = "poster-$id",
        releaseDate = "",
        runtime = 0,
        spokenLanguageList = emptyList(),
        title = title,
        video = false,
        voteAverage = 0.0,
        voteCount = 0,
        isInWatchlist = false,
    )

    private class FakeUserPreferenceDataSource(
        private val isGuest: Boolean
    ) : UserPreferenceDataSource {
        var currentSessionId: String = ""
        var currentIsGuestMode: Boolean = isGuest

        override suspend fun getSessionId(): String = currentSessionId
        override suspend fun saveSessionId(sessionId: String) {
            currentSessionId = sessionId
        }

        override suspend fun isGuestMode(): Boolean = currentIsGuestMode

        override suspend fun saveGuestMode(isGuest: Boolean) {
            currentIsGuestMode = isGuest
        }
    }

    private class FakeUserLocalDataSource : UserLocalDataSource {
        private val items = linkedMapOf<Int, Movie>()

        override fun getAccount(): Flow<Account> =
            flowOf(Account(id = 0, avatarPath = "", name = "Guest", username = "Guest"))

        override fun getWatchlistMoviePaging(): Flow<PagingData<Movie>> =
            flowOf(PagingData.from(items.values.toList()))

        override fun getMovieAccountStates(movieId: Int): Flow<AccountStates> = flowOf(
            AccountStates(
                id = movieId,
                favorite = false,
                rated = null,
                watchlist = items.containsKey(movieId)
            )
        )

        override fun addMovieToWatchlist(movie: Movie, watchlist: Boolean): Flow<Unit> = flow {
            if (watchlist) {
                items[movie.id] = movie
            } else {
                items.remove(movie.id)
            }
            emit(Unit)
        }

        fun watchlistIds(): List<Int> = items.keys.toList()
        fun movie(movieId: Int): Movie? = items[movieId]
        fun isInWatchlist(movieId: Int): Boolean = items.containsKey(movieId)
    }

    private class FakeUserRemoteDataSource : UserRemoteDataSource {
        val addMovieCalls = mutableListOf<WatchlistPostRequest>()
        var getAccountCalls = 0

        override fun getAccount(): Flow<Account> = flow {
            getAccountCalls++
            emit(Account(id = 9, avatarPath = "remote", name = "Remote", username = "Remote"))
        }

        override fun getWatchlistMoviePaging(): Flow<PagingData<Movie>> =
            flowOf(PagingData.empty())

        override fun getMovieAccountStates(movieId: Int): Flow<AccountStates> =
            flowOf(AccountStates(id = movieId, favorite = true, rated = null, watchlist = true))

        override fun addMovieToWatchlist(movie: Movie, watchlist: Boolean): Flow<Unit> = flow {
            addMovieCalls += WatchlistPostRequest(
                mediaType = "movie",
                mediaId = movie.id,
                watchlist = watchlist
            )
            emit(Unit)
        }
    }
}
