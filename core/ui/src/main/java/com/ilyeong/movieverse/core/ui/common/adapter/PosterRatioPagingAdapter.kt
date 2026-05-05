package com.ilyeong.movieverse.core.ui.common.adapter

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.ilyeong.movieverse.core.model.Movie
import com.ilyeong.movieverse.core.ui.common.diffutil.MovieDiffUtil
import com.ilyeong.movieverse.core.ui.common.listener.ItemClickListener
import com.ilyeong.movieverse.core.ui.common.viewholder.PosterRatioViewHolder

class PosterRatioPagingAdapter(
    private val itemClickListener: ItemClickListener
) : PagingDataAdapter<Movie, PosterRatioViewHolder>(MovieDiffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = PosterRatioViewHolder.create(parent)

    override fun onBindViewHolder(
        holder: PosterRatioViewHolder,
        position: Int
    ) {
        val item = getItem(position) ?: return
        holder.bind(item, itemClickListener)
    }
}