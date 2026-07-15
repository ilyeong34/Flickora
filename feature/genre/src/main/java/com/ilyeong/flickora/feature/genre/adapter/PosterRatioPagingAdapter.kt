package com.ilyeong.flickora.feature.genre.adapter

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.ilyeong.flickora.core.model.Media
import com.ilyeong.flickora.core.model.Movie
import com.ilyeong.flickora.core.ui.common.diffutil.MovieDiffUtil
import com.ilyeong.flickora.core.ui.common.listener.ItemClickListener
import com.ilyeong.flickora.core.ui.common.viewholder.PosterRatioViewHolder

internal class PosterRatioPagingAdapter(
    private val itemClickListener: ItemClickListener<Media>
) : PagingDataAdapter<Movie, PosterRatioViewHolder>(MovieDiffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = PosterRatioViewHolder.create(parent)

    override fun onBindViewHolder(
        holder: PosterRatioViewHolder,
        position: Int
    ) {
        val item = getItem(position) ?: return
        holder.bind(item, itemClickListener)
    }
}
