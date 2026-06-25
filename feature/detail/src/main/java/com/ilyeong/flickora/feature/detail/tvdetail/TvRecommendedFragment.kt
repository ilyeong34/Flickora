package com.ilyeong.flickora.feature.detail.tvdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.findNavController
import com.ilyeong.flickora.core.ui.R
import com.ilyeong.flickora.core.ui.common.decoration.PosterFixedItemDecoration
import com.ilyeong.flickora.core.ui.common.fragment.BaseFragment
import com.ilyeong.flickora.core.ui.common.listener.ItemClickListener
import com.ilyeong.flickora.core.ui.common.model.toPosterUiModel
import com.ilyeong.flickora.feature.detail.adapter.PosterFixedAdapter
import com.ilyeong.flickora.feature.detail.databinding.FragmentTvRecommendedBinding
import kotlinx.coroutines.flow.collectLatest

internal class TvRecommendedFragment : BaseFragment<FragmentTvRecommendedBinding>() {

    override val viewBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentTvRecommendedBinding =
        FragmentTvRecommendedBinding::inflate

    private val viewModel: TvDetailViewModel by viewModels({ requireParentFragment() })

    private val itemClickListener = ItemClickListener { tvSeriesId ->
        val request = NavDeepLinkRequest.Builder
            .fromUri("android-app://com.ilyeong.flickora/tv_detail_fragment?tvSeriesId=${tvSeriesId}".toUri())
            .build()

        findNavController().navigate(request)
    }

    private val recommendationAdapter = PosterFixedAdapter(itemClickListener)
    private val similarAdapter = PosterFixedAdapter(itemClickListener)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRecommendation()
        setSimilar()
        observeRecommendationContent()
    }

    private fun setRecommendation() {
        binding.rvRecommendation.adapter = recommendationAdapter
        binding.rvRecommendation.addItemDecoration(PosterFixedItemDecoration)
    }

    private fun setSimilar() {
        binding.rvSimilar.adapter = similarAdapter
        binding.rvSimilar.addItemDecoration(PosterFixedItemDecoration)
    }

    private fun observeRecommendationContent() {
        repeatOnViewStarted {
            viewModel.recommendationList.collectLatest { recommendationList ->
                val previewList = recommendationList.take(10)
                recommendationAdapter.submitList(previewList.map { it.toPosterUiModel() })
                binding.tvRecommendationSection.isVisible = previewList.isNotEmpty()
                binding.rvRecommendation.isVisible = previewList.isNotEmpty()
                updateEmptyState()
            }
        }

        repeatOnViewStarted {
            viewModel.similarList.collectLatest { similarList ->
                val previewList = similarList.take(10)
                similarAdapter.submitList(previewList.map { it.toPosterUiModel() })
                binding.tvSimilarSection.isVisible = previewList.isNotEmpty()
                binding.rvSimilar.isVisible = previewList.isNotEmpty()
                updateEmptyState()
            }
        }
    }

    private fun updateEmptyState() {
        val hasAnyContent = binding.rvRecommendation.isVisible || binding.rvSimilar.isVisible
        binding.tvRecommendationEmpty.isVisible = !hasAnyContent
    }
}
