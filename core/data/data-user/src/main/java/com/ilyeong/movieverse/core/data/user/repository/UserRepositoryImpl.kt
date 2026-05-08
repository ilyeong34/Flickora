package com.ilyeong.movieverse.core.data.user.repository

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

internal class UserRepositoryImpl @Inject constructor(
    private val apiService: UserApiService
) : UserRepository {
    override fun getAccount() = flow<Account> {
        val account = apiService.getAccount().toDomain()
        emit(account)
    }

    override fun getWatchlistMoviePaging(): Flow<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { WatchlistPagingSource(apiService) }
        ).flow
    }

    override fun getMovieAccountStates(movieId: Int) = flow<AccountStates> {
        val movieAccountStates = apiService.getMovieAccountStates(movieId).toDomain()
        emit(movieAccountStates)
    }

    override fun addMovieToWatchlist(movieId: Int, watchlist: Boolean) = flow<Unit> {
        val result = apiService.addMovieToWatchlist(
            WatchlistPostRequest(
                mediaType = "movie",
                mediaId = movieId,
                watchlist = watchlist
            )
        )

        when (result.success) {
            true -> emit(Unit)
            false -> throw Exception("Error: ${result.statusCode} - ${result.statusMessage}")
        }
    }
}