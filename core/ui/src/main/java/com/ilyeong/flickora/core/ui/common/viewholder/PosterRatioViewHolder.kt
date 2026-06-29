package com.ilyeong.flickora.core.ui.common.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.crossfade
import com.ilyeong.flickora.core.model.Media
import com.ilyeong.flickora.core.ui.databinding.ItemMoviePosterRatioSizeBinding

class PosterRatioViewHolder private constructor(
    private val binding: ItemMoviePosterRatioSizeBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(media: Media, onMediaClick: (Media) -> Unit) {
        binding.ivPoster.load(media.posterPath) {
            crossfade(true)
            listener(
                onStart = { _ -> binding.tvPosterTitle.text = null },
                onError = { _, _ -> binding.tvPosterTitle.text = media.title }
            )
        }
        binding.root.setOnClickListener {
            onMediaClick(media)
        }
    }

    companion object {
        fun create(parent: ViewGroup): PosterRatioViewHolder {
            val binding =
                ItemMoviePosterRatioSizeBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            return PosterRatioViewHolder(binding)
        }
    }
}
