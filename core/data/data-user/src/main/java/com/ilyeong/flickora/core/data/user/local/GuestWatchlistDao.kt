package com.ilyeong.flickora.core.data.user.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ilyeong.flickora.core.data.user.model.GuestWatchlistMovieEntity
import com.ilyeong.flickora.core.data.user.model.GuestWatchlistTvEntity

@Dao
internal interface GuestWatchlistDao {

    @Query("SELECT * FROM guest_watchlist ORDER BY insertedAt DESC, id ASC")
    fun getWatchlistPagingSource(): PagingSource<Int, GuestWatchlistMovieEntity>

    @Query("SELECT * FROM guest_watchlist_tv ORDER BY insertedAt DESC, id ASC")
    fun getTvWatchlistPagingSource(): PagingSource<Int, GuestWatchlistTvEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun upsert(entity: GuestWatchlistMovieEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun upsert(entity: GuestWatchlistTvEntity): Long

    @Update
    suspend fun update(entity: GuestWatchlistMovieEntity)

    @Update
    suspend fun update(entity: GuestWatchlistTvEntity)

    @Query("SELECT insertedAt FROM guest_watchlist WHERE id = :movieId")
    suspend fun getInsertedAt(movieId: Int): Long?

    @Query("SELECT insertedAt FROM guest_watchlist_tv WHERE id = :tvSeriesId")
    suspend fun getTvInsertedAt(tvSeriesId: Int): Long?

    @Query("DELETE FROM guest_watchlist WHERE id = :movieId")
    suspend fun delete(movieId: Int)

    @Query("DELETE FROM guest_watchlist_tv WHERE id = :tvSeriesId")
    suspend fun deleteTv(tvSeriesId: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM guest_watchlist WHERE id = :movieId)")
    suspend fun isInWatchlist(movieId: Int): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM guest_watchlist_tv WHERE id = :tvSeriesId)")
    suspend fun isTvInWatchlist(tvSeriesId: Int): Boolean
}
