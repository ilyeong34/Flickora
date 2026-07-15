package com.ilyeong.flickora.feature.home.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.crossfade
import com.ilyeong.flickora.core.model.Media
import com.ilyeong.flickora.core.ui.common.listener.ItemClickListener
import com.ilyeong.flickora.feature.home.databinding.ItemMediaPosterFullSizeBinding

internal class PosterFullViewHolder private constructor(
    private val binding: ItemMediaPosterFullSizeBinding
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
        fun create(parent: ViewGroup): PosterFullViewHolder {
            val binding =
                ItemMediaPosterFullSizeBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            return PosterFullViewHolder(binding)
        }
    }
}
