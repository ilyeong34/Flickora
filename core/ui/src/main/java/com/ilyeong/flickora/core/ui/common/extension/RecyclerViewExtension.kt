package com.ilyeong.flickora.core.ui.common.extension

import androidx.recyclerview.widget.RecyclerView
import com.ilyeong.flickora.core.ui.R

fun RecyclerView.calculateSpanCount(
    margin: Int = resources.getDimensionPixelSize(R.dimen.flickora_padding_large) * 2
): Int {
    val screenWidth = resources.displayMetrics.widthPixels
    val viewWidth = screenWidth - margin
    val itemWidth = resources.getDimensionPixelSize(R.dimen.flickora_poster_default_width)

    return (viewWidth / itemWidth)
}