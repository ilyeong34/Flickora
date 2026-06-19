package com.ilyeong.flickora.feature.detail.adapter

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.ilyeong.flickora.core.model.Review
import com.ilyeong.flickora.feature.detail.viewholder.ReviewViewHolder

internal class ReviewAdapter : PagingDataAdapter<Review, ReviewViewHolder>(reviewDiffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = ReviewViewHolder.create(parent)

    override fun onBindViewHolder(
        holder: ReviewViewHolder,
        position: Int
    ) {
        val item = getItem(position) ?: return
        holder.bind(item)
    }
}

private val reviewDiffUtil = object : DiffUtil.ItemCallback<Review>() {
    override fun areItemsTheSame(
        oldItem: Review,
        newItem: Review
    ) = (oldItem.id == newItem.id)

    override fun areContentsTheSame(
        oldItem: Review,
        newItem: Review
    ) = (oldItem == newItem)
}