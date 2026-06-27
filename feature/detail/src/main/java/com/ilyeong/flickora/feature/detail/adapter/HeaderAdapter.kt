package com.ilyeong.flickora.feature.detail.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.ilyeong.flickora.core.model.TvSeason
import com.ilyeong.flickora.core.ui.common.listener.ItemClickListener
import com.ilyeong.flickora.feature.detail.viewholder.TvSeasonDropdownViewHolder

internal class HeaderAdapter(
    private val itemClickListener: ItemClickListener
) : Adapter<TvSeasonDropdownViewHolder>() {
    private var seasonList: List<TvSeason> = emptyList()
    private var selectedSeason: TvSeason? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = TvSeasonDropdownViewHolder.create(parent)

    override fun onBindViewHolder(
        holder: TvSeasonDropdownViewHolder,
        position: Int
    ) {
        holder.bind(
            seasonList = seasonList,
            selectedSeason = selectedSeason,
            itemClickListener = itemClickListener
        )
    }

    override fun getItemCount() = when (seasonList.isEmpty()) {
        true -> 0
        false -> 1
    }

    fun updateSeasonDropdown(
        seasonList: List<TvSeason>,
        selectedSeason: TvSeason?
    ) {
        val wasVisible = this.seasonList.isNotEmpty()
        val isVisible = seasonList.isNotEmpty()

        this.seasonList = seasonList
        this.selectedSeason = selectedSeason

        when {
            wasVisible && !isVisible -> notifyItemRemoved(0)
            !wasVisible && isVisible -> notifyItemInserted(0)
            isVisible -> notifyItemChanged(0)
        }
    }
}
