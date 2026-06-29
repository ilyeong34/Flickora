package com.ilyeong.flickora.feature.home.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.ilyeong.flickora.core.model.Media
import com.ilyeong.flickora.core.ui.common.diffutil.MediaDiffUtil
import com.ilyeong.flickora.feature.home.viewholder.PosterFullViewHolder

internal class PosterFullAdapter(private val itemClickListener: (Media) -> Unit) :
    ListAdapter<Media, PosterFullViewHolder>(MediaDiffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = PosterFullViewHolder.create(parent)

    override fun onBindViewHolder(
        holder: PosterFullViewHolder,
        position: Int
    ) {
        val item = getItem(position) ?: return
        holder.bind(item, itemClickListener)
    }
}
