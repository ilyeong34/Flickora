package com.ilyeong.flickora.core.data.user.repository

import androidx.paging.PagingData
import com.ilyeong.flickora.core.model.Account
import com.ilyeong.flickora.core.model.AccountStates
import com.ilyeong.flickora.core.model.Movie
import com.ilyeong.flickora.core.model.TvSeries
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun getAccount(): Flow<Account>
    fun getWatchlistMoviePaging(): Flow<PagingData<Movie>>
    fun getWatchlistTvPaging(): Flow<PagingData<TvSeries>>
    fun getMovieAccountStates(movieId: Int): Flow<AccountStates>
    fun getTvAccountStates(tvSeriesId: Int): Flow<AccountStates>
    fun addMovieToWatchlist(movie: Movie, watchlist: Boolean): Flow<Unit>
    fun addTvToWatchlist(tvSeries: TvSeries, watchlist: Boolean): Flow<Unit>
}
