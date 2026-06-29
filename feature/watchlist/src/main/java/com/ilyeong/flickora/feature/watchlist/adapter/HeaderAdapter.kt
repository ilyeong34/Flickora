package com.ilyeong.flickora.feature.watchlist.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.ilyeong.flickora.feature.watchlist.model.WatchlistMediaType
import com.ilyeong.flickora.feature.watchlist.viewholder.HeaderViewHolder

internal class HeaderAdapter(
    private val onMediaTypeClick: (WatchlistMediaType) -> Unit
) : Adapter<HeaderViewHolder>() {
    private val mediaTypeList = WatchlistMediaType.entries.toList()
    private var selectedMediaType = WatchlistMediaType.MOVIE

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = HeaderViewHolder.create(parent)

    override fun onBindViewHolder(
        holder: HeaderViewHolder,
        position: Int
    ) {
        holder.bind(mediaTypeList, selectedMediaType, onMediaTypeClick)
    }

    override fun getItemCount() = 1

    fun updateSelectedMediaType(selectedMediaType: WatchlistMediaType) {
        if (this.selectedMediaType == selectedMediaType) return

        this.selectedMediaType = selectedMediaType
        notifyItemChanged(0)
    }
}
