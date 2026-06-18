package com.ilyeong.movieverse.core.data.user.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ilyeong.movieverse.core.data.user.model.GuestWatchlistMovieEntity

@Database(
    entities = [GuestWatchlistMovieEntity::class],
    version = 1,
    exportSchema = false
)
internal abstract class GuestWatchlistDatabase : RoomDatabase() {
    abstract fun guestWatchlistDao(): GuestWatchlistDao
}
