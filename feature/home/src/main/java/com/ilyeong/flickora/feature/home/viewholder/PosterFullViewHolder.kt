package com.ilyeong.flickora.feature.home.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.crossfade
import com.ilyeong.flickora.core.model.Media
import com.ilyeong.flickora.feature.home.databinding.ItemMoviePosterFullSizeBinding

internal class PosterFullViewHolder private constructor(
    private val binding: ItemMoviePosterFullSizeBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(media: Media, itemClickListener: (Media) -> Unit) {
        binding.ivPoster.load(media.posterPath) {
            crossfade(true)
            listener(
                onStart = { _ -> binding.tvPosterTitle.text = null },
                onError = { _, _ -> binding.tvPosterTitle.text = media.title }
            )
        }
        binding.root.setOnClickListener {
            itemClickListener(media)
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
