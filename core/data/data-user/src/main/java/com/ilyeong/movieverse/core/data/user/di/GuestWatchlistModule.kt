package com.ilyeong.movieverse.core.data.user.di

import android.content.Context
import androidx.room.Room
import com.ilyeong.movieverse.core.data.user.local.GuestWatchlistDao
import com.ilyeong.movieverse.core.data.user.local.GuestWatchlistDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object GuestWatchlistModule {

    private const val DATABASE_NAME = "guest_watchlist.db"

    @Provides
    @Singleton
    fun provideGuestWatchlistDatabase(
        @ApplicationContext context: Context
    ): GuestWatchlistDatabase = Room.databaseBuilder(
        context,
        GuestWatchlistDatabase::class.java,
        DATABASE_NAME
    ).build()

    @Provides
    @Singleton
    fun provideGuestWatchlistDao(
        database: GuestWatchlistDatabase
    ): GuestWatchlistDao = database.guestWatchlistDao()
}
