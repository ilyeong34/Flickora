package com.ilyeong.flickora.feature.detail.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ilyeong.flickora.core.model.TvSeason
import com.ilyeong.flickora.core.ui.R
import com.ilyeong.flickora.core.ui.common.listener.ItemClickListener
import com.ilyeong.flickora.feature.detail.databinding.ItemTvSeasonDropdownBinding

internal class TvSeasonDropdownViewHolder private constructor(
    private val binding: ItemTvSeasonDropdownBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(
        seasonList: List<TvSeason>,
        selectedSeason: TvSeason?,
        itemClickListener: ItemClickListener<Int>
    ) {
        binding.actvSeason.setAdapter(
            ArrayAdapter(
                binding.root.context,
                R.layout.item_dropdown,
                seasonList.map(TvSeason::name)
            )
        )
        binding.actvSeason.setOnItemClickListener { _, _, position, _ ->
            seasonList.getOrNull(position)?.let { season ->
                itemClickListener.onItemClick(season.seasonNumber)
            }
        }

        val selectedSeasonName = selectedSeason?.name.orEmpty()
        if (binding.actvSeason.text.toString() != selectedSeasonName) {
            binding.actvSeason.setText(selectedSeasonName, false)
        }
    }

    companion object {
        fun create(parent: ViewGroup) = TvSeasonDropdownViewHolder(
            ItemTvSeasonDropdownBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }
}
