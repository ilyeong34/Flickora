package com.ilyeong.movieverse.feature.genre.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ilyeong.movieverse.feature.genre.databinding.ShimmerItemMoviePosterRatioSizeBinding

internal class ShimmerPosterRatioViewHolder private constructor(
    binding: ShimmerItemMoviePosterRatioSizeBinding,
) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun create(parent: ViewGroup): ShimmerPosterRatioViewHolder {
            return ShimmerPosterRatioViewHolder(
                ShimmerItemMoviePosterRatioSizeBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }
}