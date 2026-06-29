package com.ilyeong.flickora.core.ui.common.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.crossfade
import com.ilyeong.flickora.core.model.Media
import com.ilyeong.flickora.core.ui.databinding.ItemMoviePosterDescriptionBinding

class PosterDescriptionViewHolder private constructor(
    private val binding: ItemMoviePosterDescriptionBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(
        media: Media,
        onItemClick: (Media) -> Unit
    ) {
        binding.posterDefault.ivPoster.load(media.posterPath) {
            crossfade(true)
            listener(
                onStart = { _ -> binding.posterDefault.tvPosterTitle.text = null },
                onError = { _, _ -> binding.posterDefault.tvPosterTitle.text = media.title }
            )
        }
        binding.tvTitle.text = media.title

        binding.rrv.rating = media.voteAverage
        binding.rrv.ratingCount = media.voteCount

        binding.tvDescription.text = media.overview

        binding.root.setOnClickListener {
            onItemClick(media)
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
