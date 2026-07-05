package com.ilyeong.flickora.feature.home.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import androidx.core.view.isVisible
import coil3.load
import coil3.request.crossfade
import com.ilyeong.flickora.core.model.Movie
import com.ilyeong.flickora.feature.home.R
import com.ilyeong.flickora.feature.home.databinding.ItemMovieTrailerBackdropBinding
import com.ilyeong.flickora.feature.home.model.TrailerPlaybackState

class PosterTrailerStripView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : HorizontalScrollView(context, attrs, defStyleAttr) {

    var onTrailerUnavailable: (() -> Unit)? = null

    private val container = LinearLayout(context).apply {
        orientation = LinearLayout.HORIZONTAL
        layoutParams = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        )
    }
    private val inflater = LayoutInflater.from(context)
    private var currentTrailerList: List<Movie> = emptyList()
    private var activeTrailerId: Int? = null
    private var activeVideoKey: String? = null
    private var activeBinding: ItemMovieTrailerBackdropBinding? = null
    private var youtubeWebPlayerView: YoutubeWebPlayerView? = null

    init {
        isHorizontalScrollBarEnabled = false
        overScrollMode = OVER_SCROLL_NEVER
        addView(container)
    }

    fun submitList(trailerList: List<Movie>) {
        val visibleTrailerList = trailerList.take(MAX_TRAILER_COUNT)
        val visibleTrailerIdList = visibleTrailerList.map { it.id }
        currentTrailerList = visibleTrailerList

        if (activeTrailerId != null && activeTrailerId !in visibleTrailerIdList) {
            releasePlayer()
        }

        visibleTrailerList.forEachIndexed { index, trailer ->
            val binding = getOrCreateTrailerCardBinding(index)
            bindTrailerCard(binding, trailer)
        }

        for (index in visibleTrailerList.size until container.childCount) {
            container.getChildAt(index).isVisible = false
        }
    }

    fun releasePlayer() {
        activeBinding?.let { showIdle(it) }
        youtubeWebPlayerView?.release()
        youtubeWebPlayerView = null
        activeTrailerId = null
        activeVideoKey = null
        activeBinding = null
    }

    internal fun capturePlaybackState(): TrailerPlaybackState? {
        val movieId = activeTrailerId ?: return null
        val videoKey = activeVideoKey ?: return null
        val currentSecond = youtubeWebPlayerView?.captureState()
            ?: return null

        return TrailerPlaybackState(
            movieId = movieId,
            videoKey = videoKey,
            currentSecond = currentSecond
        )
    }

    internal fun restorePlaybackState(state: TrailerPlaybackState?) {
        state ?: return

        if (activeTrailerId == state.movieId && activeVideoKey == state.videoKey) {
            return
        }

        val trailerIndex = currentTrailerList.indexOfFirst { it.id == state.movieId }
        if (trailerIndex == -1) return

        val trailer = currentTrailerList[trailerIndex]
        val videoKey = trailer.videos.firstOrNull { it.key == state.videoKey }?.key ?: return
        val binding = getOrCreateTrailerCardBinding(trailerIndex)

        playTrailer(
            trailer = trailer,
            binding = binding,
            videoKey = videoKey,
            startSeconds = state.currentSecond.coerceAtLeast(0f),
            autoPlay = false,
            restorePaused = true
        )
    }

    private fun getOrCreateTrailerCardBinding(index: Int): ItemMovieTrailerBackdropBinding {
        val existingView = container.getChildAt(index)
        if (existingView != null) {
            return ItemMovieTrailerBackdropBinding.bind(existingView)
        }

        return ItemMovieTrailerBackdropBinding.inflate(
            inflater,
            container,
            true
        )
    }

    private fun bindTrailerCard(
        binding: ItemMovieTrailerBackdropBinding,
        trailer: Movie
    ) {
        binding.root.isVisible = true

        if (activeTrailerId == trailer.id) {
            activeBinding = binding
            return
        }

        binding.ivBackdrop.load(trailer.backdropPath) {
            crossfade(true)
        }
        binding.tvTitle.text = trailer.title
        binding.ivPlay.setOnClickListener {
            playTrailer(
                trailer = trailer,
                binding = binding,
                videoKey = trailer.videos.firstOrNull()?.key
            )
        }

        showIdle(binding)
    }

    private fun playTrailer(
        trailer: Movie,
        binding: ItemMovieTrailerBackdropBinding,
        videoKey: String?,
        startSeconds: Float = 0f,
        autoPlay: Boolean = true,
        restorePaused: Boolean = false
    ) {
        if (videoKey == null) {
            onTrailerUnavailable?.invoke()
            return
        }

        releasePlayer()
        activeTrailerId = trailer.id
        activeVideoKey = videoKey
        activeBinding = binding
        showLoading(binding)

        val webPlayerView = YoutubeWebPlayerView(context).apply {
            layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
            )
        }
        val customPlayerUi = LayoutInflater.from(context)
            .inflate(R.layout.view_youtube_trailer_controls, binding.playerContainer, false)
        val customPlayerUiController = CustomPlayerUiController(
            customPlayerUi = customPlayerUi,
            youtubeWebPlayerView = webPlayerView,
            onPlayerStateChanged = { state ->
                when (state) {
                    YoutubeWebPlayerState.BUFFERING -> {
                        if (binding.playerContainer.alpha == 1f) {
                            showPlaying(binding)
                        } else {
                            showLoading(binding)
                        }
                    }
                    YoutubeWebPlayerState.PLAYING,
                    YoutubeWebPlayerState.PAUSED,
                    YoutubeWebPlayerState.CUED -> {
                        showPlaying(binding)
                    }
                    YoutubeWebPlayerState.UNSTARTED -> showLoading(binding)
                    else -> Unit
                }
            },
            onPlayerError = {
                releasePlayer()
            }
        )
        webPlayerView.listener = customPlayerUiController

        binding.playerContainer.removeAllViews()
        binding.playerContainer.addView(webPlayerView)
        binding.playerContainer.addView(customPlayerUi)
        youtubeWebPlayerView = webPlayerView

        webPlayerView.load(
            videoKey = videoKey,
            startSeconds = startSeconds,
            autoPlay = autoPlay,
            restorePaused = restorePaused
        )
    }

    private fun showIdle(binding: ItemMovieTrailerBackdropBinding) {
        binding.ivBackdrop.isVisible = true
        binding.vGradient.isVisible = true
        binding.ivPlay.isVisible = true
        binding.lpbLoading.isVisible = false
        binding.tvTitle.isVisible = true
        binding.playerContainer.isVisible = false
        binding.playerContainer.alpha = 1f
        binding.playerContainer.removeAllViews()
    }

    private fun showLoading(binding: ItemMovieTrailerBackdropBinding) {
        binding.ivBackdrop.isVisible = true
        binding.vGradient.isVisible = true
        binding.ivPlay.isVisible = false
        binding.lpbLoading.isVisible = true
        binding.tvTitle.isVisible = true
        binding.playerContainer.isVisible = true
        binding.playerContainer.alpha = 0f
    }

    private fun showPlaying(binding: ItemMovieTrailerBackdropBinding) {
        binding.ivBackdrop.isVisible = false
        binding.vGradient.isVisible = false
        binding.ivPlay.isVisible = false
        binding.lpbLoading.isVisible = false
        binding.tvTitle.isVisible = false
        binding.playerContainer.isVisible = true
        binding.playerContainer.alpha = 1f
    }

    override fun onDetachedFromWindow() {
        releasePlayer()
        super.onDetachedFromWindow()
    }

    private companion object {
        const val MAX_TRAILER_COUNT = 5
    }
}
