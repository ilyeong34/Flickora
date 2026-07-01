package com.ilyeong.flickora.feature.home.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.ilyeong.flickora.core.model.Media
import com.ilyeong.flickora.core.ui.common.diffutil.MediaDiffUtil
import com.ilyeong.flickora.feature.home.viewholder.PosterRankingViewHolder

internal class PosterRankingAdapter(
    private val itemClickListener: (Media) -> Unit
) : ListAdapter<Media, PosterRankingViewHolder>(MediaDiffUtil) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = PosterRankingViewHolder.create(parent)

    override fun onBindViewHolder(
        holder: PosterRankingViewHolder,
        position: Int
    ) {
        val item = getItem(position) ?: return
        holder.bind(
            media = item,
            rank = rankText(position),
            onItemClick = itemClickListener
        )
    }

    private fun rankText(position: Int): String = (position + 1).toString()
}
