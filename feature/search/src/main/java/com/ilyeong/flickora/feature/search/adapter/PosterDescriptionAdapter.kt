package com.ilyeong.flickora.feature.search.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.ilyeong.flickora.core.model.Media
import com.ilyeong.flickora.core.ui.common.diffutil.MediaDiffUtil
import com.ilyeong.flickora.core.ui.common.listener.ItemClickListener
import com.ilyeong.flickora.core.ui.common.viewholder.PosterDescriptionViewHolder

internal class PosterDescriptionAdapter(
    private val itemClickListener: ItemClickListener<Media>
) : ListAdapter<Media, PosterDescriptionViewHolder>(MediaDiffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = PosterDescriptionViewHolder.create(parent)

    override fun onBindViewHolder(
        holder: PosterDescriptionViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position), itemClickListener)
    }
}
