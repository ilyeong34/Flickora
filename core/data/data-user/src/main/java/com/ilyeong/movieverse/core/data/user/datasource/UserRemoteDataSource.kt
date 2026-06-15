package com.ilyeong.movieverse.core.data.user.datasource

import androidx.paging.PagingData
import com.ilyeong.movieverse.core.model.Account
import com.ilyeong.movieverse.core.model.AccountStates
import com.ilyeong.movieverse.core.model.Movie
import kotlinx.coroutines.flow.Flow

internal interface UserRemoteDataSource {
    fun getAccount(): Flow<Account>
    fun getWatchlistMoviePaging(): Flow<PagingData<Movie>>
    fun getMovieAccountStates(movieId: Int): Flow<AccountStates>
    fun addMovieToWatchlist(movie: Movie, watchlist: Boolean): Flow<Unit>
}
