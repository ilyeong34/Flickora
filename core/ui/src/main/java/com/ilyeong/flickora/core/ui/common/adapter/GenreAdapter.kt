package com.ilyeong.flickora.core.ui.common.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.ilyeong.flickora.core.model.Genre
import com.ilyeong.flickora.core.ui.common.listener.ItemClickListener
import com.ilyeong.flickora.core.ui.common.viewholder.GenreViewHolder

class GenreAdapter(
    private val itemClickListener: ItemClickListener? = null,
) : ListAdapter<Genre, GenreViewHolder>(genreDiffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = GenreViewHolder.create(parent)

    override fun onBindViewHolder(
        holder: GenreViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position), itemClickListener)
    }
}

private val genreDiffUtil = object : DiffUtil.ItemCallback<Genre>() {
    override fun areItemsTheSame(
        oldItem: Genre,
        newItem: Genre
    ) = (oldItem.id == newItem.id)

    override fun areContentsTheSame(
        oldItem: Genre,
        newItem: Genre
    ) = (oldItem == newItem)
}
