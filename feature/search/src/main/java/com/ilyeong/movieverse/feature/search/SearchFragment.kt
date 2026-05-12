package com.ilyeong.movieverse.feature.search

import android.graphics.Rect
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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.ilyeong.movieverse.core.ui.R
import com.ilyeong.movieverse.core.ui.common.adapter.PosterRatioPagingAdapter
import com.ilyeong.movieverse.core.ui.common.decoration.PosterDescriptionItemDecoration
import com.ilyeong.movieverse.core.ui.common.extension.calculateSpanCount
import com.ilyeong.movieverse.core.ui.common.extension.getQueryFlow
import com.ilyeong.movieverse.core.ui.common.fragment.BaseFragment
import com.ilyeong.movieverse.core.ui.common.listener.ItemClickListener
import com.ilyeong.movieverse.feature.search.adapter.HeaderAdapter
import com.ilyeong.movieverse.feature.search.adapter.PosterDescriptionAdapter
import com.ilyeong.movieverse.feature.search.databinding.FragmentSearchBinding
import com.ilyeong.movieverse.feature.search.model.TrendState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged

@AndroidEntryPoint
internal class SearchFragment : BaseFragment<FragmentSearchBinding>() {

    override val viewBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSearchBinding =
        FragmentSearchBinding::inflate

    private val viewModel: SearchViewModel by viewModels()

    val itemClickListener = ItemClickListener { movieId ->
        val request = NavDeepLinkRequest.Builder
            .fromUri("android-app://com.ilyeong.movieverse/detail_fragment?movieId=${movieId}".toUri())
            .build()

        findNavController().navigate(request)
    }

    private val trendHeaderAdapter = HeaderAdapter()
    private val posterDescriptionAdapter = PosterDescriptionAdapter(itemClickListener)

    private val searchHeaderAdapter = HeaderAdapter()
    private val searchAdapter = PosterRatioPagingAdapter(itemClickListener)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarNavigationIcon()
        setSearchView()
        setTrend()
        setSearch()
        setRetryBtn()

        observeUiState()
        observePagingData()
    }

    private fun setToolbarNavigationIcon() {
        binding.tb.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    @OptIn(FlowPreview::class)
    private fun setSearchView() {
        repeatOnViewStarted {
            binding.sv.getQueryFlow()
                .debounce(500L)
                .distinctUntilChanged()
                .collectLatest {
                    viewModel.setQuery(it)
                }
        }
    }

    private fun setTrend() {
        binding.rvTrend.adapter = ConcatAdapter(trendHeaderAdapter, posterDescriptionAdapter)
        binding.rvTrend.addItemDecoration(PosterDescriptionItemDecoration)
    }

    private fun setSearch() {
        val spanCount = binding.rvSearch.calculateSpanCount()

        binding.rvSearch.adapter = ConcatAdapter(searchHeaderAdapter, searchAdapter)
        binding.rvSearch.layoutManager = GridLayoutManager(requireContext(), spanCount)
            .apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int) = if (position == 0) spanCount else 1
                }
            }

        binding.rvSearch.addItemDecoration(object : ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                val position = parent.getChildAdapterPosition(view)
                if (position == RecyclerView.NO_POSITION) return

                val layoutManager = parent.layoutManager as? GridLayoutManager ?: return
                val spanSizeLookup = layoutManager.spanSizeLookup
                val spanCount = layoutManager.spanCount
                val spanIndex = spanSizeLookup.getSpanIndex(position, spanCount)

                val resources = parent.context.resources
                val mediumPadding =
                    resources.getDimensionPixelOffset(R.dimen.movieverse_padding_medium)
                val largePadding =
                    resources.getDimensionPixelOffset(R.dimen.movieverse_padding_large)

                outRect.bottom = largePadding

                if (position == 0) {
                    outRect.top = largePadding
                    return
                }

                val third = (mediumPadding / 3f).toInt()
                when (spanIndex) {
                    0 -> {
                        outRect.right = third * 2
                    }

                    spanCount - 1 -> {
                        outRect.left = third * 2
                    }

                    else -> {
                        outRect.left = third
                        outRect.right = third
                    }
                }
            }

        })
    }

    private fun setRetryBtn() {
        binding.ldf.btnRetry.setOnClickListener {
            searchAdapter.retry()
        }
    }

    private fun observeUiState() {
        repeatOnViewStarted {
            viewModel.uiState.collectLatest { uiState ->
                val query = uiState.query.trim()
                val isSearchMode = query.isNotBlank()

                if (isSearchMode) {
                    // 검색 모드에서는 trend 계층을 절대 보여주지 않는다
                    binding.lpb.isVisible = false
                    binding.tv.isVisible = false
                    binding.rvTrend.isVisible = false

                    return@collectLatest
                }

                // 여기 아래는 query blank일 때만 실행
                binding.rvSearch.isVisible = false
                binding.ldf.root.isVisible = false
                searchHeaderAdapter.updateHeaderTitle(null)

                when (uiState.trendState) {
                    is TrendState.Loading -> {
                        binding.lpb.isVisible = true
                        binding.tv.isVisible = false
                        binding.rvTrend.isVisible = false
                    }

                    is TrendState.Failure -> {
                        binding.lpb.isVisible = false
                        binding.tv.isVisible = true
                        binding.rvTrend.isVisible = false
                    }

                    is TrendState.Success -> {
                        binding.lpb.isVisible = false
                        binding.tv.isVisible = false
                        binding.rvTrend.isVisible = true

                        trendHeaderAdapter.updateHeaderTitle(
                            getString(R.string.movie_section_trending_day)
                        )
                        posterDescriptionAdapter.submitList(uiState.trendState.movieList)
                    }
                }
            }
        }
    }

    private fun observePagingData() {
        repeatOnViewStarted {
            viewModel.searchMoviePaging.collectLatest {
                searchAdapter.submitData(it)
            }
        }

        repeatOnViewStarted {
            searchAdapter.loadStateFlow.collectLatest { loadState ->
                val query = viewModel.uiState.value.query.trim()

                if (query.isBlank()) {
                    binding.rvSearch.isVisible = false
                    binding.ldf.root.isVisible = false
                    searchHeaderAdapter.updateHeaderTitle(null)
                    return@collectLatest
                }

                val refresh = loadState.refresh

                when (refresh) {
                    is LoadState.Loading -> {
                        binding.rvSearch.isVisible = true
                        binding.ldf.root.isVisible = false
                    }

                    is LoadState.Error -> {
                        binding.rvSearch.isVisible = false
                        binding.ldf.root.isVisible = true
                    }

                    is LoadState.NotLoading -> {
                        binding.rvSearch.isVisible = true
                        binding.ldf.root.isVisible = false

                        searchHeaderAdapter.updateHeaderTitle(
                            when (searchAdapter.itemCount == 0) {
                                true -> getString(R.string.search_result_empty)
                                false -> getString(R.string.search_result)
                            }
                        )
                    }
                }
            }
        }
    }
}
