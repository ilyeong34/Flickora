package com.ilyeong.flickora.core.ui.common.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.ilyeong.flickora.core.ui.R

object PosterFixedItemDecoration : ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val smallPadding = parent.context.resources
            .getDimensionPixelOffset(R.dimen.flickora_padding_small)

        outRect.top = smallPadding
        outRect.bottom = smallPadding
        outRect.left = smallPadding
        outRect.right = smallPadding
    }
}
