package com.ilyeong.movieverse.feature.home.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.ilyeong.movieverse.core.model.Movie
import com.ilyeong.movieverse.core.ui.common.diffutil.MovieDiffUtil
import com.ilyeong.movieverse.core.ui.common.listener.ItemClickListener
import com.ilyeong.movieverse.feature.home.viewholder.PosterFullViewHolder

internal class PosterFullAdapter(private val itemClickListener: ItemClickListener) :
    ListAdapter<Movie, PosterFullViewHolder>(MovieDiffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = PosterFullViewHolder.create(parent)

    override fun onBindViewHolder(
        holder: PosterFullViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position), itemClickListener)
    }
}