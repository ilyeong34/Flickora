package com.ilyeong.flickora.feature.home.adapter

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.ilyeong.flickora.core.model.Media
import com.ilyeong.flickora.core.ui.common.diffutil.MediaDiffUtil
import com.ilyeong.flickora.core.ui.common.listener.ItemClickListener
import com.ilyeong.flickora.core.ui.common.viewholder.PosterFixedViewHolder

internal class PosterFixedPagingAdapter(
    private val itemClickListener: ItemClickListener<Media>
) : PagingDataAdapter<Media, PosterFixedViewHolder>(MediaDiffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = PosterFixedViewHolder.create(parent)

    override fun onBindViewHolder(
        holder: PosterFixedViewHolder,
        position: Int
    ) {
        val item = getItem(position) ?: return
        holder.bind(item, itemClickListener)
    }
}
