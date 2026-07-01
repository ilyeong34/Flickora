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
    private val mediaWatchlistAdapter = PosterFixedPagingAdapter(mediaClickListener)
    private val movieTopRatedAdapter = PosterFixedPagingAdapter(mediaClickListener)
    private val movieUpcomingAdapter = PosterFixedPagingAdapter(mediaClickListener)
    private val tvPopularAdapter = PosterFixedPagingAdapter(mediaClickListener)
    private val moviePopularAdapter = PosterFixedPagingAdapter(mediaClickListener)
    private val tvTopRatedAdapter = PosterFixedPagingAdapter(mediaClickListener)
    private val tvOnTheAirAdapter = PosterFixedPagingAdapter(mediaClickListener)
    private val tvAiringTodayAdapter = PosterFixedPagingAdapter(mediaClickListener)

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
        binding.rvGenre.adapter = genreAdapter
        binding.rvGenre.addItemDecoration(PosterFixedItemDecoration)
    }

    private fun setMovieSection() {
        binding.tvSection1.text = getString(CoreR.string.media_section_watchlist)
        binding.tvSection2.text = getString(CoreR.string.movie_section_upcoming)
        binding.tvSection3.text = getString(CoreR.string.tv_section_on_the_air)
        binding.tvSection4.text = getString(CoreR.string.movie_section_trending_top_10)
        binding.tvSection5.text = getString(CoreR.string.movie_section_popular)
        binding.tvSection6.text = getString(CoreR.string.tv_section_airing_today)
        binding.tvSection7.text = getString(CoreR.string.movie_section_top_rated)
        binding.tvSection8.text = getString(CoreR.string.tv_section_trending_top_10)
        binding.tvSection9.text = getString(CoreR.string.tv_section_popular)
        binding.tvSection10.text = getString(CoreR.string.movie_section_now_playing_trailer)
        binding.tvSection11.text = getString(CoreR.string.tv_section_top_rated)

        binding.rvSection1.adapter = mediaWatchlistAdapter
        binding.rvSection2.adapter = movieUpcomingAdapter
        binding.rvSection3.adapter = tvOnTheAirAdapter
        binding.rvSection4.adapter = movieRankingAdapter
        binding.rvSection5.adapter = moviePopularAdapter
        binding.rvSection6.adapter = tvAiringTodayAdapter
        binding.rvSection7.adapter = movieTopRatedAdapter
        binding.rvSection8.adapter = tvRankingAdapter
        binding.rvSection9.adapter = tvPopularAdapter
        // 트레일러
        binding.rvSection11.adapter = tvTopRatedAdapter

        binding.rvSection1.addItemDecoration(PosterFixedItemDecoration)
        binding.rvSection2.addItemDecoration(PosterFixedItemDecoration)
        binding.rvSection3.addItemDecoration(PosterFixedItemDecoration)
        binding.rvSection4.addItemDecoration(PosterFixedItemDecoration)
        binding.rvSection5.addItemDecoration(PosterFixedItemDecoration)
        binding.rvSection6.addItemDecoration(PosterFixedItemDecoration)
        binding.rvSection7.addItemDecoration(PosterFixedItemDecoration)
        binding.rvSection8.addItemDecoration(PosterFixedItemDecoration)
        binding.rvSection9.addItemDecoration(PosterFixedItemDecoration)
        // 트레일러
        binding.rvSection11.addItemDecoration(PosterFixedItemDecoration)
    }

    private fun setRetryBtn() {
        binding.ldf.btnRetry.setOnClickListener {
            viewModel.loadData()
            mediaWatchlistAdapter.retry()
            movieUpcomingAdapter.retry()
            tvPopularAdapter.retry()
            movieTopRatedAdapter.retry()
            moviePopularAdapter.retry()
            tvTopRatedAdapter.retry()
            tvOnTheAirAdapter.retry()
            tvAiringTodayAdapter.retry()
        }
    }

    private fun observeUiState() {
        repeatOnViewStarted {
            viewModel.watchlistPaging.collectLatest {
                mediaWatchlistAdapter.submitData(it)
            }
        }

        repeatOnViewStarted {
            viewModel.upcomingMoviePaging.collectLatest {
                movieUpcomingAdapter.submitData(it)
            }
        }

        repeatOnViewStarted {
            viewModel.popularMoviePaging.collectLatest {
                tvPopularAdapter.submitData(it)
            }
        }

        repeatOnViewStarted {
            viewModel.topRatedMoviePaging.collectLatest {
                movieTopRatedAdapter.submitData(it)
            }
        }

        repeatOnViewStarted {
            viewModel.popularTvPaging.collectLatest {
                moviePopularAdapter.submitData(it)
            }
        }

        repeatOnViewStarted {
            viewModel.topRatedTvPaging.collectLatest {
                tvTopRatedAdapter.submitData(it)
            }
        }

        repeatOnViewStarted {
            viewModel.onTheAirTvPaging.collectLatest {
                tvOnTheAirAdapter.submitData(it)
            }
        }

        repeatOnViewStarted {
            viewModel.airingTodayTvPaging.collectLatest {
                tvAiringTodayAdapter.submitData(it)
            }
        }

        binding.tvSection1.isVisible = (mediaWatchlistAdapter.itemCount > 0)
        binding.tvSection1.isVisible = (mediaWatchlistAdapter.itemCount > 0)

        repeatOnViewStarted {
            combine(
                viewModel.uiState,
                mediaWatchlistAdapter.loadStateFlow,
                movieUpcomingAdapter.loadStateFlow,
                tvPopularAdapter.loadStateFlow,
                movieTopRatedAdapter.loadStateFlow,
                moviePopularAdapter.loadStateFlow,
                tvTopRatedAdapter.loadStateFlow,
                tvOnTheAirAdapter.loadStateFlow,
                tvAiringTodayAdapter.loadStateFlow
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
                            || (watchlistState.refresh is LoadState.Loading && mediaWatchlistAdapter.itemCount == 0 && binding.rvSection1.isVisible == true)
                            || (upcomingState.refresh is LoadState.Loading && movieUpcomingAdapter.itemCount == 0)
                            || (popularState.refresh is LoadState.Loading && tvPopularAdapter.itemCount == 0)
                            || (topRatedState.refresh is LoadState.Loading && movieTopRatedAdapter.itemCount == 0)
                            || (popularTvState.refresh is LoadState.Loading && moviePopularAdapter.itemCount == 0)
                            || (topRatedTvState.refresh is LoadState.Loading && tvTopRatedAdapter.itemCount == 0)
                            || (onTheAirTvState.refresh is LoadState.Loading && tvOnTheAirAdapter.itemCount == 0)
                            || (airingTodayTvState.refresh is LoadState.Loading && tvAiringTodayAdapter.itemCount == 0)

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
                            || (watchlistState.refresh is LoadState.Error && mediaWatchlistAdapter.itemCount == 0 && binding.rvSection1.isVisible == false)
                            || (upcomingState.refresh is LoadState.Error && movieUpcomingAdapter.itemCount == 0)
                            || (popularState.refresh is LoadState.Error && tvPopularAdapter.itemCount == 0)
                            || (topRatedState.refresh is LoadState.Error && movieTopRatedAdapter.itemCount == 0)
                            || (popularTvState.refresh is LoadState.Error && moviePopularAdapter.itemCount == 0)
                            || (topRatedTvState.refresh is LoadState.Error && tvTopRatedAdapter.itemCount == 0)
                            || (onTheAirTvState.refresh is LoadState.Error && tvOnTheAirAdapter.itemCount == 0)
                            || (airingTodayTvState.refresh is LoadState.Error && tvAiringTodayAdapter.itemCount == 0)

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
//                        bindTrailerSection(successState.nowPlayingTrailerList)

                        binding.tvSection1.isVisible = (mediaWatchlistAdapter.itemCount > 0)
                        binding.tvSection1.isVisible = (mediaWatchlistAdapter.itemCount > 0)
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
        mediaWatchlistAdapter.refresh()
    }
//
//    private fun bindTrailerSection(movieList: List<Movie>) {
//        if (trailerMovieList == movieList &&
//            binding.llNowPlayingTrailer.childCount == movieList.take(MAX_TRAILER_COUNT).size &&
//            binding.hsvNowPlayingTrailer.isVisible == movieList.isNotEmpty()
//        ) {
//            return
//        }
//
//        releaseActiveTrailerPlayer()
//        trailerMovieList = movieList
//        binding.tvMovieSection4.isVisible = movieList.isNotEmpty()
//        binding.hsvNowPlayingTrailer.isVisible = movieList.isNotEmpty()
//        binding.llNowPlayingTrailer.removeAllViews()
//
//        movieList.take(MAX_TRAILER_COUNT).forEach { movie ->
//            val trailerBinding = ItemMovieTrailerBackdropBinding.inflate(
//                layoutInflater,
//                binding.llNowPlayingTrailer,
//                false
//            )
//
//            trailerBinding.ivBackdrop.load(movie.backdropPath) {
//                crossfade(true)
//            }
//            trailerBinding.tvTitle.text = movie.title
//            trailerBinding.root.setOnClickListener {
//                onTrailerCardClick(
//                    movie = movie,
//                    cardBinding = trailerBinding,
//                    playerContainer = trailerBinding.playerContainer
//                )
//            }
//
//            binding.llNowPlayingTrailer.addView(trailerBinding.root)
//        }
//    }
//
//    private fun onTrailerCardClick(
//        movie: Movie,
//        cardBinding: ItemMovieTrailerBackdropBinding,
//        playerContainer: FrameLayout
//    ) {
//        val video = movie.videos.firstOrNull()
//
//        if (video == null) {
//            showMessage(getString(CoreR.string.trailer_video_unavailable_message))
//            return
//        }
//
//        playTrailerInline(
//            videoKey = video.key,
//            cardBinding = cardBinding,
//            playerContainer = playerContainer
//        )
//    }
//
//    private fun playTrailerInline(
//        videoKey: String,
//        cardBinding: ItemMovieTrailerBackdropBinding,
//        playerContainer: FrameLayout
//    ) {
//        releaseActiveTrailerPlayer()
//        showTrailerLoading(cardBinding)
//
//        val playerView = YouTubePlayerView(requireContext()).apply {
//            enableAutomaticInitialization = false
//            alpha = 0f
//            layoutParams = FrameLayout.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT
//            )
//        }
//
//        playerContainer.addView(playerView)
//        viewLifecycleOwner.lifecycle.addObserver(playerView)
//        playerView.initialize(
//            object : AbstractYouTubePlayerListener() {
//                override fun onReady(youTubePlayer: YouTubePlayer) {
//                    youTubePlayer.loadVideo(videoKey, 0f)
//                }
//
//                override fun onStateChange(
//                    youTubePlayer: YouTubePlayer,
//                    state: PlayerConstants.PlayerState
//                ) {
//                    if (state == PlayerConstants.PlayerState.PLAYING) {
//                        playerView.alpha = 1f
//                        hideTrailerThumbnail(cardBinding)
//                        hideTrailerLoading(cardBinding)
//                    } else if (state == PlayerConstants.PlayerState.ENDED) {
//                        playerView.alpha = 0f
//                        showTrailerThumbnail(cardBinding)
//                        hideTrailerLoading(cardBinding)
//                    }
//                }
//
//                override fun onError(
//                    youTubePlayer: YouTubePlayer,
//                    error: PlayerConstants.PlayerError
//                ) {
//                    playerView.alpha = 0f
//                    showTrailerThumbnail(cardBinding)
//                    hideTrailerLoading(cardBinding)
//                }
//            }
//        )
//
//        activeYouTubePlayerView = playerView
//        activePlayerContainer = playerContainer
//        activeTrailerCardBinding = cardBinding
//    }
//
//    private fun hideTrailerThumbnail(cardBinding: ItemMovieTrailerBackdropBinding) {
//        cardBinding.ivBackdrop.isVisible = false
//        cardBinding.vGradient.isVisible = false
//        cardBinding.ivPlay.isVisible = false
//        cardBinding.tvTitle.isVisible = false
//    }
//
//    private fun showTrailerThumbnail(cardBinding: ItemMovieTrailerBackdropBinding) {
//        cardBinding.ivBackdrop.isVisible = true
//        cardBinding.vGradient.isVisible = true
//        cardBinding.ivPlay.isVisible = true
//        cardBinding.tvTitle.isVisible = true
//    }
//
//    private fun showTrailerLoading(cardBinding: ItemMovieTrailerBackdropBinding) {
//        cardBinding.ivPlay.isVisible = false
//        cardBinding.lpbLoading.isVisible = true
//    }
//
//    private fun hideTrailerLoading(cardBinding: ItemMovieTrailerBackdropBinding) {
//        cardBinding.lpbLoading.isVisible = false
//    }
//
//    private fun releaseActiveTrailerPlayer() {
//        activeYouTubePlayerView?.let { playerView ->
//            viewLifecycleOwner.lifecycle.removeObserver(playerView)
//            playerView.release()
//        }
//        activePlayerContainer?.removeAllViews()
//        restoreActiveTrailerCardImageState()
//        activeYouTubePlayerView = null
//        activePlayerContainer = null
//        activeTrailerCardBinding = null
//    }
//
//    private fun restoreActiveTrailerCardImageState() {
//        activeTrailerCardBinding?.run {
//            showTrailerThumbnail(this)
//            hideTrailerLoading(this)
//        }
//    }
//
//    override fun onStop() {
//        releaseActiveTrailerPlayer()
//        super.onStop()
//    }
//
//    override fun onDestroyView() {
//        releaseActiveTrailerPlayer()
//        super.onDestroyView()
//    }
//
//    private companion object {
//        const val MAX_TRAILER_COUNT = 5
//    }
}
