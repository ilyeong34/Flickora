package com.ilyeong.movieverse.core.ui.common.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.crossfade
import com.ilyeong.movieverse.core.model.Movie
import com.ilyeong.movieverse.core.ui.common.listener.ItemClickListener
import com.ilyeong.movieverse.core.ui.databinding.ItemMoviePosterDescriptionBinding

class PosterDescriptionViewHolder private constructor(
    private val binding: ItemMoviePosterDescriptionBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(movie: Movie, itemClickListener: ItemClickListener) {
        binding.posterDefault.ivPoster.load(movie.posterPath) {
            crossfade(true)
            listener(
                onStart = { _ -> binding.posterDefault.tvPosterTitle.text = null },
                onError = { _, _ -> binding.posterDefault.tvPosterTitle.text = movie.title }
            )
        }
        binding.tvTitle.text = movie.title

        binding.rrv.rating = movie.voteAverage.toDouble()
        binding.rrv.ratingCount = movie.voteCount

        binding.tvDescription.text = movie.overview

        binding.root.setOnClickListener {
            itemClickListener.onItemClick(movie.id)
        }
    }

    companion object {
        fun create(parent: ViewGroup): PosterDescriptionViewHolder {
            val binding = ItemMoviePosterDescriptionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return PosterDescriptionViewHolder(binding)
        }
    }
}