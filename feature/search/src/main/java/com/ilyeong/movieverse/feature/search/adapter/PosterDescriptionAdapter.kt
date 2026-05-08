package com.ilyeong.movieverse.feature.search.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.ilyeong.movieverse.core.model.Movie
import com.ilyeong.movieverse.core.ui.common.diffutil.MovieDiffUtil
import com.ilyeong.movieverse.core.ui.common.listener.ItemClickListener
import com.ilyeong.movieverse.core.ui.common.viewholder.PosterDescriptionViewHolder

internal class PosterDescriptionAdapter(
    private val itemClickListener: ItemClickListener
) : ListAdapter<Movie, PosterDescriptionViewHolder>(MovieDiffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = PosterDescriptionViewHolder.create(parent)

    override fun onBindViewHolder(
        holder: PosterDescriptionViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position), itemClickListener)
    }
}