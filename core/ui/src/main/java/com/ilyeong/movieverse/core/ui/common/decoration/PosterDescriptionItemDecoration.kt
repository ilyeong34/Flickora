package com.ilyeong.movieverse.core.ui.common.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.ilyeong.movieverse.core.ui.R

object PosterDescriptionItemDecoration : ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val resources = parent.context.resources

        outRect.bottom = resources.getDimensionPixelOffset(R.dimen.movieverse_padding_large)

        when (position) {
            0 -> outRect.top = resources.getDimensionPixelOffset(R.dimen.movieverse_padding_large)
        }
    }
}