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
import com.ilyeong.flickora.feature.home.databinding.ItemMovieTrailerBackdropBinding
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
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
    private var activeTrailerId: Int? = null
    private var activeBinding: ItemMovieTrailerBackdropBinding? = null
    private var playerView: YouTubePlayerView? = null

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
        playerView?.let { view ->
            lifecycle?.removeObserver(view)
            view.release()
        }
        playerView = null
        activeTrailerId = null
        activeBinding = null
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
                binding = binding
            )
        }

        showIdle(binding)
    }

    private fun playTrailer(
        trailer: Movie,
        binding: ItemMovieTrailerBackdropBinding
    ) {
        val videoKey = trailer.videos.firstOrNull()?.key ?: run {
            onTrailerUnavailable?.invoke()
            return
        }

        releasePlayer()
        activeTrailerId = trailer.id
        activeBinding = binding
        showLoading(binding)

        val view = YouTubePlayerView(context).apply {
            enableAutomaticInitialization = false
            alpha = 0f
            layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
            )
        }

        binding.playerContainer.removeAllViews()
        binding.playerContainer.addView(view)
        lifecycle?.addObserver(view)
        playerView = view

        view.initialize(
            object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    youTubePlayer.loadVideo(videoKey, 0f)
                }

                override fun onStateChange(
                    youTubePlayer: YouTubePlayer,
                    state: PlayerConstants.PlayerState
                ) {
                    when (state) {
                        PlayerConstants.PlayerState.PLAYING -> {
                            view.alpha = 1f
                            showPlaying(binding)
                        }

                        PlayerConstants.PlayerState.BUFFERING,
                        PlayerConstants.PlayerState.VIDEO_CUED -> {
                            showLoading(binding)
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
            }
        )
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
