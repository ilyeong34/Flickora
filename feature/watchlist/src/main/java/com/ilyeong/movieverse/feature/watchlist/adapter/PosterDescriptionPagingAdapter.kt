package com.ilyeong.movieverse.feature.watchlist.adapter

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.ilyeong.movieverse.core.model.Movie
import com.ilyeong.movieverse.core.ui.common.diffutil.MovieDiffUtil
import com.ilyeong.movieverse.core.ui.common.listener.ItemClickListener
import com.ilyeong.movieverse.core.ui.common.viewholder.PosterDescriptionViewHolder

internal class PosterDescriptionPagingAdapter(
    private val itemClickListener: ItemClickListener
) : PagingDataAdapter<Movie, PosterDescriptionViewHolder>(MovieDiffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = PosterDescriptionViewHolder.create(parent)

    override fun onBindViewHolder(
        holder: PosterDescriptionViewHolder,
        position: Int
    ) {
        val item = getItem(position) ?: return
        holder.bind(item, itemClickListener)
    }

}