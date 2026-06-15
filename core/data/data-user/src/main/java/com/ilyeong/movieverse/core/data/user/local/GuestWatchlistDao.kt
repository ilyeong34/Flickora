package com.ilyeong.movieverse.core.data.user.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ilyeong.movieverse.core.data.user.model.GuestWatchlistMovieEntity

@Dao
internal interface GuestWatchlistDao {

    @Query("SELECT * FROM guest_watchlist ORDER BY insertedAt DESC, id ASC")
    fun getWatchlistPagingSource(): PagingSource<Int, GuestWatchlistMovieEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun upsert(entity: GuestWatchlistMovieEntity): Long

    @Update
    suspend fun update(entity: GuestWatchlistMovieEntity)

    @Query("SELECT insertedAt FROM guest_watchlist WHERE id = :movieId")
    suspend fun getInsertedAt(movieId: Int): Long?

    @Query("DELETE FROM guest_watchlist WHERE id = :movieId")
    suspend fun delete(movieId: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM guest_watchlist WHERE id = :movieId)")
    suspend fun isInWatchlist(movieId: Int): Boolean
}
