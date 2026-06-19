package com.ilyeong.flickora.feature.home.adapter

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.ilyeong.flickora.core.model.Movie
import com.ilyeong.flickora.core.ui.common.diffutil.MovieDiffUtil
import com.ilyeong.flickora.core.ui.common.listener.ItemClickListener
import com.ilyeong.flickora.core.ui.common.viewholder.PosterFixedViewHolder

internal class PosterFixedPagingAdapter(
    private val itemClickListener: ItemClickListener
) : PagingDataAdapter<Movie, PosterFixedViewHolder>(MovieDiffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = PosterFixedViewHolder.create(parent)

    override fun onBindViewHolder(
        holder: PosterFixedViewHolder,
        position: Int
    ) {
        val item = getItem(position) ?: return
        holder.bind(item, itemClickListener)
    }
}