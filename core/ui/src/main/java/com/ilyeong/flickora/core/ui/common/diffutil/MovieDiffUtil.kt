package com.ilyeong.flickora.core.ui.common.diffutil

import com.ilyeong.flickora.core.model.Movie
import androidx.recyclerview.widget.DiffUtil

object MovieDiffUtil : DiffUtil.ItemCallback<Movie>() {
    override fun areItemsTheSame(
        oldItem: Movie,
        newItem: Movie
    ) = (oldItem.id == newItem.id)

    override fun areContentsTheSame(
        oldItem: Movie,
        newItem: Movie
    ) = (oldItem == newItem)
}