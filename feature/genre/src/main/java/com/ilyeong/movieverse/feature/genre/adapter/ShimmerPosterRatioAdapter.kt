package com.ilyeong.movieverse.feature.genre.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ilyeong.movieverse.feature.genre.viewholder.ShimmerPosterRatioViewHolder

internal class ShimmerPosterRatioAdapter(
    private val itemCount: Int
) : RecyclerView.Adapter<ShimmerPosterRatioViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = ShimmerPosterRatioViewHolder.create(parent)

    override fun onBindViewHolder(holder: ShimmerPosterRatioViewHolder, position: Int) {
        holder.itemView.tag = position
    }

    override fun getItemCount(): Int = itemCount
}