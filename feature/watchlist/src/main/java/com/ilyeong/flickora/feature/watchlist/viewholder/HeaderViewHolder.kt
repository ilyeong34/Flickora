package com.ilyeong.flickora.feature.watchlist.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ilyeong.flickora.core.ui.common.listener.ItemClickListener
import com.ilyeong.flickora.feature.watchlist.databinding.ItemWatchlistMediaTypeHeaderBinding
import com.ilyeong.flickora.feature.watchlist.model.WatchlistMediaType

internal class HeaderViewHolder private constructor(
    private val binding: ItemWatchlistMediaTypeHeaderBinding
) : ViewHolder(binding.root) {

    fun bind(
        selectedMediaType: WatchlistMediaType,
        itemClickListener: ItemClickListener
    ) {
        binding.chipMovie.text = binding.root.context.getString(WatchlistMediaType.MOVIE.labelRes)
        binding.chipTvSeries.text =
            binding.root.context.getString(WatchlistMediaType.TV_SERIES.labelRes)

        binding.chipMovie.isSelected = selectedMediaType == WatchlistMediaType.MOVIE
        binding.chipTvSeries.isSelected = selectedMediaType == WatchlistMediaType.TV_SERIES

        binding.chipMovie.setOnClickListener {
            itemClickListener.onItemClick(WatchlistMediaType.MOVIE.value)
        }
        binding.chipTvSeries.setOnClickListener {
            itemClickListener.onItemClick(WatchlistMediaType.TV_SERIES.value)
        }
    }

    companion object {
        fun create(parent: ViewGroup) = HeaderViewHolder(
            ItemWatchlistMediaTypeHeaderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }
}
