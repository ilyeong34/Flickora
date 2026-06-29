package com.ilyeong.flickora.core.data.user.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ilyeong.flickora.core.model.Genre
import com.ilyeong.flickora.core.model.TvSeries

@Entity(tableName = "guest_watchlist_tv")
internal data class GuestWatchlistTvEntity(
    @PrimaryKey val id: Int,
    val backdropPath: String,
    val firstAirDate: String,
    val name: String,
    val overview: String,
    val posterPath: String,
    val originalLanguage: String,
    val originalName: String,
    val popularity: Double,
    val voteAverage: Double,
    val voteCount: Int,
    val insertedAt: Long,
)

internal fun TvSeries.toGuestWatchlistTvEntity(
    insertedAt: Long = System.currentTimeMillis()
): GuestWatchlistTvEntity = GuestWatchlistTvEntity(
    id = id,
    backdropPath = backdropPath,
    firstAirDate = firstAirDate,
    name = name,
    overview = overview,
    posterPath = posterPath,
    originalLanguage = originalLanguage,
    originalName = originalName,
    popularity = popularity,
    voteAverage = voteAverage,
    voteCount = voteCount,
    insertedAt = insertedAt,
)

internal fun GuestWatchlistTvEntity.toDomain(): TvSeries = TvSeries(
    adult = false,
    backdropPath = backdropPath,
    genreList = emptyList<Genre>(),
    id = id,
    originCountry = emptyList(),
    originalLanguage = originalLanguage,
    originalName = originalName.ifBlank { name },
    overview = overview,
    popularity = popularity,
    posterPath = posterPath,
    firstAirDate = firstAirDate,
    name = name,
    voteAverage = voteAverage,
    voteCount = voteCount,
)
