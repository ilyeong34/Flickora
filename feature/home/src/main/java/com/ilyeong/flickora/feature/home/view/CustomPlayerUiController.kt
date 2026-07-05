package com.ilyeong.flickora.feature.home.view

import android.annotation.SuppressLint
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewParent
import android.widget.SeekBar
import androidx.core.view.isVisible
import com.ilyeong.flickora.feature.home.R

@SuppressLint("ClickableViewAccessibility")
internal class CustomPlayerUiController(
    customPlayerUi: View,
    private val youtubeWebPlayerView: YoutubeWebPlayerView,
    private val onPlayerReady: () -> Unit = {},
    private val onPlayerStateChanged: (Int) -> Unit = {},
    private val onPlayerError: (Int) -> Unit = {}
) : YoutubeWebPlayerView.Listener {

    private val sbProgress: SeekBar = customPlayerUi.findViewById(R.id.sb_progress)
    private val playerGestureDetector = GestureDetector(
        customPlayerUi.context,
        object : GestureDetector.SimpleOnGestureListener() {
            override fun onDown(event: MotionEvent): Boolean = true

            override fun onSingleTapUp(event: MotionEvent): Boolean {
                suppressAutoProgressUntilMillis = 0L
                toggleProgress()
                return false
            }

            override fun onDoubleTap(event: MotionEvent): Boolean {
                suppressAutoProgressUntilMillis =
                    System.currentTimeMillis() + DOUBLE_TAP_AUTO_PROGRESS_SUPPRESSION_MILLIS
                hideProgressAfterDoubleTap()
                return false
            }
        }
    )
    private var suppressAutoProgressUntilMillis = 0L
    private var videoDuration = 0f
    private var isSeeking = false
    private var playerState: Int? = null
    private val hideProgressRunnable = Runnable {
        if (!isSeeking && playerState == YoutubeWebPlayerState.PLAYING) {
            sbProgress.isVisible = false
        }
    }
    private val hideProgressAfterDoubleTapRunnable = Runnable {
        if (!isSeeking) {
            sbProgress.isVisible = false
        }
    }

    init {
        youtubeWebPlayerView.onTouchEventObserved = ::handlePlayerTouchEvent

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
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    if (videoDuration > 0f) {
                        youtubeWebPlayerView.seekTo(
                            videoDuration * seekBar.progress / seekBar.max
                        )
                    }
                    isSeeking = false
                }
            }
        )
    }

    override fun onReady() {
        onPlayerReady()
    }

    override fun onStateChange(state: Int) {
        playerState = state
        onPlayerStateChanged(state)
        if (System.currentTimeMillis() < suppressAutoProgressUntilMillis) {
            return
        }
        when (state) {
            YoutubeWebPlayerState.PLAYING -> showProgressTemporarily()
            YoutubeWebPlayerState.BUFFERING,
            YoutubeWebPlayerState.PAUSED,
            YoutubeWebPlayerState.CUED -> showProgress()

            YoutubeWebPlayerState.ENDED -> sbProgress.removeCallbacks(hideProgressRunnable)
            else -> Unit
        }
    }

    override fun onCurrentSecond(second: Float) {
        if (videoDuration <= 0f || isSeeking) {
            return
        }

        sbProgress.progress =
            (sbProgress.max * second / videoDuration).toInt().coerceIn(0, sbProgress.max)
    }

    override fun onDuration(duration: Float) {
        videoDuration = duration
    }

    override fun onError(errorCode: Int) {
        onPlayerError(errorCode)
    }

    private fun handlePlayerTouchEvent(event: MotionEvent) {
        playerGestureDetector.onTouchEvent(event)
    }

    private fun showProgress() {
        sbProgress.removeCallbacks(hideProgressRunnable)
        sbProgress.removeCallbacks(hideProgressAfterDoubleTapRunnable)
        sbProgress.isVisible = true
    }

    private fun showProgressTemporarily() {
        showProgress()
        sbProgress.postDelayed(hideProgressRunnable, PROGRESS_HIDE_DELAY_MILLIS)
    }

    private fun toggleProgress() {
        sbProgress.removeCallbacks(hideProgressAfterDoubleTapRunnable)
        if (sbProgress.isVisible) {
            sbProgress.removeCallbacks(hideProgressRunnable)
            sbProgress.isVisible = false
        } else {
            showProgressTemporarily()
        }
    }

    private fun hideProgressAfterDoubleTap() {
        sbProgress.removeCallbacks(hideProgressRunnable)
        sbProgress.removeCallbacks(hideProgressAfterDoubleTapRunnable)
        sbProgress.postDelayed(
            hideProgressAfterDoubleTapRunnable,
            DOUBLE_TAP_PROGRESS_HIDE_DELAY_MILLIS
        )
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
        const val DOUBLE_TAP_PROGRESS_HIDE_DELAY_MILLIS = 200L
        const val DOUBLE_TAP_AUTO_PROGRESS_SUPPRESSION_MILLIS = 1500L
    }
}
