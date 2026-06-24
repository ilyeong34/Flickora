package com.ilyeong.flickora.feature.home.adapter

import androidx.recyclerview.widget.DiffUtil
import com.ilyeong.flickora.core.model.TvSeries

internal object PopularTvPosterDiffUtil : DiffUtil.ItemCallback<TvSeries>() {
    override fun areItemsTheSame(
        oldItem: TvSeries,
        newItem: TvSeries
    ) = oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: TvSeries,
        newItem: TvSeries
    ) = oldItem == newItem
}
