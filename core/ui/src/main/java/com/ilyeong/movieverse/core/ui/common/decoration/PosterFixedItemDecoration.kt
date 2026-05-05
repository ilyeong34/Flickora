package com.ilyeong.movieverse.core.ui.common.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.ilyeong.movieverse.core.ui.R

object PosterFixedItemDecoration : ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val itemCount = state.itemCount
        val resources = parent.context.resources

        val smallPadding =
            resources.getDimensionPixelOffset(R.dimen.movieverse_padding_small)
        val largePadding =
            resources.getDimensionPixelOffset(R.dimen.movieverse_padding_large)

        outRect.top = smallPadding
        outRect.bottom = smallPadding
        outRect.left = smallPadding
        outRect.right = smallPadding

        when (position) {
            0 -> {
                outRect.left = largePadding
                outRect.right = smallPadding
            }

            itemCount - 1 -> {
                outRect.left = smallPadding
                outRect.right = largePadding
            }
        }
    }
}