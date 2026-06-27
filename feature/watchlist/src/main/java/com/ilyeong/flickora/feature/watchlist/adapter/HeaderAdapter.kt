package com.ilyeong.flickora.feature.watchlist.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.ilyeong.flickora.core.ui.common.listener.ItemClickListener
import com.ilyeong.flickora.feature.watchlist.model.WatchlistMediaType
import com.ilyeong.flickora.feature.watchlist.viewholder.HeaderViewHolder

internal class HeaderAdapter(
    private val itemClickListener: ItemClickListener
) : Adapter<HeaderViewHolder>() {
    private var selectedMediaType = WatchlistMediaType.MOVIE

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = HeaderViewHolder.create(parent)

    override fun onBindViewHolder(
        holder: HeaderViewHolder,
        position: Int
    ) {
        holder.bind(selectedMediaType, itemClickListener)
    }

    override fun getItemCount() = 1

    fun updateSelectedMediaType(selectedMediaType: WatchlistMediaType) {
        if (this.selectedMediaType == selectedMediaType) return

        this.selectedMediaType = selectedMediaType
        notifyItemChanged(0)
    }
}
