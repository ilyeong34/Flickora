package com.ilyeong.movieverse.feature.search.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.ilyeong.movieverse.feature.search.viewholder.HeaderViewHolder

internal class HeaderAdapter : Adapter<HeaderViewHolder>() {
    private var title: String = ""

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = HeaderViewHolder.create(parent)

    override fun onBindViewHolder(
        holder: HeaderViewHolder,
        position: Int
    ) {
        holder.bind(title)
    }

    override fun getItemCount() = 1
    fun updateHeaderTitle(title: String?) {
        if (this.title == title) return
        this.title = title ?: ""

        notifyItemChanged(0)
    }
}