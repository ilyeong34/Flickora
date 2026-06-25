package com.ilyeong.flickora.core.ui.common.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.crossfade
import com.ilyeong.flickora.core.ui.common.listener.ItemClickListener
import com.ilyeong.flickora.core.ui.common.model.PosterUiModel
import com.ilyeong.flickora.core.ui.databinding.ItemMoviePosterFixedSizeBinding

class PosterFixedViewHolder private constructor(
    private val binding: ItemMoviePosterFixedSizeBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(poster: PosterUiModel, itemClickListener: ItemClickListener) {
        binding.ivPoster.load(poster.posterPath) {
            crossfade(true)
            listener(
                onStart = { _ -> binding.tvPosterTitle.text = null },
                onError = { _, _ -> binding.tvPosterTitle.text = poster.title }
            )
        }
        binding.root.setOnClickListener {
            itemClickListener.onItemClick(poster.id)
        }
    }

    companion object {
        fun create(parent: ViewGroup): PosterFixedViewHolder {
            val binding =
                ItemMoviePosterFixedSizeBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            return PosterFixedViewHolder(binding)
        }
    }
}
