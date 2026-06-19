package com.ilyeong.flickora.core.data.user.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ilyeong.flickora.core.model.Genre
import com.ilyeong.flickora.core.model.Movie
import com.ilyeong.flickora.core.model.SpokenLanguage

@Entity(tableName = "guest_watchlist")
internal data class GuestWatchlistMovieEntity(
    @PrimaryKey val id: Int,
    val posterPath: String,
    val title: String,
    val overview: String,
    val voteAverage: Double,
    val voteCount: Int,
    val insertedAt: Long,
)

internal fun Movie.toGuestWatchlistEntity(
    insertedAt: Long = System.currentTimeMillis()
): GuestWatchlistMovieEntity =
    GuestWatchlistMovieEntity(
        id = id,
        posterPath = posterPath,
        title = title,
        overview = overview,
        voteAverage = voteAverage,
        voteCount = voteCount,
        insertedAt = insertedAt,
    )

internal fun GuestWatchlistMovieEntity.toDomain(): Movie = Movie(
    adult = false,
    collection = null,
    backdropPath = "",
    genreList = emptyList<Genre>(),
    id = id,
    originalLanguage = "",
    originalTitle = title,
    overview = overview,
    popularity = 0.0,
    posterPath = posterPath,
    releaseDate = "",
    runtime = 0,
    spokenLanguageList = emptyList<SpokenLanguage>(),
    title = title,
    video = false,
    voteAverage = voteAverage,
    voteCount = voteCount,
    isInWatchlist = true,
)
