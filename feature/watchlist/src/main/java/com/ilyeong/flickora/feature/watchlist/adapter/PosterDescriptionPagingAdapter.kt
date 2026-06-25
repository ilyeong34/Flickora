package com.ilyeong.flickora.feature.watchlist.adapter

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.ilyeong.flickora.core.ui.common.diffutil.PosterUiModelDiffUtil
import com.ilyeong.flickora.core.ui.common.listener.ItemClickListener
import com.ilyeong.flickora.core.ui.common.model.PosterUiModel
import com.ilyeong.flickora.core.ui.common.viewholder.PosterDescriptionViewHolder

internal class PosterDescriptionPagingAdapter(
    private val itemClickListener: ItemClickListener
) : PagingDataAdapter<PosterUiModel, PosterDescriptionViewHolder>(PosterUiModelDiffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = PosterDescriptionViewHolder.create(parent)

    override fun onBindViewHolder(
        holder: PosterDescriptionViewHolder,
        position: Int
    ) {
        val item = getItem(position) ?: return
        holder.bind(item, itemClickListener)
    }

}
