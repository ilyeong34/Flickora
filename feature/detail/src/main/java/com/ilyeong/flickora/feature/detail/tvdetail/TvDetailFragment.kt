package com.ilyeong.flickora.feature.detail.tvdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil3.load
import coil3.request.crossfade
import com.google.android.material.tabs.TabLayoutMediator
import com.ilyeong.flickora.core.ui.R
import com.ilyeong.flickora.core.ui.common.extension.toMessageResId
import com.ilyeong.flickora.core.ui.common.fragment.BaseFragment
import com.ilyeong.flickora.feature.detail.adapter.TvDetailTabAdapter
import com.ilyeong.flickora.feature.detail.databinding.FragmentTvDetailBinding
import com.ilyeong.flickora.feature.detail.model.DetailEvent
import com.ilyeong.flickora.feature.detail.model.TvDetailUiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TvDetailFragment : BaseFragment<FragmentTvDetailBinding>() {

    override val viewBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentTvDetailBinding =
        FragmentTvDetailBinding::inflate

    private val viewModel: TvDetailViewModel by viewModels()

    private val tvSeriesArgs: TvDetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadData(tvSeriesArgs.tvSeriesId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolBarNavigationIcon()
        setTvWatchlistIcon()
        setTabs()
        setRetryBtn()

        observeUiState()
        observeEvents()
    }

    private fun setToolBarNavigationIcon() {
        binding.tb.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setTvWatchlistIcon() {
        binding.ivWatchlist.setOnClickListener {
            viewModel.addTvToWatchlist()
        }
    }

    private fun setTabs() {
        binding.vpTab.adapter = TvDetailTabAdapter(this)
        binding.vpTab.setUserInputEnabled(false)

        TabLayoutMediator(binding.tl, binding.vpTab) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.information)
                1 -> tab.text = getString(R.string.episode)
                2 -> tab.text = getString(R.string.recommended)
                3 -> tab.text = getString(R.string.review)
            }
        }.attach()
    }

    private fun setRetryBtn() {
        binding.ldf.btnRetry.setOnClickListener {
            viewModel.loadData(tvSeriesArgs.tvSeriesId)
        }
    }

    private fun observeUiState() {
        repeatOnViewStarted {
            viewModel.uiState.collect { state ->
                when (state) {
                    TvDetailUiState.Loading -> {
                        binding.sfl.startShimmer()
                        binding.sfl.isVisible = true
                        binding.content.isVisible = false
                        binding.ldf.root.isVisible = false
                    }

                    is TvDetailUiState.Success -> {
                        binding.sfl.stopShimmer()
                        binding.sfl.isVisible = false
                        binding.content.isVisible = true
                        binding.ldf.root.isVisible = false

                        val tvSeries = state.tvSeries
                        binding.ivBackdrop.load(tvSeries.backdropPath) { crossfade(true) }

                        binding.posterDefault.ivPoster.load(tvSeries.posterPath) {
                            crossfade(true)
                            listener(
                                onStart = { _ ->
                                    binding.posterDefault.tvPosterTitle.text = null
                                },
                                onError = { _, _ ->
                                    binding.posterDefault.tvPosterTitle.text =
                                        tvSeries.name.ifBlank { tvSeries.originalName }
                                }
                            )
                        }

                        binding.tvTvTitle.text = tvSeries.name.ifBlank { tvSeries.originalName }
                        binding.ivWatchlist.isSelected = tvSeries.isInWatchlist
                        binding.rrv.rating = tvSeries.voteAverage
                        binding.rrv.ratingCount = tvSeries.voteCount
                    }

                    TvDetailUiState.Failure -> {
                        binding.sfl.stopShimmer()
                        binding.sfl.isVisible = false
                        binding.content.isVisible = false
                        binding.ldf.root.isVisible = true
                    }
                }
            }
        }
    }

    private fun observeEvents() {
        repeatOnViewStarted {
            viewModel.events.collect {
                when (it) {
                    is DetailEvent.ShowMessage -> {
                        showMessage(getString(it.error.toMessageResId()))
                    }
                }
            }
        }
    }
}
