package com.ilyeong.movieverse.core.data.user.datasource

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.ilyeong.movieverse.core.data.user.api.UserApiService
import com.ilyeong.movieverse.core.data.user.model.WatchlistPostRequest
import com.ilyeong.movieverse.core.data.user.model.toDomain
import com.ilyeong.movieverse.core.data.user.paging.WatchlistPagingSource
import com.ilyeong.movieverse.core.model.Account
import com.ilyeong.movieverse.core.model.AccountStates
import com.ilyeong.movieverse.core.model.Movie
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
            pagingSourceFactory = { WatchlistPagingSource(apiService) }
        ).flow

    override fun getMovieAccountStates(movieId: Int): Flow<AccountStates> = flow {
        emit(apiService.getMovieAccountStates(movieId).toDomain())
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
}
