package com.ilyeong.movieverse.feature.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.paging.LoadState
import com.ilyeong.movieverse.core.ui.common.fragment.BaseFragment
import com.ilyeong.movieverse.feature.detail.adapter.ReviewAdapter
import com.ilyeong.movieverse.feature.detail.databinding.FragmentReviewBinding
import kotlinx.coroutines.flow.collectLatest

internal class ReviewFragment : BaseFragment<FragmentReviewBinding>() {

    override val viewBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentReviewBinding =
        FragmentReviewBinding::inflate

    private val viewModel: DetailViewModel by viewModels({ requireParentFragment() })

    private val reviewAdapter = ReviewAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRetryBtn()
        observePagingData()
    }

    private fun setRetryBtn() {
        binding.ldf.btnRetry.setOnClickListener {
            reviewAdapter.retry()
        }
    }

    private fun observePagingData() {
        binding.rvReview.adapter = reviewAdapter

        repeatOnViewStarted {
            viewModel.reviewPaging.collectLatest {
                reviewAdapter.submitData(it)
            }
        }

        repeatOnViewStarted {
            reviewAdapter.loadStateFlow.collectLatest {
                when (it.refresh) {
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