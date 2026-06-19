package com.ilyeong.flickora.feature.search.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ilyeong.flickora.feature.search.databinding.ItemMovieSectionHeaderBinding

internal class HeaderViewHolder private constructor(
    private val binding: ItemMovieSectionHeaderBinding
) : ViewHolder(binding.root) {

    fun bind(title: String) {
        binding.tvTitle.text = title
    }

    companion object {
        fun create(parent: ViewGroup) = HeaderViewHolder(
            ItemMovieSectionHeaderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }
}