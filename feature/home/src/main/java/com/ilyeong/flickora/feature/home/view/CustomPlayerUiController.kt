package com.ilyeong.flickora.feature.home.view

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import android.view.ViewParent
import android.widget.SeekBar
import androidx.core.view.isVisible
import com.ilyeong.flickora.feature.home.R
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener

@SuppressLint("ClickableViewAccessibility")
internal class CustomPlayerUiController(
    customPlayerUi: View,
    private val youTubePlayer: YouTubePlayer
) : AbstractYouTubePlayerListener() {

    private val touchOverlay: View = customPlayerUi.findViewById(R.id.touch_overlay)
    private val sbProgress: SeekBar = customPlayerUi.findViewById(R.id.sb_progress)
    private var videoDuration = 0f
    private var isSeeking = false
    private var playerState: PlayerConstants.PlayerState? = null
    private val hideProgressRunnable = Runnable {
        if (!isSeeking && playerState == PlayerConstants.PlayerState.PLAYING) {
            sbProgress.isVisible = false
        }
    }

    init {
        touchOverlay.setOnTouchListener { touchedView, event ->
            touchedView.requestParentsDisallowIntercept(event.actionMasked == MotionEvent.ACTION_UP)
            toggleProgress()
            false
        }

        sbProgress.setOnTouchListener { touchedView, event ->
            showProgress()
            touchedView.requestParentsDisallowIntercept(
                event.actionMasked != MotionEvent.ACTION_UP &&
                        event.actionMasked != MotionEvent.ACTION_CANCEL
            )
            false
        }
        sbProgress.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar,
                    progress: Int,
                    fromUser: Boolean
                ) = Unit

                override fun onStartTrackingTouch(seekBar: SeekBar) {
                    isSeeking = true
                    showProgress()
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    if (videoDuration > 0f) {
                        youTubePlayer.seekTo(
                            videoDuration * seekBar.progress / seekBar.max
                        )
                    }
                    isSeeking = false
                    showProgressTemporarily()
                }
            }
        )
    }

    override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerConstants.PlayerState) {
        playerState = state
        when (state) {
            PlayerConstants.PlayerState.PLAYING -> showProgressTemporarily()
            PlayerConstants.PlayerState.BUFFERING,
            PlayerConstants.PlayerState.PAUSED,
            PlayerConstants.PlayerState.VIDEO_CUED -> showProgress()

            PlayerConstants.PlayerState.ENDED -> sbProgress.removeCallbacks(hideProgressRunnable)
            else -> Unit
        }
    }

    override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
        if (videoDuration <= 0f || isSeeking) {
            return
        }

        sbProgress.progress =
            (sbProgress.max * second / videoDuration).toInt().coerceIn(0, sbProgress.max)
    }

    override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {
        videoDuration = duration
    }

    private fun showProgress() {
        sbProgress.removeCallbacks(hideProgressRunnable)
        sbProgress.isVisible = true
    }

    private fun showProgressTemporarily() {
        showProgress()
        sbProgress.postDelayed(hideProgressRunnable, PROGRESS_HIDE_DELAY_MILLIS)
    }

    private fun toggleProgress() {
        if (sbProgress.isVisible) {
            sbProgress.removeCallbacks(hideProgressRunnable)
            sbProgress.isVisible = false
        } else {
            showProgressTemporarily()
        }
    }

    private fun View.requestParentsDisallowIntercept(disallow: Boolean) {
        var currentParent: ViewParent? = parent
        while (currentParent != null) {
            currentParent.requestDisallowInterceptTouchEvent(disallow)
            currentParent = currentParent.parent
        }
    }

    private companion object {
        const val PROGRESS_HIDE_DELAY_MILLIS = 4000L
    }
}
