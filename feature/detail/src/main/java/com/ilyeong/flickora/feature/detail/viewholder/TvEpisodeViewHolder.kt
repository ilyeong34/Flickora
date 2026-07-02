package com.ilyeong.flickora.feature.detail.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.crossfade
import com.ilyeong.flickora.core.model.TvEpisode
import com.ilyeong.flickora.core.ui.R
import com.ilyeong.flickora.feature.detail.databinding.ItemTvEpisodeBinding

class TvEpisodeViewHolder private constructor(
    private val binding: ItemTvEpisodeBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(episode: TvEpisode) {
        binding.ivEpisodeStill.load(episode.stillPath) {
            crossfade(true)
            listener(
                onStart = { _ -> binding.tvEpisodeName1.text = null },
                onError = { _, _ -> binding.tvEpisodeName1.text = episode.name }
            )
        }

        binding.tvEpisodeName2.text = "${episode.episodeNumber}. ${episode.name}"
        binding.tvEpisodeRuntime.text = when (episode.runtime == 0) {
            true -> binding.root.context.getString(R.string.runtime_empty)
            false -> binding.root.context.getString(R.string.episode_runtime_short, episode.runtime)
        }
        binding.tvEpisodeOverview.text = episode.overview.ifBlank {
            binding.root.context.getString(R.string.info_empty)
        }
    }

    companion object {
        fun create(parent: ViewGroup): TvEpisodeViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemTvEpisodeBinding.inflate(layoutInflater, parent, false)
            return TvEpisodeViewHolder(binding)
        }
    }
}
