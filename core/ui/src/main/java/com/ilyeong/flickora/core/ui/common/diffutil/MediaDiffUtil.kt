package com.ilyeong.flickora.core.ui.common.diffutil

import androidx.recyclerview.widget.DiffUtil
import com.ilyeong.flickora.core.model.Media

object MediaDiffUtil : DiffUtil.ItemCallback<Media>() {
    override fun areItemsTheSame(
        oldItem: Media,
        newItem: Media
    ) = oldItem::class == newItem::class && oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: Media,
        newItem: Media
    ) = oldItem == newItem
}
