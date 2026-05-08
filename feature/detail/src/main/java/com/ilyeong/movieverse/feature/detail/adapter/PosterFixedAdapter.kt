package com.ilyeong.movieverse.feature.detail.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.ilyeong.movieverse.core.model.Movie
import com.ilyeong.movieverse.core.ui.common.diffutil.MovieDiffUtil
import com.ilyeong.movieverse.core.ui.common.listener.ItemClickListener
import com.ilyeong.movieverse.core.ui.common.viewholder.PosterFixedViewHolder

internal class PosterFixedAdapter(private val itemClickListener: ItemClickListener) :
    ListAdapter<Movie, PosterFixedViewHolder>(MovieDiffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = PosterFixedViewHolder.create(parent)

    override fun onBindViewHolder(
        holder: PosterFixedViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position), itemClickListener)
    }
}