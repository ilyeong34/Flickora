package com.ilyeong.flickora.core.data.user.datasource

import androidx.paging.PagingData
import com.ilyeong.flickora.core.model.Account
import com.ilyeong.flickora.core.model.AccountStates
import com.ilyeong.flickora.core.model.Movie
import kotlinx.coroutines.flow.Flow

internal interface UserLocalDataSource {
    fun getAccount(): Flow<Account>
    fun getWatchlistMoviePaging(): Flow<PagingData<Movie>>
    fun getMovieAccountStates(movieId: Int): Flow<AccountStates>
    fun addMovieToWatchlist(movie: Movie, watchlist: Boolean): Flow<Unit>
}
