package com.ilyeong.flickora.feature.detail.tvdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.ilyeong.flickora.core.ui.R
import com.ilyeong.flickora.core.ui.common.adapter.GenreAdapter
import com.ilyeong.flickora.core.ui.common.decoration.PosterFixedItemDecoration
import com.ilyeong.flickora.core.ui.common.fragment.BaseFragment
import com.ilyeong.flickora.feature.detail.adapter.CastAdapter
import com.ilyeong.flickora.feature.detail.databinding.FragmentTvInformationBinding
import com.ilyeong.flickora.feature.detail.tvdetail.model.TvDetailUiState
import kotlinx.coroutines.flow.collectLatest

internal class TvInformationFragment : BaseFragment<FragmentTvInformationBinding>() {

    override val viewBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentTvInformationBinding =
        FragmentTvInformationBinding::inflate

    private val viewModel: TvDetailViewModel by viewModels({ requireParentFragment() })

    private val castAdapter = CastAdapter()
    private val genreAdapter = GenreAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setCast()
        setGenre()
        observeCastPreview()
        observeUiState()
    }

    private fun setCast() {
        binding.rvTvCast.adapter = castAdapter
        binding.rvTvCast.addItemDecoration(PosterFixedItemDecoration)
    }

    private fun setGenre() {
        binding.rvTvGenre.adapter = genreAdapter
        binding.rvTvGenre.addItemDecoration(PosterFixedItemDecoration)
    }

    private fun observeCastPreview() {
        repeatOnViewStarted {
            viewModel.castPreviewList.collectLatest { castList ->
                val castPreviewList = castList.take(10)
                castAdapter.submitList(castPreviewList)
                binding.rvTvCast.isVisible = castPreviewList.isNotEmpty()
                binding.tvTvCastEmpty.isVisible = castPreviewList.isEmpty()
            }
        }
    }

    private fun observeUiState() {
        repeatOnViewStarted {
            viewModel.uiState.collect { state ->
                when (state) {
                    TvDetailUiState.Loading -> {
                        /* no-op */
                    }

                    is TvDetailUiState.Success -> {
                        val tvSeries = state.tvSeries

                        binding.tvOverview.text = when (tvSeries.overview.isBlank()) {
                            true -> getString(R.string.info_empty)
                            false -> tvSeries.overview
                        }

                        genreAdapter.submitList(tvSeries.genreList)
                        binding.rvTvGenre.isVisible = tvSeries.genreList.isNotEmpty()
                        binding.tvTvGenreEmpty.isVisible = tvSeries.genreList.isEmpty()

                        binding.tvFirstAirDate.text = when (tvSeries.firstAirDate.isBlank()) {
                            true -> getString(R.string.first_air_date_empty)
                            false -> getString(R.string.first_air_date, tvSeries.firstAirDate)
                        }

                        binding.tvLastAirDate.text = when (tvSeries.lastAirDate.isBlank()) {
                            true -> getString(R.string.last_air_date_empty)
                            false -> getString(R.string.last_air_date, tvSeries.lastAirDate)
                        }

                        binding.tvStatus.text = when (tvSeries.status.isBlank()) {
                            true -> getString(R.string.status_empty)
                            false -> getString(R.string.status, tvSeries.status)
                        }

                        binding.tvSeasonCount.text = when (tvSeries.numberOfSeasons == 0) {
                            true -> getString(R.string.season_count_empty)
                            false -> getString(R.string.season_count, tvSeries.numberOfSeasons)
                        }

                        binding.tvAudio.text = when (tvSeries.spokenLanguageList.isEmpty()) {
                            true -> getString(R.string.audio_empty)
                            false -> getString(
                                R.string.audio,
                                tvSeries.spokenLanguageList.joinToString {
                                    it.englishName.ifBlank { it.name }
                                }
                            )
                        }

                        binding.tvEpisodeRuntime.text = when (tvSeries.episodeRunTime.isEmpty()) {
                            true -> getString(R.string.episode_runtime_empty)
                            false -> getString(
                                R.string.episode_runtime,
                                tvSeries.episodeRunTime.joinToString { "${it}m" }
                            )
                        }
                    }

                    TvDetailUiState.Failure -> {
                        /* no-op */
                    }
                }
            }
        }
    }
}
