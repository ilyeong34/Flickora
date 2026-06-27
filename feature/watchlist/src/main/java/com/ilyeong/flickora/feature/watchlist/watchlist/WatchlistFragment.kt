package com.ilyeong.flickora.feature.watchlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.ConcatAdapter
import com.ilyeong.flickora.core.ui.common.decoration.PosterDescriptionItemDecoration
import com.ilyeong.flickora.core.ui.common.fragment.BaseFragment
import com.ilyeong.flickora.core.ui.common.listener.ItemClickListener
import com.ilyeong.flickora.feature.watchlist.adapter.PosterDescriptionPagingAdapter
import com.ilyeong.flickora.feature.watchlist.adapter.HeaderAdapter
import com.ilyeong.flickora.feature.watchlist.databinding.FragmentWatchlistBinding
import com.ilyeong.flickora.feature.watchlist.model.WatchlistMediaType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
internal class WatchlistFragment : BaseFragment<FragmentWatchlistBinding>() {

    override val viewBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentWatchlistBinding
        get() = FragmentWatchlistBinding::inflate

    private val viewModel: WatchlistViewModel by viewModels()

    private val mediaTypeClickListener = ItemClickListener { mediaTypeId ->
        viewModel.setSelectedMediaType(WatchlistMediaType.fromValue(mediaTypeId))
    }

    private val watchlistItemClickListener = ItemClickListener { itemId ->
        when (viewModel.selectedMediaType.value) {
            WatchlistMediaType.MOVIE -> navigateToMovieDetail(itemId)
            WatchlistMediaType.TV_SERIES -> navigateToTvDetail(itemId)
        }
    }

    private val headerAdapter = HeaderAdapter(mediaTypeClickListener)
    private val watchlistAdapter = PosterDescriptionPagingAdapter(watchlistItemClickListener)

    private val watchlistAdapterWithHeader = ConcatAdapter(headerAdapter, watchlistAdapter)

    private fun navigateToMovieDetail(movieId: Int) {
        val request = NavDeepLinkRequest.Builder
            .fromUri("android-app://com.ilyeong.flickora/detail_fragment?movieId=${movieId}".toUri())
            .build()

        findNavController().navigate(request)
    }

    private fun navigateToTvDetail(tvSeriesId: Int) {
        val request = NavDeepLinkRequest.Builder
            .fromUri("android-app://com.ilyeong.flickora/detail_fragment?tvSeriesId=${tvSeriesId}".toUri())
            .build()

        findNavController().navigate(request)
    }

    private var shouldRefresh = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setWatchlist()
        setRetryBtn()

        observeWatchlist()

        refreshData()
    }

    private fun setWatchlist() {
        binding.rvWatchlist.adapter = watchlistAdapterWithHeader
        binding.rvWatchlist.addItemDecoration(PosterDescriptionItemDecoration)
    }

    private fun setRetryBtn() {
        binding.ldf.btnRetry.setOnClickListener {
            watchlistAdapter.retry()
        }
    }

    private fun observeWatchlist() {
        repeatOnViewStarted {
            viewModel.selectedMediaType.collectLatest { selectedMediaType ->
                headerAdapter.updateSelectedMediaType(selectedMediaType)
            }
        }

        repeatOnViewStarted {
            viewModel.watchlistPaging.collectLatest {
                watchlistAdapter.submitData(it)
            }
        }

        repeatOnViewStarted {
            watchlistAdapter.loadStateFlow.collectLatest {
                // Call refresh only once on the initial creation.
                if (shouldRefresh) {
                    refreshData()
                    shouldRefresh = false
                }

                when (it.refresh) {
                    is LoadState.Loading -> {
                        // Show shimmer only during the initial loading state.
                        if (watchlistAdapter.itemCount == 0) {
                            binding.sfl.startShimmer()
                            binding.sfl.isVisible = true
                            binding.content.isVisible = false
                            binding.ldf.root.isVisible = false
                        }
                    }

                    is LoadState.NotLoading -> {
                        binding.sfl.stopShimmer()
                        binding.sfl.isVisible = false
                        binding.content.isVisible = true
                        binding.ldf.root.isVisible = false

                        binding.tvWatchlistEmpty.isVisible = (watchlistAdapter.itemCount == 0)
                    }

                    is LoadState.Error -> {
                        binding.sfl.stopShimmer()
                        binding.sfl.isVisible = false
                        binding.content.isVisible = false
                        binding.ldf.root.isVisible = true
                    }
                }
            }
        }
    }

    private fun refreshData() {
        watchlistAdapter.refresh()
    }
}
