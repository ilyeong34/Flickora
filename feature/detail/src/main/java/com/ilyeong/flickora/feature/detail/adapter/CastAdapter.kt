package com.ilyeong.flickora.feature.detail.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.ilyeong.flickora.core.model.Cast
import com.ilyeong.flickora.feature.detail.viewholder.CastViewHolder

internal class CastAdapter : ListAdapter<Cast, CastViewHolder>(castDiffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = CastViewHolder.create(parent)

    override fun onBindViewHolder(
        holder: CastViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

}

private val castDiffUtil = object : DiffUtil.ItemCallback<Cast>() {
    override fun areItemsTheSame(
        oldItem: Cast,
        newItem: Cast
    ) = (oldItem.id == newItem.id)

    override fun areContentsTheSame(
        oldItem: Cast,
        newItem: Cast
    ) = (oldItem == newItem)
}