package com.ilyeong.flickora.feature.home.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.util.AttributeSet
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

    private val assetLoader = WebViewAssetLoader.Builder()
        .addPathHandler(
            ASSET_RESOURCE_PATH,
            WebViewAssetLoader.ResourcesPathHandler(context.applicationContext)
        )
        .build()
    private val webView = WebView(context)
    private var videoKey: String? = null
    private var currentSecond = 0f

    init {
        setBackgroundColor(Color.BLACK)
        webView.layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )
        configureWebView()
        addView(webView)
    }

    fun load(videoKey: String, startSeconds: Float = 0f, autoPlay: Boolean = true) {
        this.videoKey = videoKey
        currentSecond = startSeconds
        loadPlayerHtml(
            videoKey = videoKey,
            startSeconds = startSeconds,
            initialMode = if (autoPlay) INITIAL_MODE_AUTOPLAY else INITIAL_MODE_CUE
        )
    }

    fun restorePaused(videoKey: String, startSeconds: Float) {
        this.videoKey = videoKey
        currentSecond = startSeconds
        loadPlayerHtml(
            videoKey = videoKey,
            startSeconds = startSeconds,
            initialMode = INITIAL_MODE_RESTORE_PAUSED
        )
    }

    fun captureState(): Float? {
        return if (videoKey == null) null else currentSecond
    }

    fun release() {
        listener = null
        removeAllViews()
        webView.stopLoading()
        webView.loadUrl("about:blank")
        webView.removeJavascriptInterface(JS_BRIDGE_NAME)
        webView.destroy()
    }

    private fun configureWebView() {
        webView.setBackgroundColor(Color.BLACK)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.mediaPlaybackRequiresUserGesture = false
        webView.webChromeClient = WebChromeClient()
        webView.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(
                view: WebView,
                request: WebResourceRequest
            ): WebResourceResponse? = assetLoader.shouldInterceptRequest(request.url)

            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean = !request.url.isAllowedYoutubeUrl()
        }
        webView.addJavascriptInterface(PlayerBridge(), JS_BRIDGE_NAME)
    }

    private fun loadPlayerHtml(
        videoKey: String?,
        startSeconds: Float,
        initialMode: String
    ) {
        val url = PLAYER_URL.toUri().buildUpon()
            .appendQueryParameter("videoId", videoKey.orEmpty())
            .appendQueryParameter("startSeconds", startSeconds.toJsNumber())
            .appendQueryParameter("initialMode", initialMode)
            .build()
            .toString()
        webView.loadUrl(url)
    }

    private inner class PlayerBridge {
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
        fun onError(errorCode: Int) {
            post { listener?.onError(errorCode) }
        }
    }

    interface Listener {
        fun onStateChange(state: Int) = Unit
        fun onCurrentSecond(second: Float) = Unit
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

        fun Uri.isAllowedYoutubeUrl(): Boolean {
            val host = host.orEmpty()
            return scheme == "https" && (
                    host == APP_ASSET_HOST ||
                            host == "youtube.com" ||
                            host.endsWith(".youtube.com") ||
                            host == "youtube-nocookie.com" ||
                            host.endsWith(".youtube-nocookie.com") ||
                            host == "googlevideo.com" ||
                            host.endsWith(".googlevideo.com") ||
                            host == "ytimg.com" ||
                            host.endsWith(".ytimg.com")
                    )
        }
    }
}
