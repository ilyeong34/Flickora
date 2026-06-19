package com.ilyeong.flickora.feature.detail.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil3.load
import coil3.request.crossfade
import coil3.request.error
import com.ilyeong.flickora.core.model.Cast
import com.ilyeong.flickora.core.ui.R
import com.ilyeong.flickora.feature.detail.databinding.ItemMovieCastBinding

class CastViewHolder private constructor(
    private val binding: ItemMovieCastBinding
) : ViewHolder(binding.root) {

    fun bind(cast: Cast) {
        binding.ivCast.load(cast.profilePath) {
            crossfade(true)
            error(R.drawable.ic_profile_filled_gray_24)
        }
        binding.tvName.text = cast.name
        binding.tvCharacter.text = when (cast.character.isBlank()) {
            true -> binding.root.context.getString(R.string.character, "??")
            false -> binding.root.context.getString(R.string.character, cast.character)
        }
    }

    companion object {
        fun create(parent: ViewGroup): CastViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemMovieCastBinding.inflate(layoutInflater, parent, false)
            return CastViewHolder(binding)
        }
    }
}