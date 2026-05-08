package com.ilyeong.movieverse.core.ui.common.diffutil

import com.ilyeong.movieverse.core.model.Movie
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