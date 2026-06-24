package com.ilyeong.flickora.feature.home.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.load
import coil3.request.crossfade
import com.ilyeong.flickora.core.model.TvSeries
import com.ilyeong.flickora.core.ui.databinding.ItemMoviePosterFixedSizeBinding

internal class PopularTvPosterViewHolder private constructor(
    private val binding: ItemMoviePosterFixedSizeBinding,
    private val imageLoader: ImageLoader
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(tvSeries: TvSeries) {
        binding.ivPoster.load(
            tvSeries.posterPath,
            imageLoader = imageLoader
        ) {
            crossfade(true)
            listener(
                onStart = { _ -> binding.tvPosterTitle.text = null },
                onError = { _, _ -> binding.tvPosterTitle.text = tvSeries.name.ifBlank { tvSeries.originalName } }
            )
        }
    }

    companion object {
        fun create(
            parent: ViewGroup,
            imageLoader: ImageLoader = SingletonImageLoader.get(parent.context)
        ): PopularTvPosterViewHolder {
            val binding =
                ItemMoviePosterFixedSizeBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            return PopularTvPosterViewHolder(binding, imageLoader)
        }
    }
}
