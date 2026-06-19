package com.ilyeong.flickora.feature.home.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.ilyeong.flickora.core.model.Movie
import com.ilyeong.flickora.core.ui.common.diffutil.MovieDiffUtil
import com.ilyeong.flickora.core.ui.common.listener.ItemClickListener
import com.ilyeong.flickora.feature.home.viewholder.PosterFullViewHolder

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