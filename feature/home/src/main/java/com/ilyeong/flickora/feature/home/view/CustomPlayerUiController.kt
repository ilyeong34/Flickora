package com.ilyeong.flickora.feature.home.view

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import android.view.ViewParent
import android.widget.SeekBar
import com.ilyeong.flickora.feature.home.R
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener

@SuppressLint("ClickableViewAccessibility")
internal class CustomPlayerUiController(
    customPlayerUi: View,
    private val youTubePlayer: YouTubePlayer
) : AbstractYouTubePlayerListener() {

    private var sbProgress: SeekBar = customPlayerUi.findViewById(R.id.sb_progress)
    private var videoDuration = 0f
    private var isSeeking = false

    init {
        sbProgress.setOnTouchListener { touchedView, event ->
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

    private fun View.requestParentsDisallowIntercept(disallow: Boolean) {
        var currentParent: ViewParent? = parent
        while (currentParent != null) {
            currentParent.requestDisallowInterceptTouchEvent(disallow)
            currentParent = currentParent.parent
        }
    }
}