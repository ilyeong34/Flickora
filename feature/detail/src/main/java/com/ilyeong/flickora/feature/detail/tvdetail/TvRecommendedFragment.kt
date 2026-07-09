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
import com.ilyeong.flickora.core.model.Media
import com.ilyeong.flickora.core.ui.R
import com.ilyeong.flickora.core.ui.common.decoration.PosterFixedItemDecoration
import com.ilyeong.flickora.core.ui.common.fragment.BaseFragment
import com.ilyeong.flickora.feature.detail.adapter.PosterFixedAdapter
import com.ilyeong.flickora.feature.detail.databinding.FragmentTvRecommendedBinding
import com.ilyeong.flickora.feature.detail.model.TvDetailUiState
import kotlinx.coroutines.flow.collectLatest

internal class TvRecommendedFragment : BaseFragment<FragmentTvRecommendedBinding>() {

    override val viewBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentTvRecommendedBinding =
        FragmentTvRecommendedBinding::inflate

    private val viewModel: TvDetailViewModel by viewModels({ requireParentFragment() })

    private val itemClickListener: (Media) -> Unit = { media ->
        val tvSeriesId = media.id
        val request = NavDeepLinkRequest.Builder
            .fromUri("android-app://com.ilyeong.flickora/detail_fragment?tvSeriesId=${tvSeriesId}".toUri())
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
        binding.tvRecommendationSection.text = getString(R.string.media_section_recommendation)
    }

    private fun setSimilar() {
        binding.rvSimilar.adapter = similarAdapter
        binding.rvSimilar.addItemDecoration(PosterFixedItemDecoration)
        binding.tvSimilarSection.text = getString(R.string.media_section_similar)
    }

    private fun observeRecommendationContent() {
        repeatOnViewStarted {
            viewModel.uiState.collectLatest { state ->
                when (state) {
                    is TvDetailUiState.Success -> {
                        val recommendationPreviewList = state.recommendationList
                        recommendationAdapter.submitList(
                            recommendationPreviewList.map { it as Media }
                        )
                        binding.tvRecommendationSection.isVisible =
                            recommendationPreviewList.isNotEmpty()
                        binding.rvRecommendation.isVisible = recommendationPreviewList.isNotEmpty()

                        val similarPreviewList = state.similarList
                        similarAdapter.submitList(similarPreviewList.map { it as Media })
                        binding.tvSimilarSection.isVisible = similarPreviewList.isNotEmpty()
                        binding.rvSimilar.isVisible = similarPreviewList.isNotEmpty()

                        updateEmptyState()
                    }

                    else -> {
                        recommendationAdapter.submitList(emptyList())
                        similarAdapter.submitList(emptyList())
                        binding.tvRecommendationSection.isVisible = false
                        binding.rvRecommendation.isVisible = false
                        binding.tvSimilarSection.isVisible = false
                        binding.rvSimilar.isVisible = false
                        updateEmptyState()
                    }
                }
            }
        }
    }

    private fun updateEmptyState() {
        val hasAnyContent = binding.rvRecommendation.isVisible || binding.rvSimilar.isVisible
        binding.tvRecommendationEmpty.isVisible = !hasAnyContent
    }
}
