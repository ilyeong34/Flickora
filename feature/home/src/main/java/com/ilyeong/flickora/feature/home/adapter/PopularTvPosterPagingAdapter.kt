package com.ilyeong.flickora.feature.home.adapter

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.ilyeong.flickora.core.model.TvSeries
import com.ilyeong.flickora.feature.home.viewholder.PopularTvPosterViewHolder

internal class PopularTvPosterPagingAdapter :
    PagingDataAdapter<TvSeries, PopularTvPosterViewHolder>(PopularTvPosterDiffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = PopularTvPosterViewHolder.create(parent)

    override fun onBindViewHolder(
        holder: PopularTvPosterViewHolder,
        position: Int
    ) {
        val item = getItem(position) ?: return
        holder.bind(item)
    }
}
