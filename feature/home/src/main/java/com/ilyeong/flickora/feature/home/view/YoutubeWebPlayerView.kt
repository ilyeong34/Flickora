package com.ilyeong.flickora.feature.home.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Message
import android.util.AttributeSet
import android.view.MotionEvent
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.core.net.toUri
import androidx.webkit.WebViewAssetLoader

internal object YoutubeWebPlayerState {
    const val UNSTARTED = -1
    const val ENDED = 0
    const val PLAYING = 1
    const val PAUSED = 2
    const val BUFFERING = 3
    const val CUED = 5
}

@SuppressLint("SetJavaScriptEnabled")
class YoutubeWebPlayerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    var listener: Listener? = null
    var onTouchEventObserved: ((MotionEvent) -> Unit)? = null

    private val assetLoader = WebViewAssetLoader.Builder()
        .addPathHandler(
            ASSET_RESOURCE_PATH,
            WebViewAssetLoader.ResourcesPathHandler(context.applicationContext)
        )
        .build()
    private val webView = WebView(context)
    private var isReady = false
    private var pendingCommand: String? = null
    private var videoKey: String? = null
    private var currentSecond = 0f
    private var duration = 0f

    init {
        setBackgroundColor(Color.BLACK)
        webView.layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )
        configureWebView()
        addView(webView)
    }

    fun load(
        videoKey: String,
        startSeconds: Float = 0f,
        autoPlay: Boolean = true,
        restorePaused: Boolean = false
    ) {
        this.videoKey = videoKey
        currentSecond = startSeconds
        loadPlayerHtml(
            videoKey = videoKey,
            startSeconds = startSeconds,
            initialMode = when {
                restorePaused -> INITIAL_MODE_RESTORE_PAUSED
                autoPlay -> INITIAL_MODE_AUTOPLAY
                else -> INITIAL_MODE_CUE
            }
        )
    }

    fun captureState(): Float? {
        return if (videoKey == null) null else currentSecond
    }

    fun seekTo(seconds: Float) {
        currentSecond = seconds
        evaluateWhenReady("seekTo(${seconds.toJsNumber()});")
    }

    fun release() {
        listener = null
        onTouchEventObserved = null
        pendingCommand = null
        isReady = false
        removeAllViews()
        webView.stopLoading()
        webView.loadUrl("about:blank")
        webView.removeJavascriptInterface(JS_BRIDGE_NAME)
        webView.destroy()
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        onTouchEventObserved?.invoke(event)
        return super.dispatchTouchEvent(event)
    }

    private fun configureWebView() {
        webView.setBackgroundColor(Color.BLACK)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.mediaPlaybackRequiresUserGesture = false
        webView.webChromeClient = object : WebChromeClient() {
            override fun onCreateWindow(
                view: WebView,
                isDialog: Boolean,
                isUserGesture: Boolean,
                resultMsg: Message
            ): Boolean = false
        }
        webView.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(
                view: WebView,
                request: WebResourceRequest
            ): WebResourceResponse? = assetLoader.shouldInterceptRequest(request.url)

            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean = request.url.shouldBlockNavigation(request.isForMainFrame)
        }
        webView.addJavascriptInterface(PlayerBridge(), JS_BRIDGE_NAME)
    }

    private fun loadPlayerHtml(
        videoKey: String?,
        startSeconds: Float,
        initialMode: String
    ) {
        isReady = false
        pendingCommand = null
        duration = 0f
        val url = PLAYER_URL.toUri().buildUpon()
            .appendQueryParameter("videoId", videoKey.orEmpty())
            .appendQueryParameter("startSeconds", startSeconds.toJsNumber())
            .appendQueryParameter("initialMode", initialMode)
            .build()
            .toString()
        webView.loadUrl(url)
    }

    private fun evaluateWhenReady(command: String) {
        if (isReady) {
            webView.evaluateJavascript(command, null)
        } else {
            pendingCommand = command
        }
    }

    private inner class PlayerBridge {
        @JavascriptInterface
        fun onReady(value: String) {
            post {
                isReady = true
                listener?.onReady()
                pendingCommand?.let { command ->
                    pendingCommand = null
                    webView.evaluateJavascript(command, null)
                }
            }
        }

        @JavascriptInterface
        fun onStateChange(state: Int) {
            post { listener?.onStateChange(state) }
        }

        @JavascriptInterface
        fun onCurrentSecond(second: Double) {
            post {
                currentSecond = second.toFloat()
                listener?.onCurrentSecond(currentSecond)
            }
        }

        @JavascriptInterface
        fun onDuration(duration: Double) {
            post {
                this@YoutubeWebPlayerView.duration = duration.toFloat()
                listener?.onDuration(this@YoutubeWebPlayerView.duration)
            }
        }

        @JavascriptInterface
        fun onError(errorCode: Int) {
            post { listener?.onError(errorCode) }
        }
    }

    interface Listener {
        fun onReady() = Unit
        fun onStateChange(state: Int) = Unit
        fun onCurrentSecond(second: Float) = Unit
        fun onDuration(duration: Float) = Unit
        fun onError(errorCode: Int) = Unit
    }

    companion object {
        const val JS_BRIDGE_NAME = "AndroidPlayer"
        const val APP_ASSET_HOST = "appassets.androidplatform.net"
        const val ASSET_RESOURCE_PATH = "/res/"
        const val PLAYER_URL =
            "https://appassets.androidplatform.net/res/raw/youtube_iframe_player.html"
        const val INITIAL_MODE_AUTOPLAY = "autoplay"
        const val INITIAL_MODE_CUE = "cue"
        const val INITIAL_MODE_RESTORE_PAUSED = "restore_paused"

        fun Float.toJsNumber(): String = if (isFinite()) toString() else "0"

        fun Uri.shouldBlockNavigation(isForMainFrame: Boolean): Boolean {
            if (isPlayerAssetUrl()) {
                return false
            }

            if (isForMainFrame) {
                return true
            }

            return isYoutubePageNavigation()
        }

        private fun Uri.isPlayerAssetUrl(): Boolean {
            return scheme == "https" &&
                    host == APP_ASSET_HOST &&
                    path == "/res/raw/youtube_iframe_player.html"
        }

        private fun Uri.isYoutubePageNavigation(): Boolean {
            val host = host.orEmpty()
            val path = path.orEmpty()
            return scheme == "https" &&
                    (host == "youtube.com" || host.endsWith(".youtube.com")) &&
                    (
                            path == "/watch" ||
                                    path.startsWith("/channel/") ||
                                    path.startsWith("/c/") ||
                                    path.startsWith("/user/") ||
                                    path.startsWith("/@")
                            )
        }
    }
}
