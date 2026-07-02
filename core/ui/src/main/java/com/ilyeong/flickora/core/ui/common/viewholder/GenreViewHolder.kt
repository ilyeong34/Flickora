package com.ilyeong.flickora.core.ui.common.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ilyeong.flickora.core.model.Genre
import com.ilyeong.flickora.core.ui.common.listener.ItemClickListener
import com.ilyeong.flickora.core.ui.databinding.ItemMovieGenreChipBinding

class GenreViewHolder private constructor(
    private val binding: ItemMovieGenreChipBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(genre: Genre, itemClickListener: ItemClickListener?) {
        binding.chipGenre.text = genre.name

        binding.root.setOnClickListener {
            itemClickListener?.onItemClick(genre.id)
        }
    }

    companion object {
        fun create(parent: ViewGroup): GenreViewHolder {
            val binding = ItemMovieGenreChipBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return GenreViewHolder(binding)
        }
    }
}
