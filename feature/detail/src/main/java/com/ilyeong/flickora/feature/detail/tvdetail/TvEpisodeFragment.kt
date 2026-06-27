package com.ilyeong.flickora.feature.detail.tvdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import com.ilyeong.flickora.core.model.TvSeason
import com.ilyeong.flickora.core.ui.common.fragment.BaseFragment
import com.ilyeong.flickora.core.ui.common.listener.ItemClickListener
import com.ilyeong.flickora.feature.detail.adapter.HeaderAdapter
import com.ilyeong.flickora.feature.detail.adapter.TvEpisodeAdapter
import com.ilyeong.flickora.feature.detail.databinding.FragmentTvEpisodeBinding
import com.ilyeong.flickora.feature.detail.model.TvDetailUiState

internal class TvEpisodeFragment : BaseFragment<FragmentTvEpisodeBinding>() {

    override val viewBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentTvEpisodeBinding =
        FragmentTvEpisodeBinding::inflate

    private val viewModel: TvDetailViewModel by viewModels({ requireParentFragment() })

    private val seasonHeaderAdapter = HeaderAdapter(
        ItemClickListener { seasonNumber ->
            viewModel.selectSeason(seasonNumber)
        }
    )
    private val tvEpisodeAdapter = TvEpisodeAdapter()
    private var seasonList: List<TvSeason> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setEpisode()
        observeUiState()
    }

    private fun setEpisode() {
        binding.rvEpisode.adapter = ConcatAdapter(seasonHeaderAdapter, tvEpisodeAdapter)
    }

    private fun observeUiState() {
        repeatOnViewStarted {
            viewModel.uiState.collect { state ->
                when (state) {
                    TvDetailUiState.Loading -> {
                        /* no-op */
                    }

                    is TvDetailUiState.Success -> {
                        bindSeasonList(state)
                    }

                    TvDetailUiState.Failure -> {
                        /* no-op */
                    }
                }
            }
        }
    }

    private fun bindSeasonList(state: TvDetailUiState.Success) {
        seasonList = state.tvSeries.seasonList
        val selectedSeason = seasonList.firstOrNull {
            it.seasonNumber == state.selectedSeasonNumber
        }
        val episodeList = selectedSeason?.episodeList.orEmpty()

        binding.tvEpisodeEmpty.isVisible = episodeList.isEmpty()
        binding.rvEpisode.isVisible = seasonList.isNotEmpty()

        tvEpisodeAdapter.submitList(episodeList)
        setSeasonDropdown(selectedSeason)
    }

    private fun setSeasonDropdown(selectedSeason: TvSeason?) {
        seasonHeaderAdapter.updateSeasonDropdown(
            seasonList = seasonList,
            selectedSeason = selectedSeason
        )
    }
}
