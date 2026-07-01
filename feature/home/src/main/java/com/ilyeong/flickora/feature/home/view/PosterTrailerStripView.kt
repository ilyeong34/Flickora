package com.ilyeong.flickora.feature.home.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.SeekBar
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
    private var playerView: YouTubePlayerView? = null
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
        currentTrailerList = visibleTrailerList
        val visibleTrailerIdList = visibleTrailerList.map { it.id }

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
        (playerView?.parent as? ViewGroup)?.removeView(playerView)
        playerTracker?.let { tracker -> youTubePlayer?.removeListener(tracker) }
        playerView?.let { view ->
            lifecycle?.removeObserver(view)
            view.release()
        }
        playerView = null
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
        if (trailerIndex == -1) {
            return
        }

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

        val customControls =
            youtubePlayerView.inflateCustomPlayerUi(R.layout.view_youtube_trailer_controls)
        val progressSeekBar = customControls.findViewById<SeekBar>(R.id.sb_progress)
        progressSeekBar.progressTintList = context.getColorStateList(android.R.color.holo_red_light)
        progressSeekBar.thumbTintList = context.getColorStateList(android.R.color.holo_red_light)
        progressSeekBar.progressBackgroundTintList =
            context.getColorStateList(android.R.color.darker_gray)
        var videoDuration = 0f
        var isSeeking = false

        binding.playerContainer.removeAllViews()
        binding.playerContainer.addView(youtubePlayerView)
        lifecycle?.addObserver(youtubePlayerView)
        playerView = youtubePlayerView

        val youtubePlayerListener = object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                val tracker = YouTubePlayerTracker()
                this@PosterTrailerStripView.youTubePlayer = youTubePlayer
                playerTracker = tracker
                youTubePlayer.addListener(tracker)

                progressSeekBar.setOnTouchListener { touchedView, event ->
                    touchedView.requestParentsDisallowIntercept(
                        event.actionMasked != MotionEvent.ACTION_UP &&
                                event.actionMasked != MotionEvent.ACTION_CANCEL
                    )
                    false
                }
                progressSeekBar.setOnSeekBarChangeListener(
                    object : SeekBar.OnSeekBarChangeListener {
                        override fun onProgressChanged(
                            seekBar: SeekBar,
                            progress: Int,
                            fromUser: Boolean
                        ) = Unit

                        override fun onStartTrackingTouch(seekBar: SeekBar) {
                            isSeeking = true
                            seekBar.requestParentsDisallowIntercept(true)
                        }

                        override fun onStopTrackingTouch(seekBar: SeekBar) {
                            if (videoDuration > 0f) {
                                youTubePlayer.seekTo(
                                    videoDuration * seekBar.progress / seekBar.max
                                )
                            }
                            isSeeking = false
                            seekBar.requestParentsDisallowIntercept(false)
                        }
                    }
                )

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

                    PlayerConstants.PlayerState.ENDED -> {
                        releasePlayer()
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

            override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                if (videoDuration <= 0f || isSeeking) {
                    return
                }

                progressSeekBar.progress = (
                        progressSeekBar.max * second / videoDuration
                        ).toInt().coerceIn(0, progressSeekBar.max)
            }

            override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {
                videoDuration = duration
            }
        }

        val options = IFramePlayerOptions.Builder(context)
            .controls(0)
            .build()

        youtubePlayerView.initialize(youtubePlayerListener, options)
    }


    private fun View.requestParentsDisallowIntercept(disallow: Boolean) {
        var currentParent: ViewParent? = parent
        while (currentParent != null) {
            currentParent.requestDisallowInterceptTouchEvent(disallow)
            currentParent = currentParent.parent
        }
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
