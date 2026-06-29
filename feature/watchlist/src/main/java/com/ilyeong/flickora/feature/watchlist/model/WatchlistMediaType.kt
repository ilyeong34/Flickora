package com.ilyeong.flickora.feature.watchlist.model

import androidx.annotation.StringRes
import com.ilyeong.flickora.core.ui.R

internal enum class WatchlistMediaType(
    @StringRes val labelRes: Int
) {
    MOVIE(labelRes = R.string.watchlist_movie_chip),
    TV_SERIES(labelRes = R.string.watchlist_tv_series_chip)
}
