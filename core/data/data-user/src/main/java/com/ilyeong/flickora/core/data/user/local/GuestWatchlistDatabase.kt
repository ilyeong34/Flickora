package com.ilyeong.flickora.core.data.user.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ilyeong.flickora.core.data.user.model.GuestWatchlistMovieEntity
import com.ilyeong.flickora.core.data.user.model.GuestWatchlistTvEntity

@Database(
    entities = [GuestWatchlistMovieEntity::class, GuestWatchlistTvEntity::class],
    version = 2,
    exportSchema = false
)
internal abstract class GuestWatchlistDatabase : RoomDatabase() {
    abstract fun guestWatchlistDao(): GuestWatchlistDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS guest_watchlist_tv (
                        id INTEGER NOT NULL PRIMARY KEY,
                        backdropPath TEXT NOT NULL,
                        firstAirDate TEXT NOT NULL,
                        name TEXT NOT NULL,
                        overview TEXT NOT NULL,
                        posterPath TEXT NOT NULL,
                        originalLanguage TEXT NOT NULL,
                        originalName TEXT NOT NULL,
                        popularity REAL NOT NULL,
                        voteAverage REAL NOT NULL,
                        voteCount INTEGER NOT NULL,
                        insertedAt INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }
    }
}
