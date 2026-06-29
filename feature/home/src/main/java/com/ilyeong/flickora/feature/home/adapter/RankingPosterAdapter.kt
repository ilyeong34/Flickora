package com.ilyeong.flickora.feature.home.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.ilyeong.flickora.core.model.Media
import com.ilyeong.flickora.core.ui.common.diffutil.MediaDiffUtil
import com.ilyeong.flickora.feature.home.viewholder.RankingPosterViewHolder

internal class RankingPosterAdapter(
    private val itemClickListener: (Media) -> Unit
) : ListAdapter<Media, RankingPosterViewHolder>(MediaDiffUtil) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = RankingPosterViewHolder.create(parent)

    override fun onBindViewHolder(
        holder: RankingPosterViewHolder,
        position: Int
    ) {
        val item = getItem(position) ?: return
        holder.bind(
            media = item,
            rank = rankText(position),
            onItemClick = itemClickListener
        )
    }

    internal companion object {
        fun rankText(position: Int): String = (position + 1).toString()
    }
}
