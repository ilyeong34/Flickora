package com.ilyeong.flickora.feature.home.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.crossfade
import com.ilyeong.flickora.core.model.Media
import com.ilyeong.flickora.feature.home.databinding.ItemMovieRankingPosterBinding

internal class PosterRankingViewHolder private constructor(
    private val binding: ItemMovieRankingPosterBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(
        media: Media,
        rank: String,
        onItemClick: (Media) -> Unit
    ) {
        binding.poster.ivPoster.load(media.posterPath) {
            crossfade(true)
            listener(
                onStart = { _ -> binding.poster.tvPosterTitle.text = null },
                onError = { _, _ -> binding.poster.tvPosterTitle.text = media.title }
            )
        }
        binding.tvRank.text = rank

        binding.root.setOnClickListener {
            onItemClick(media)
        }
    }

    companion object {
        fun create(parent: ViewGroup): PosterRankingViewHolder {
            val binding = ItemMovieRankingPosterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return PosterRankingViewHolder(binding)
        }
    }
}
