package com.ilyeong.flickora.core.data.user.datasource

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.ilyeong.flickora.core.data.user.api.UserApiService
import com.ilyeong.flickora.core.data.user.model.WatchlistPostRequest
import com.ilyeong.flickora.core.data.user.model.toDomain
import com.ilyeong.flickora.core.data.user.paging.WatchlistMoviePagingSource
import com.ilyeong.flickora.core.data.user.paging.WatchlistTvPagingSource
import com.ilyeong.flickora.core.model.Account
import com.ilyeong.flickora.core.model.AccountStates
import com.ilyeong.flickora.core.model.Movie
import com.ilyeong.flickora.core.model.TvSeries
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

internal class UserRemoteDataSourceImpl @Inject constructor(
    private val apiService: UserApiService,
) : UserRemoteDataSource {

    override fun getAccount(): Flow<Account> = flow {
        emit(apiService.getAccount().toDomain())
    }

    override fun getWatchlistMoviePaging(): Flow<PagingData<Movie>> =
        Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { WatchlistMoviePagingSource(apiService) }
        ).flow

    override fun getWatchlistTvPaging(): Flow<PagingData<TvSeries>> =
        Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { WatchlistTvPagingSource(apiService) }
        ).flow

    override fun getMovieAccountStates(movieId: Int): Flow<AccountStates> = flow {
        emit(apiService.getMovieAccountStates(movieId).toDomain())
    }

    override fun getTvAccountStates(tvSeriesId: Int): Flow<AccountStates> = flow {
        emit(apiService.getTvAccountStates(tvSeriesId).toDomain())
    }

    override fun addMovieToWatchlist(movie: Movie, watchlist: Boolean): Flow<Unit> = flow {
        val result = apiService.addMovieToWatchlist(
            WatchlistPostRequest(
                mediaType = "movie",
                mediaId = movie.id,
                watchlist = watchlist
            )
        )

        if (result.success) {
            emit(Unit)
        } else {
            throw Exception("Error: ${result.statusCode} - ${result.statusMessage}")
        }
    }

    override fun addTvToWatchlist(tvSeries: TvSeries, watchlist: Boolean): Flow<Unit> = flow {
        val result = apiService.addMovieToWatchlist(
            WatchlistPostRequest(
                mediaType = "tv",
                mediaId = tvSeries.id,
                watchlist = watchlist
            )
        )

        if (result.success) {
            emit(Unit)
        } else {
            throw Exception("Error: ${result.statusCode} - ${result.statusMessage}")
        }
    }
}
