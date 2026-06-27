package com.ilyeong.flickora.feature.detail.tvdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.paging.LoadState
import com.ilyeong.flickora.core.ui.common.fragment.BaseFragment
import com.ilyeong.flickora.feature.detail.adapter.ReviewAdapter
import com.ilyeong.flickora.feature.detail.databinding.FragmentTvReviewBinding
import kotlinx.coroutines.flow.collectLatest

internal class TvReviewFragment : BaseFragment<FragmentTvReviewBinding>() {

    override val viewBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentTvReviewBinding =
        FragmentTvReviewBinding::inflate

    private val viewModel: TvDetailViewModel by viewModels({ requireParentFragment() })

    private val reviewAdapter = ReviewAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setReview()
        setRetryBtn()
        observeReview()
    }

    private fun setReview() {
        binding.rvReview.adapter = reviewAdapter
    }

    private fun setRetryBtn() {
        binding.ldf.btnRetry.setOnClickListener {
            reviewAdapter.retry()
        }
    }

    private fun observeReview() {
        repeatOnViewStarted {
            viewModel.reviewPaging.collectLatest {
                reviewAdapter.submitData(it)
            }
        }

        repeatOnViewStarted {
            reviewAdapter.loadStateFlow.collectLatest { loadStates ->
                when (loadStates.refresh) {
                    is LoadState.Loading -> {
                        binding.sfl.startShimmer()
                        binding.sfl.isVisible = true
                        binding.rvReview.isVisible = false
                        binding.tvReviewEmpty.isVisible = false
                        binding.ldf.root.isVisible = false
                    }

                    is LoadState.NotLoading -> {
                        binding.sfl.stopShimmer()
                        binding.sfl.isVisible = false
                        binding.rvReview.isVisible = true
                        binding.tvReviewEmpty.isVisible = (reviewAdapter.itemCount == 0)
                        binding.ldf.root.isVisible = false
                    }

                    is LoadState.Error -> {
                        binding.sfl.stopShimmer()
                        binding.sfl.isVisible = false
                        binding.rvReview.isVisible = false
                        binding.tvReviewEmpty.isVisible = false
                        binding.ldf.root.isVisible = true
                    }
                }
            }
        }
    }
}
