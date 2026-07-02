package com.ilyeong.flickora.feature.home.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import coil3.load
import coil3.request.crossfade
import com.ilyeong.flickora.core.model.Movie
import com.ilyeong.flickora.feature.home.R
import com.ilyeong.flickora.feature.home.databinding.ItemMovieTrailerBackdropBinding
import com.ilyeong.flickora.feature.home.model.TrailerPlaybackState
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

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
    private var lifecycle: Lifecycle? = null
    private var currentTrailerList: List<Movie> = emptyList()
    private var activeTrailerId: Int? = null
    private var activeVideoKey: String? = null
    private var activeBinding: ItemMovieTrailerBackdropBinding? = null
    private var youtubePlayerView: YouTubePlayerView? = null
    private var youTubePlayer: YouTubePlayer? = null
    private var playerTracker: YouTubePlayerTracker? = null

    init {
        isHorizontalScrollBarEnabled = false
        overScrollMode = OVER_SCROLL_NEVER
        addView(container)
    }

    fun bindLifecycle(lifecycle: Lifecycle) {
        this.lifecycle = lifecycle
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
        (youtubePlayerView?.parent as? ViewGroup)?.removeView(youtubePlayerView)
        playerTracker?.let { tracker -> youTubePlayer?.removeListener(tracker) }
        youtubePlayerView?.let { view ->
            lifecycle?.removeObserver(view)
            view.release()
        }
        youtubePlayerView = null
        youTubePlayer = null
        playerTracker = null
        activeTrailerId = null
        activeVideoKey = null
        activeBinding = null
    }

    internal fun capturePlaybackState(): TrailerPlaybackState? {
        val movieId = activeTrailerId ?: return null
        val videoKey = activeVideoKey ?: return null
        val currentSecond = playerTracker?.currentSecond ?: return null

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
            autoPlay = false
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
        autoPlay: Boolean = true
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

        val youtubePlayerView = YouTubePlayerView(context).apply {
            enableAutomaticInitialization = false
            alpha = 0f
            layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
            )
        }

        binding.playerContainer.removeAllViews()
        binding.playerContainer.addView(youtubePlayerView)
        lifecycle?.addObserver(youtubePlayerView)
        this.youtubePlayerView = youtubePlayerView

        val customPlayerUi =
            youtubePlayerView.inflateCustomPlayerUi(R.layout.view_youtube_trailer_controls)
        val listener = object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                this@PosterTrailerStripView.youTubePlayer = youTubePlayer

                val tracker = YouTubePlayerTracker()
                youTubePlayer.addListener(tracker)
                playerTracker = tracker

                val customPlayerUiController = CustomPlayerUiController(
                    customPlayerUi = customPlayerUi,
                    youTubePlayer = youTubePlayer
                )
                youTubePlayer.addListener(customPlayerUiController)

                if (autoPlay) {
                    youTubePlayer.loadVideo(videoKey, startSeconds)
                } else {
                    youTubePlayer.cueVideo(videoKey, startSeconds)
                }
            }

            override fun onStateChange(
                youTubePlayer: YouTubePlayer,
                state: PlayerConstants.PlayerState
            ) {
                when (state) {
                    PlayerConstants.PlayerState.PLAYING -> {
                        youtubePlayerView.alpha = 1f
                        showPlaying(binding)
                    }

                    PlayerConstants.PlayerState.BUFFERING -> {
                        if (youtubePlayerView.alpha == 1f) {
                            showPlaying(binding)
                        } else {
                            showLoading(binding)
                        }
                    }

                    PlayerConstants.PlayerState.VIDEO_CUED -> {
                        youtubePlayerView.alpha = 1f
                        showPlaying(binding)
                    }

                    else -> Unit
                }
            }

            override fun onError(
                youTubePlayer: YouTubePlayer,
                error: PlayerConstants.PlayerError
            ) {
                releasePlayer()
            }
        }

        val options = IFramePlayerOptions.Builder(context).controls(0).build()

        youtubePlayerView.initialize(listener, options)
    }

    private fun showIdle(binding: ItemMovieTrailerBackdropBinding) {
        binding.ivBackdrop.isVisible = true
        binding.vGradient.isVisible = true
        binding.ivPlay.isVisible = true
        binding.lpbLoading.isVisible = false
        binding.tvTitle.isVisible = true
        binding.playerContainer.isVisible = false
        binding.playerContainer.removeAllViews()
    }

    private fun showLoading(binding: ItemMovieTrailerBackdropBinding) {
        binding.ivBackdrop.isVisible = true
        binding.vGradient.isVisible = true
        binding.ivPlay.isVisible = false
        binding.lpbLoading.isVisible = true
        binding.tvTitle.isVisible = true
        binding.playerContainer.isVisible = true
    }

    private fun showPlaying(binding: ItemMovieTrailerBackdropBinding) {
        binding.ivBackdrop.isVisible = false
        binding.vGradient.isVisible = false
        binding.ivPlay.isVisible = false
        binding.lpbLoading.isVisible = false
        binding.tvTitle.isVisible = false
        binding.playerContainer.isVisible = true
    }

    override fun onDetachedFromWindow() {
        releasePlayer()
        super.onDetachedFromWindow()
    }

    private companion object {
        const val MAX_TRAILER_COUNT = 5
    }
}
