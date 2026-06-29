package com.ilyeong.flickora.feature.search.adapter

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.ilyeong.flickora.core.model.Media
import com.ilyeong.flickora.core.ui.common.diffutil.MediaDiffUtil
import com.ilyeong.flickora.core.ui.common.viewholder.PosterRatioViewHolder

internal class PosterRatioPagingAdapter(
    private val onMediaClick: (Media) -> Unit
) : PagingDataAdapter<Media, PosterRatioViewHolder>(MediaDiffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = PosterRatioViewHolder.create(parent)

    override fun onBindViewHolder(
        holder: PosterRatioViewHolder,
        position: Int
    ) {
        val item = getItem(position) ?: return
        holder.bind(item, onMediaClick)
    }
}
