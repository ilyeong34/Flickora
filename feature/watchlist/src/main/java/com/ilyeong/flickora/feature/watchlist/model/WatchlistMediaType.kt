package com.ilyeong.flickora.feature.watchlist.model

import androidx.annotation.StringRes
import com.ilyeong.flickora.core.ui.R

internal enum class WatchlistMediaType(
    val value: Int,
    @StringRes val labelRes: Int
) {
    MOVIE(
        value = 0,
        labelRes = R.string.watchlist_movie_chip
    ),
    TV_SERIES(
        value = 1,
        labelRes = R.string.watchlist_tv_series_chip
    );

    companion object {
        fun fromValue(value: Int): WatchlistMediaType = values().firstOrNull { it.value == value }
            ?: MOVIE
    }
}
