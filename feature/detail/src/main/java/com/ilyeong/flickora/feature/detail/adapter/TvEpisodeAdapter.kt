package com.ilyeong.flickora.feature.detail.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.ilyeong.flickora.core.model.TvEpisode
import com.ilyeong.flickora.feature.detail.viewholder.TvEpisodeViewHolder

internal class TvEpisodeAdapter : ListAdapter<TvEpisode, TvEpisodeViewHolder>(tvEpisodeDiffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = TvEpisodeViewHolder.create(parent)

    override fun onBindViewHolder(
        holder: TvEpisodeViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }
}

private val tvEpisodeDiffUtil = object : DiffUtil.ItemCallback<TvEpisode>() {
    override fun areItemsTheSame(
        oldItem: TvEpisode,
        newItem: TvEpisode
    ) = (oldItem.id == newItem.id)

    override fun areContentsTheSame(
        oldItem: TvEpisode,
        newItem: TvEpisode
    ) = (oldItem == newItem)
}

