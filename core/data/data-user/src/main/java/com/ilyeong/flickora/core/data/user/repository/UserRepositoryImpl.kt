package com.ilyeong.flickora.core.data.user.repository

import androidx.paging.PagingData
import com.ilyeong.flickora.core.data.user.datasource.UserLocalDataSource
import com.ilyeong.flickora.core.data.user.datasource.UserRemoteDataSource
import com.ilyeong.flickora.core.datastore.user.UserPreferenceDataSource
import com.ilyeong.flickora.core.model.Account
import com.ilyeong.flickora.core.model.AccountStates
import com.ilyeong.flickora.core.model.Movie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

internal class UserRepositoryImpl @Inject constructor(
    private val userPreferenceDataSource: UserPreferenceDataSource,
    private val userLocalDataSource: UserLocalDataSource,
    private val userRemoteDataSource: UserRemoteDataSource,
) : UserRepository {

    override fun getAccount(): Flow<Account> = flow {
        if (userPreferenceDataSource.isGuestMode()) {
            emitAll(userLocalDataSource.getAccount())
        } else {
            emitAll(userRemoteDataSource.getAccount())
        }
    }

    override fun getWatchlistMoviePaging(): Flow<PagingData<Movie>> = flow {
        if (userPreferenceDataSource.isGuestMode()) {
            emitAll(userLocalDataSource.getWatchlistMoviePaging())
        } else {
            emitAll(userRemoteDataSource.getWatchlistMoviePaging())
        }
    }

    override fun getMovieAccountStates(movieId: Int): Flow<AccountStates> = flow {
        if (userPreferenceDataSource.isGuestMode()) {
            emitAll(userLocalDataSource.getMovieAccountStates(movieId))
        } else {
            emitAll(userRemoteDataSource.getMovieAccountStates(movieId))
        }
    }

    override fun addMovieToWatchlist(movie: Movie, watchlist: Boolean): Flow<Unit> = flow {
        if (userPreferenceDataSource.isGuestMode()) {
            emitAll(userLocalDataSource.addMovieToWatchlist(movie, watchlist))
        } else {
            emitAll(userRemoteDataSource.addMovieToWatchlist(movie, watchlist))
        }
    }
}
