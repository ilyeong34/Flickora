package com.ilyeong.movieverse.core.data.user.datasource

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.ilyeong.movieverse.core.data.user.local.GuestWatchlistDao
import com.ilyeong.movieverse.core.data.user.model.toDomain
import com.ilyeong.movieverse.core.data.user.model.toGuestWatchlistEntity
import com.ilyeong.movieverse.core.model.Account
import com.ilyeong.movieverse.core.model.AccountStates
import com.ilyeong.movieverse.core.model.Movie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class UserLocalDataSourceImpl @Inject constructor(
    private val guestWatchlistDao: GuestWatchlistDao,
) : UserLocalDataSource {

    override fun getAccount(): Flow<Account> = flowOf(
        Account(id = 0, avatarPath = "", name = "Guest", username = "Guest")
    )

    override fun getWatchlistMoviePaging(): Flow<PagingData<Movie>> =
        Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { guestWatchlistDao.getWatchlistPagingSource() }
        ).flow.map { pagingData ->
            pagingData.map { entity -> entity.toDomain() }
        }

    override fun getMovieAccountStates(movieId: Int): Flow<AccountStates> = flow {
        emit(
            AccountStates(
                id = movieId,
                favorite = false,
                rated = null,
                watchlist = guestWatchlistDao.isInWatchlist(movieId)
            )
        )
    }

    // Guest watchlist is intentionally local-only.
    // Login/logout only swaps the active datasource; we do not migrate or merge rows here.
    override fun addMovieToWatchlist(movie: Movie, watchlist: Boolean): Flow<Unit> = flow {
        if (!watchlist) {
            guestWatchlistDao.delete(movie.id)
            emit(Unit)
            return@flow
        }

        val insertedAt = guestWatchlistDao.getInsertedAt(movie.id) ?: System.currentTimeMillis()
        val entity = movie.toGuestWatchlistEntity(insertedAt = insertedAt)
        val insertedRowId = guestWatchlistDao.upsert(entity)

        if (insertedRowId == -1L) {
            guestWatchlistDao.update(entity)
        }
        emit(Unit)
    }
}
