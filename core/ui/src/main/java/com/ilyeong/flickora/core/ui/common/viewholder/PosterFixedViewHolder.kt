package com.ilyeong.flickora.core.ui.common.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.crossfade
import com.ilyeong.flickora.core.model.Media
import com.ilyeong.flickora.core.ui.common.listener.ItemClickListener
import com.ilyeong.flickora.core.ui.databinding.ItemMediaPosterFixedSizeBinding

class PosterFixedViewHolder private constructor(
    private val binding: ItemMediaPosterFixedSizeBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(media: Media, itemClickListener: ItemClickListener<Media>) {
        binding.ivPoster.load(media.posterPath) {
            crossfade(true)
            listener(
                onStart = { _ -> binding.tvPosterTitle.text = null },
                onError = { _, _ -> binding.tvPosterTitle.text = media.title }
            )
        }
        binding.root.setOnClickListener {
            itemClickListener.onItemClick(media)
        }
    }

    companion object {
        fun create(parent: ViewGroup): PosterFixedViewHolder {
            val binding =
                ItemMediaPosterFixedSizeBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            return PosterFixedViewHolder(binding)
        }
    }
}
