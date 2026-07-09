package com.ilyeong.flickora.feature.detail.movie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ilyeong.flickora.core.model.Media
import com.ilyeong.flickora.core.ui.R
import com.ilyeong.flickora.core.ui.common.decoration.PosterFixedItemDecoration
import com.ilyeong.flickora.core.ui.common.fragment.BaseFragment
import com.ilyeong.flickora.feature.detail.adapter.PosterFixedAdapter
import com.ilyeong.flickora.feature.detail.databinding.FragmentRecommendedBinding
import com.ilyeong.flickora.feature.detail.model.MovieDetailUiState

internal class MovieRecommendedFragment : BaseFragment<FragmentRecommendedBinding>() {

    override val viewBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentRecommendedBinding =
        FragmentRecommendedBinding::inflate

    private val viewModel: MovieDetailViewModel by viewModels({ requireParentFragment() })

    private val itemClickListener: (Media) -> Unit = { media ->
        val movieId = media.id
        val action = MovieDetailFragmentDirections.actionDetailFragmentToDetailFragment(movieId)
        findNavController().navigate(action)
    }

    private val collectionAdapter = PosterFixedAdapter(itemClickListener)
    private val recommendationAdapter = PosterFixedAdapter(itemClickListener)
    private val similarAdapter = PosterFixedAdapter(itemClickListener)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setCollection()
        setRecommendation()
        setSimilar()

        observeUiState()
    }

    private fun setCollection() {
        binding.tvMovieSection1.text = getString(R.string.media_section_collection)
        binding.rvMovieSection1.adapter = collectionAdapter
        binding.rvMovieSection1.addItemDecoration(PosterFixedItemDecoration)
    }

    private fun setRecommendation() {
        binding.tvMovieSection2.text = getString(R.string.media_section_recommendation)
        binding.rvMovieSection2.adapter = recommendationAdapter
        binding.rvMovieSection2.addItemDecoration(PosterFixedItemDecoration)
    }

    private fun setSimilar() {
        binding.tvMovieSection3.text = getString(R.string.media_section_similar)
        binding.rvMovieSection3.adapter = similarAdapter
        binding.rvMovieSection3.addItemDecoration(PosterFixedItemDecoration)
    }

    private fun observeUiState() {
        repeatOnViewStarted {
            viewModel.uiState.collect {
                when (it) {
                    is MovieDetailUiState.Loading -> {
                        /* no-op */
                    }

                    is MovieDetailUiState.Success -> {
                        // Collection movies
                        collectionAdapter.submitList(
                            it.collectionMovieList.map { movie -> movie as Media }
                        )
                        binding.tvMovieSection1.isVisible = it.collectionMovieList.isNotEmpty()
                        binding.rvMovieSection1.isVisible = it.collectionMovieList.isNotEmpty()

                        // Recommended movies
                        recommendationAdapter.submitList(it.movieRecommendationList)
                        binding.tvMovieSection2.isVisible = it.movieRecommendationList.isNotEmpty()
                        binding.rvMovieSection2.isVisible = it.movieRecommendationList.isNotEmpty()

                        // Similar movies
                        similarAdapter.submitList(it.movieSimilarList)
                        binding.tvMovieSection3.isVisible = it.movieSimilarList.isNotEmpty()
                        binding.rvMovieSection3.isVisible = it.movieSimilarList.isNotEmpty()

                        // Empty state
                        binding.tvReviewEmpty.isVisible =
                            it.collectionMovieList.isEmpty()
                                    && it.movieRecommendationList.isEmpty()
                                    && it.movieSimilarList.isEmpty()
                    }

                    is MovieDetailUiState.Failure -> {
                        /* no-op */
                    }
                }
            }
        }
    }
}
