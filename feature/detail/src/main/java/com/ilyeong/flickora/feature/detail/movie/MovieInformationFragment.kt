package com.ilyeong.flickora.feature.detail

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
import com.ilyeong.flickora.feature.detail.databinding.FragmentInformationBinding
import com.ilyeong.flickora.feature.detail.model.MovieDetailUiState
import com.ilyeong.flickora.feature.detail.movie.MovieDetailViewModel

internal class MovieInformationFragment : BaseFragment<FragmentInformationBinding>() {

    override val viewBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentInformationBinding =
        FragmentInformationBinding::inflate

    private val viewModel: MovieDetailViewModel by viewModels({ requireParentFragment() })

    private val castAdapter = CastAdapter()
    private val genreAdapter = GenreAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setOverview()
        setCast()
        setGenre()

        observeUiState()
    }

    private fun setOverview() {
        binding.tvOverview.setOnClickListener {
            when (binding.tvOverview.maxLines) {
                5 -> binding.tvOverview.maxLines = Int.MAX_VALUE
                else -> binding.tvOverview.maxLines = 5
            }
        }
    }

    private fun setCast() {
        binding.rvMovieCast.adapter = castAdapter
        binding.rvMovieCast.addItemDecoration(PosterFixedItemDecoration)
    }

    private fun setGenre() {
        binding.rvMovieGenre.adapter = genreAdapter
        binding.rvMovieGenre.addItemDecoration(PosterFixedItemDecoration)
    }

    private fun observeUiState() {
        repeatOnViewStarted {
            viewModel.uiState.collect {
                when (it) {
                    MovieDetailUiState.Loading -> {
                        /* no-op */
                    }

                    is MovieDetailUiState.Success -> {
                        val movie = it.movie

                        // Overview
                        binding.tvOverview.text = when (movie.overview.isBlank()) {
                            true -> getString(R.string.info_empty)
                            false -> movie.overview
                        }

                        // Main cast
                        castAdapter.submitList(it.cast)
                        binding.rvMovieCast.isVisible = it.cast.isNotEmpty()
                        binding.tvMovieCastEmpty.isVisible = it.cast.isEmpty()

                        // Genres
                        genreAdapter.submitList(movie.genreList)
                        binding.rvMovieGenre.isVisible = movie.genreList.isNotEmpty()
                        binding.tvMovieGenreEmpty.isVisible = movie.genreList.isEmpty()

                        // Additional info
                        binding.tvRelease.text = when (movie.releaseDate.isBlank()) {
                            true -> getString(R.string.release_empty)
                            false -> getString(R.string.release, movie.releaseDate)
                        }

                        binding.tvRuntime.text = when {
                            movie.runtime == 0 -> getString(R.string.runtime_empty)
                            movie.runtime < 60 -> getString(R.string.runtime_short, movie.runtime)
                            else -> getString(
                                R.string.runtime_long,
                                movie.runtime / 60,
                                movie.runtime % 60
                            )
                        }

                        binding.tvLanguage.text = when (movie.spokenLanguageList.isEmpty()) {
                            true -> getString(R.string.language_empty)
                            false -> getString(
                                R.string.language,
                                movie.spokenLanguageList.joinToString { it.englishName }
                            )
                        }
                    }

                    MovieDetailUiState.Failure -> {
                        /* no-op */
                    }
                }
            }
        }
    }
}
