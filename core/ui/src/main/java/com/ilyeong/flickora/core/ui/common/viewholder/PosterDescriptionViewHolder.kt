package com.ilyeong.flickora.core.ui.common.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.crossfade
import com.ilyeong.flickora.core.ui.common.listener.ItemClickListener
import com.ilyeong.flickora.core.ui.common.model.PosterUiModel
import com.ilyeong.flickora.core.ui.databinding.ItemMoviePosterDescriptionBinding

class PosterDescriptionViewHolder private constructor(
    private val binding: ItemMoviePosterDescriptionBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(poster: PosterUiModel, itemClickListener: ItemClickListener) {
        binding.posterDefault.ivPoster.load(poster.posterPath) {
            crossfade(true)
            listener(
                onStart = { _ -> binding.posterDefault.tvPosterTitle.text = null },
                onError = { _, _ -> binding.posterDefault.tvPosterTitle.text = poster.title }
            )
        }
        binding.tvTitle.text = poster.title

        binding.rrv.rating = poster.voteAverage
        binding.rrv.ratingCount = poster.voteCount

        binding.tvDescription.text = poster.overview

        binding.root.setOnClickListener {
            itemClickListener.onItemClick(poster.id)
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
