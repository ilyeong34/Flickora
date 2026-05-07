package com.ilyeong.movieverse.feature.genre

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
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.ilyeong.movieverse.core.ui.R
import com.ilyeong.movieverse.core.ui.common.adapter.PosterRatioPagingAdapter
import com.ilyeong.movieverse.core.ui.common.extension.calculateSpanCount
import com.ilyeong.movieverse.core.ui.common.fragment.BaseFragment
import com.ilyeong.movieverse.feature.genre.adapter.ShimmerPosterRatioAdapter
import com.ilyeong.movieverse.feature.genre.databinding.FragmentGenreBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
internal class GenreFragment : BaseFragment<FragmentGenreBinding>() {

    override val viewBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentGenreBinding =
        FragmentGenreBinding::inflate

    private val viewModel: GenreViewModel by viewModels()

    private val genreId: GenreFragmentArgs by navArgs()

    private val genreMovieAdapter = PosterRatioPagingAdapter { movieId ->
        val request = NavDeepLinkRequest.Builder
            .fromUri("android-app://com.ilyeong.movieverse/detail_fragment?movieId=${movieId}".toUri())

        findNavController().navigate(request)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setGenreId(genreId.genreId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbar()
        setGenreMovie()
        setRetryBtn()

        observeUiState()
        observePagingData()
    }

    private fun setToolbar() {
        binding.tb.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setGenreMovie() {
        val spanCount = binding.rvGenreMovie.calculateSpanCount()
        val itemDecoration = object : ItemDecoration() {
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

                val mediumPadding =
                    resources.getDimensionPixelOffset(R.dimen.movieverse_padding_medium)
                val largePadding =
                    resources.getDimensionPixelOffset(R.dimen.movieverse_padding_large)

                if (position < spanCount) outRect.top = largePadding

                outRect.bottom = largePadding

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
        }

        binding.rvGenreMovie.adapter = genreMovieAdapter
        binding.rvGenreMovie.layoutManager = GridLayoutManager(requireContext(), spanCount)
        binding.rvGenreMovie.addItemDecoration(itemDecoration)

        binding.rvShimmer.adapter = ShimmerPosterRatioAdapter(spanCount * 5)
        binding.rvShimmer.layoutManager = GridLayoutManager(requireContext(), spanCount)
        binding.rvShimmer.addItemDecoration(itemDecoration)
    }

    private fun setRetryBtn() {
        binding.ldf.btnRetry.setOnClickListener {
            genreMovieAdapter.retry()
        }
    }

    private fun observeUiState() {
        repeatOnViewStarted {
            viewModel.uiState.collect {
                // 장르 이름을 가져오지 못하면 그냥 보여주지 않는다.
                // 중요한 거는 장르 영화 목록
                binding.tb.title = it.genre.name
            }
        }
    }

    private fun observePagingData() {
        repeatOnViewStarted {
            viewModel.genreMoviePaging.collectLatest {
                genreMovieAdapter.submitData(it)
            }
        }

        repeatOnViewStarted {
            genreMovieAdapter.loadStateFlow.collectLatest {
                when (it.refresh) {
                    is LoadState.Loading -> {
                        binding.sfl.startShimmer()
                        binding.rvShimmer.isVisible = true
                        binding.rvGenreMovie.isVisible = false
                        binding.ldf.root.isVisible = false
                    }

                    is LoadState.NotLoading -> {
                        binding.sfl.stopShimmer()
                        binding.rvShimmer.isVisible = false
                        binding.rvGenreMovie.isVisible = true
                        binding.ldf.root.isVisible = false
                    }

                    is LoadState.Error -> {
                        binding.sfl.stopShimmer()
                        binding.rvShimmer.isVisible = false
                        binding.rvGenreMovie.isVisible = false
                        binding.ldf.root.isVisible = true
                    }
                }
            }
        }
    }
}