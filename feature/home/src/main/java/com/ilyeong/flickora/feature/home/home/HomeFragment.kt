package com.ilyeong.flickora.feature.home.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.findNavController
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import coil3.load
import coil3.request.crossfade
import com.ilyeong.flickora.core.model.Media
import com.ilyeong.flickora.core.model.Movie
import com.ilyeong.flickora.core.model.TvSeries
import com.ilyeong.flickora.core.ui.common.adapter.GenreAdapter
import com.ilyeong.flickora.core.ui.common.decoration.PosterFixedItemDecoration
import com.ilyeong.flickora.core.ui.common.fragment.BaseFragment
import com.ilyeong.flickora.core.ui.common.listener.ItemClickListener
import com.ilyeong.flickora.feature.home.HomeViewModel
import com.ilyeong.flickora.feature.home.adapter.PosterFixedPagingAdapter
import com.ilyeong.flickora.feature.home.adapter.PosterFullAdapter
import com.ilyeong.flickora.feature.home.adapter.PosterRankingAdapter
import com.ilyeong.flickora.feature.home.databinding.FragmentHomeBinding
import com.ilyeong.flickora.feature.home.databinding.ItemMovieTrailerBackdropBinding
import com.ilyeong.flickora.feature.home.model.HomeUiState
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlin.math.abs
import com.ilyeong.flickora.core.ui.R as CoreR

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    override val viewBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentHomeBinding =
        FragmentHomeBinding::inflate

    private val viewModel: HomeViewModel by viewModels()

    private val movieClickListener = ItemClickListener { movieId ->
        val request = NavDeepLinkRequest.Builder
            .fromUri("android-app://com.ilyeong.flickora/detail_fragment?movieId=${movieId}".toUri())
            .build()

        findNavController().navigate(request)
    }

    private val tvSeriesClickListener = ItemClickListener { tvSeriesId ->
        val request = NavDeepLinkRequest.Builder
            .fromUri("android-app://com.ilyeong.flickora/detail_fragment?tvSeriesId=${tvSeriesId}".toUri())
            .build()

        findNavController().navigate(request)
    }

    private val mediaClickListener: (Media) -> Unit = { media ->
        when (media) {
            is Movie -> movieClickListener.onItemClick(media.id)
            is TvSeries -> tvSeriesClickListener.onItemClick(media.id)
        }
    }

    private val genreClickListener = ItemClickListener { genreId ->
        val request = NavDeepLinkRequest.Builder
            .fromUri("android-app://com.ilyeong.flickora/genre_fragment?genreId=${genreId}".toUri())
            .build()

        findNavController().navigate(request)
    }

    private val posterFullAdapter = PosterFullAdapter(mediaClickListener)
    private val genreAdapter = GenreAdapter(genreClickListener)
    private val movieRankingAdapter = PosterRankingAdapter(mediaClickListener)
    private val tvRankingAdapter = PosterRankingAdapter(mediaClickListener)
    private val watchlistAdapter = PosterFixedPagingAdapter(mediaClickListener)
    private val topRatedAdapter = PosterFixedPagingAdapter(mediaClickListener)
    private val upcomingAdapter = PosterFixedPagingAdapter(mediaClickListener)
    private val popularAdapter = PosterFixedPagingAdapter(mediaClickListener)
    private val popularTvAdapter = PosterFixedPagingAdapter(mediaClickListener)
    private val topRatedTvAdapter = PosterFixedPagingAdapter(mediaClickListener)
    private val onTheAirTvAdapter = PosterFixedPagingAdapter(mediaClickListener)
    private val airingTodayTvAdapter = PosterFixedPagingAdapter(mediaClickListener)

    private var trailerMovieList: List<Movie> = emptyList()
    private var activeYouTubePlayerView: YouTubePlayerView? = null
    private var activePlayerContainer: FrameLayout? = null
    private var activeTrailerCardBinding: ItemMovieTrailerBackdropBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbarMenu()
        setMovieBanner()
        setMovieGenre()
        setMovieSection()
        setRetryBtn()

        observeUiState()

        refreshData()
    }

    private fun setToolbarMenu() {
        binding.tb.setOnMenuItemClickListener { _ ->
            val request = NavDeepLinkRequest.Builder
                .fromUri("android-app://com.ilyeong.flickora/search_fragment".toUri())
                .build()

            findNavController().navigate(request)
            true
        }
    }

    private fun setMovieBanner() {

        binding.vpBanner.adapter = posterFullAdapter
        binding.vpBanner.offscreenPageLimit = 1
        binding.vpBanner.setPageTransformer(
            CompositePageTransformer().also {
                it.addTransformer(
                    MarginPageTransformer(
                        binding.root.context.resources.getDimensionPixelSize(
                            CoreR.dimen.flickora_padding_xlarge
                        )
                    )
                )
                it.addTransformer { eachPageView: View, positionFromCenter: Float ->
                    val scale = 1 - abs(positionFromCenter)
                    eachPageView.scaleY = 0.85f + 0.15f * scale
                }
            }
        )
    }

    private fun setMovieGenre() {
        binding.rvMovieGenre.adapter = genreAdapter
        binding.rvMovieGenre.addItemDecoration(PosterFixedItemDecoration)
    }

    private fun setMovieSection() {
        binding.tvMovieRanking.text = getString(CoreR.string.home_section_movie_trending_top_10)
        binding.tvMovieSection1.text = getString(CoreR.string.movie_section_watchlist)
        binding.tvMovieSection2.text = getString(CoreR.string.movie_section_upcoming)
        binding.tvMovieSection3.text = getString(CoreR.string.movie_section_popular)
        binding.tvMovieSection4.text = getString(CoreR.string.movie_section_now_playing_trailer)
        binding.tvMovieSection6.text = getString(CoreR.string.movie_section_top_rated)
        binding.tvTvRanking.text = getString(CoreR.string.home_section_tv_trending_top_10)
        binding.tvTvSectionPopular.text = getString(CoreR.string.tv_section_popular)
        binding.tvTvSectionTopRated.text = getString(CoreR.string.tv_section_top_rated)
        binding.tvTvSectionOnTheAir.text = getString(CoreR.string.tv_section_on_the_air)
        binding.tvTvSectionAiringToday.text = getString(CoreR.string.tv_section_airing_today)

        binding.rvMovieRanking.adapter = movieRankingAdapter
        binding.rvTvRanking.adapter = tvRankingAdapter
        binding.rvMovieSection1.adapter = watchlistAdapter
        binding.rvMovieSection2.adapter = upcomingAdapter
        binding.rvMovieSection3.adapter = popularAdapter
        binding.rvMovieSection6.adapter = topRatedAdapter
        binding.rvTvSectionPopular.adapter = popularTvAdapter
        binding.rvTvSectionTopRated.adapter = topRatedTvAdapter
        binding.rvTvSectionOnTheAir.adapter = onTheAirTvAdapter
        binding.rvTvSectionAiringToday.adapter = airingTodayTvAdapter

        binding.rvMovieRanking.addItemDecoration(PosterFixedItemDecoration)
        binding.rvTvRanking.addItemDecoration(PosterFixedItemDecoration)
        binding.rvMovieSection1.addItemDecoration(PosterFixedItemDecoration)
        binding.rvMovieSection2.addItemDecoration(PosterFixedItemDecoration)
        binding.rvMovieSection3.addItemDecoration(PosterFixedItemDecoration)
        binding.rvMovieSection6.addItemDecoration(PosterFixedItemDecoration)
        binding.rvTvSectionPopular.addItemDecoration(PosterFixedItemDecoration)
        binding.rvTvSectionTopRated.addItemDecoration(PosterFixedItemDecoration)
        binding.rvTvSectionOnTheAir.addItemDecoration(PosterFixedItemDecoration)
        binding.rvTvSectionAiringToday.addItemDecoration(PosterFixedItemDecoration)
    }

    private fun setRetryBtn() {
        binding.ldf.btnRetry.setOnClickListener {
            viewModel.loadData()
            watchlistAdapter.retry()
            upcomingAdapter.retry()
            popularAdapter.retry()
            topRatedAdapter.retry()
            popularTvAdapter.retry()
            topRatedTvAdapter.retry()
            onTheAirTvAdapter.retry()
            airingTodayTvAdapter.retry()
        }
    }

    private fun observeUiState() {
        repeatOnViewStarted {
            viewModel.watchlistPaging.collectLatest {
                watchlistAdapter.submitData(it)
            }
        }

        repeatOnViewStarted {
            viewModel.upcomingMoviePaging.collectLatest {
                upcomingAdapter.submitData(it)
            }
        }

        repeatOnViewStarted {
            viewModel.popularMoviePaging.collectLatest {
                popularAdapter.submitData(it)
            }
        }

        repeatOnViewStarted {
            viewModel.topRatedMoviePaging.collectLatest {
                topRatedAdapter.submitData(it)
            }
        }

        repeatOnViewStarted {
            viewModel.popularTvPaging.collectLatest {
                popularTvAdapter.submitData(it)
            }
        }

        repeatOnViewStarted {
            viewModel.topRatedTvPaging.collectLatest {
                topRatedTvAdapter.submitData(it)
            }
        }

        repeatOnViewStarted {
            viewModel.onTheAirTvPaging.collectLatest {
                onTheAirTvAdapter.submitData(it)
            }
        }

        repeatOnViewStarted {
            viewModel.airingTodayTvPaging.collectLatest {
                airingTodayTvAdapter.submitData(it)
            }
        }

        binding.tvMovieSection1.isVisible = (watchlistAdapter.itemCount > 0)
        binding.rvMovieSection1.isVisible = (watchlistAdapter.itemCount > 0)

        repeatOnViewStarted {
            combine(
                viewModel.uiState,
                watchlistAdapter.loadStateFlow,
                upcomingAdapter.loadStateFlow,
                popularAdapter.loadStateFlow,
                topRatedAdapter.loadStateFlow,
                popularTvAdapter.loadStateFlow,
                topRatedTvAdapter.loadStateFlow,
                onTheAirTvAdapter.loadStateFlow,
                airingTodayTvAdapter.loadStateFlow
            ) {
                val uiState = it[0] as HomeUiState
                val watchlistState = it[1] as CombinedLoadStates
                val upcomingState = it[2] as CombinedLoadStates
                val popularState = it[3] as CombinedLoadStates
                val topRatedState = it[4] as CombinedLoadStates
                val popularTvState = it[5] as CombinedLoadStates
                val topRatedTvState = it[6] as CombinedLoadStates
                val onTheAirTvState = it[7] as CombinedLoadStates
                val airingTodayTvState = it[8] as CombinedLoadStates

                val isFirstLoading =
                    uiState is HomeUiState.Loading
                            || (watchlistState.refresh is LoadState.Loading && watchlistAdapter.itemCount == 0 && binding.rvMovieSection1.isVisible == true)
                            || (upcomingState.refresh is LoadState.Loading && upcomingAdapter.itemCount == 0)
                            || (popularState.refresh is LoadState.Loading && popularAdapter.itemCount == 0)
                            || (topRatedState.refresh is LoadState.Loading && topRatedAdapter.itemCount == 0)
                            || (popularTvState.refresh is LoadState.Loading && popularTvAdapter.itemCount == 0)
                            || (topRatedTvState.refresh is LoadState.Loading && topRatedTvAdapter.itemCount == 0)
                            || (onTheAirTvState.refresh is LoadState.Loading && onTheAirTvAdapter.itemCount == 0)
                            || (airingTodayTvState.refresh is LoadState.Loading && airingTodayTvAdapter.itemCount == 0)

                val isFirstLoadingSuccess =
                    uiState is HomeUiState.Success
                            && watchlistState.refresh is LoadState.NotLoading
                            && upcomingState.refresh is LoadState.NotLoading
                            && popularState.refresh is LoadState.NotLoading
                            && topRatedState.refresh is LoadState.NotLoading
                            && popularTvState.refresh is LoadState.NotLoading
                            && topRatedTvState.refresh is LoadState.NotLoading
                            && onTheAirTvState.refresh is LoadState.NotLoading
                            && airingTodayTvState.refresh is LoadState.NotLoading

                val isFirstLoadingFailure =
                    uiState is HomeUiState.Failure
                            || (watchlistState.refresh is LoadState.Error && watchlistAdapter.itemCount == 0 && binding.rvMovieSection1.isVisible == false)
                            || (upcomingState.refresh is LoadState.Error && upcomingAdapter.itemCount == 0)
                            || (popularState.refresh is LoadState.Error && popularAdapter.itemCount == 0)
                            || (topRatedState.refresh is LoadState.Error && topRatedAdapter.itemCount == 0)
                            || (popularTvState.refresh is LoadState.Error && popularTvAdapter.itemCount == 0)
                            || (topRatedTvState.refresh is LoadState.Error && topRatedTvAdapter.itemCount == 0)
                            || (onTheAirTvState.refresh is LoadState.Error && onTheAirTvAdapter.itemCount == 0)
                            || (airingTodayTvState.refresh is LoadState.Error && airingTodayTvAdapter.itemCount == 0)

                when {
                    isFirstLoading -> {
                        binding.sfl.startShimmer()
                        binding.sfl.isVisible = true
                        binding.content.isVisible = false
                        binding.ldf.root.isVisible = false
                    }

                    isFirstLoadingSuccess -> {
                        binding.sfl.stopShimmer()
                        binding.sfl.isVisible = false
                        binding.content.isVisible = true
                        binding.ldf.root.isVisible = false

                        val successState = uiState
                        posterFullAdapter.submitList(successState.bannerMediaList)
                        movieRankingAdapter.submitList(successState.rankingMovieList)
                        tvRankingAdapter.submitList(successState.rankingTvList)
                        genreAdapter.submitList(successState.genreList)
                        bindTrailerSection(successState.nowPlayingTrailerList)

                        binding.tvMovieSection1.isVisible = (watchlistAdapter.itemCount > 0)
                        binding.rvMovieSection1.isVisible = (watchlistAdapter.itemCount > 0)
                        binding.tvMovieRanking.isVisible =
                            successState.rankingMovieList.isNotEmpty()
                        binding.rvMovieRanking.isVisible =
                            successState.rankingMovieList.isNotEmpty()
                        binding.tvTvRanking.isVisible =
                            successState.rankingTvList.isNotEmpty()
                        binding.rvTvRanking.isVisible =
                            successState.rankingTvList.isNotEmpty()
                    }

                    isFirstLoadingFailure -> {
                        binding.sfl.stopShimmer()
                        binding.sfl.isVisible = false
                        binding.content.isVisible = false
                        binding.ldf.root.isVisible = true
                    }

                    else -> {   // refresh
                        if (watchlistState.refresh is LoadState.Error
                            || upcomingState.refresh is LoadState.Error
                            || popularState.refresh is LoadState.Error
                            || topRatedState.refresh is LoadState.Error
                            || popularTvState.refresh is LoadState.Error
                            || topRatedTvState.refresh is LoadState.Error
                            || onTheAirTvState.refresh is LoadState.Error
                            || airingTodayTvState.refresh is LoadState.Error
                        ) {
                            showMessage(getString(CoreR.string.fail_refresh_message))
                        }
                    }
                }
            }.collect()
        }
    }

    private fun refreshData() {
        watchlistAdapter.refresh()
    }

    private fun bindTrailerSection(movieList: List<Movie>) {
        if (trailerMovieList == movieList &&
            binding.llNowPlayingTrailer.childCount == movieList.take(MAX_TRAILER_COUNT).size &&
            binding.hsvNowPlayingTrailer.isVisible == movieList.isNotEmpty()
        ) {
            return
        }

        releaseActiveTrailerPlayer()
        trailerMovieList = movieList
        binding.tvMovieSection4.isVisible = movieList.isNotEmpty()
        binding.hsvNowPlayingTrailer.isVisible = movieList.isNotEmpty()
        binding.llNowPlayingTrailer.removeAllViews()

        movieList.take(MAX_TRAILER_COUNT).forEach { movie ->
            val trailerBinding = ItemMovieTrailerBackdropBinding.inflate(
                layoutInflater,
                binding.llNowPlayingTrailer,
                false
            )

            trailerBinding.ivBackdrop.load(movie.backdropPath) {
                crossfade(true)
            }
            trailerBinding.tvTitle.text = movie.title
            trailerBinding.root.setOnClickListener {
                onTrailerCardClick(
                    movie = movie,
                    cardBinding = trailerBinding,
                    playerContainer = trailerBinding.playerContainer
                )
            }

            binding.llNowPlayingTrailer.addView(trailerBinding.root)
        }
    }

    private fun onTrailerCardClick(
        movie: Movie,
        cardBinding: ItemMovieTrailerBackdropBinding,
        playerContainer: FrameLayout
    ) {
        val video = movie.videos.firstOrNull()

        if (video == null) {
            showMessage(getString(CoreR.string.trailer_video_unavailable_message))
            return
        }

        playTrailerInline(
            videoKey = video.key,
            cardBinding = cardBinding,
            playerContainer = playerContainer
        )
    }

    private fun playTrailerInline(
        videoKey: String,
        cardBinding: ItemMovieTrailerBackdropBinding,
        playerContainer: FrameLayout
    ) {
        releaseActiveTrailerPlayer()
        showTrailerLoading(cardBinding)

        val playerView = YouTubePlayerView(requireContext()).apply {
            enableAutomaticInitialization = false
            alpha = 0f
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        playerContainer.addView(playerView)
        viewLifecycleOwner.lifecycle.addObserver(playerView)
        playerView.initialize(
            object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    youTubePlayer.loadVideo(videoKey, 0f)
                }

                override fun onStateChange(
                    youTubePlayer: YouTubePlayer,
                    state: PlayerConstants.PlayerState
                ) {
                    if (state == PlayerConstants.PlayerState.PLAYING) {
                        playerView.alpha = 1f
                        hideTrailerThumbnail(cardBinding)
                        hideTrailerLoading(cardBinding)
                    } else if (state == PlayerConstants.PlayerState.ENDED) {
                        playerView.alpha = 0f
                        showTrailerThumbnail(cardBinding)
                        hideTrailerLoading(cardBinding)
                    }
                }

                override fun onError(
                    youTubePlayer: YouTubePlayer,
                    error: PlayerConstants.PlayerError
                ) {
                    playerView.alpha = 0f
                    showTrailerThumbnail(cardBinding)
                    hideTrailerLoading(cardBinding)
                }
            }
        )

        activeYouTubePlayerView = playerView
        activePlayerContainer = playerContainer
        activeTrailerCardBinding = cardBinding
    }

    private fun hideTrailerThumbnail(cardBinding: ItemMovieTrailerBackdropBinding) {
        cardBinding.ivBackdrop.isVisible = false
        cardBinding.vGradient.isVisible = false
        cardBinding.ivPlay.isVisible = false
        cardBinding.tvTitle.isVisible = false
    }

    private fun showTrailerThumbnail(cardBinding: ItemMovieTrailerBackdropBinding) {
        cardBinding.ivBackdrop.isVisible = true
        cardBinding.vGradient.isVisible = true
        cardBinding.ivPlay.isVisible = true
        cardBinding.tvTitle.isVisible = true
    }

    private fun showTrailerLoading(cardBinding: ItemMovieTrailerBackdropBinding) {
        cardBinding.ivPlay.isVisible = false
        cardBinding.lpbLoading.isVisible = true
    }

    private fun hideTrailerLoading(cardBinding: ItemMovieTrailerBackdropBinding) {
        cardBinding.lpbLoading.isVisible = false
    }

    private fun releaseActiveTrailerPlayer() {
        activeYouTubePlayerView?.let { playerView ->
            viewLifecycleOwner.lifecycle.removeObserver(playerView)
            playerView.release()
        }
        activePlayerContainer?.removeAllViews()
        restoreActiveTrailerCardImageState()
        activeYouTubePlayerView = null
        activePlayerContainer = null
        activeTrailerCardBinding = null
    }

    private fun restoreActiveTrailerCardImageState() {
        activeTrailerCardBinding?.run {
            showTrailerThumbnail(this)
            hideTrailerLoading(this)
        }
    }

    override fun onStop() {
        releaseActiveTrailerPlayer()
        super.onStop()
    }

    override fun onDestroyView() {
        releaseActiveTrailerPlayer()
        super.onDestroyView()
    }

    private companion object {
        const val MAX_TRAILER_COUNT = 5
    }
}
