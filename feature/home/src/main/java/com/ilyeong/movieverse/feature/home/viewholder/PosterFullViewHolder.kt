package com.ilyeong.movieverse.feature.home.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.crossfade
import com.ilyeong.movieverse.core.model.Movie
import com.ilyeong.movieverse.core.ui.common.listener.ItemClickListener
import com.ilyeong.movieverse.feature.home.databinding.ItemMoviePosterFullSizeBinding

internal class PosterFullViewHolder private constructor(
    private val binding: ItemMoviePosterFullSizeBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(movie: Movie, itemClickListener: ItemClickListener) {
        binding.ivPoster.load(movie.posterPath) {
            crossfade(true)
            listener(
                onStart = { _ -> binding.tvPosterTitle.text = null },
                onError = { _, _ -> binding.tvPosterTitle.text = movie.title }
            )
        }
        binding.root.setOnClickListener {
            itemClickListener.onItemClick(movie.id)
        }
    }

    companion object {
        fun create(parent: ViewGroup): PosterFullViewHolder {
            val binding =
                ItemMoviePosterFullSizeBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            return PosterFullViewHolder(binding)
        }
    }
}