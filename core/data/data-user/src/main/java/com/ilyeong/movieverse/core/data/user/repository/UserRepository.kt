package com.ilyeong.movieverse.core.data.user.repository

import androidx.paging.PagingData
import com.ilyeong.movieverse.core.model.Account
import com.ilyeong.movieverse.core.model.AccountStates
import com.ilyeong.movieverse.core.model.Movie
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun getAccount(): Flow<Account>
    fun getWatchlistMoviePaging(): Flow<PagingData<Movie>>
    fun getMovieAccountStates(movieId: Int): Flow<AccountStates>
    fun addMovieToWatchlist(movieId: Int, watchlist: Boolean): Flow<Unit>
}