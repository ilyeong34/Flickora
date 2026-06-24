package com.ilyeong.flickora.feature.home

import android.content.Context
import android.widget.FrameLayout
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.request.Disposable
import coil3.request.ErrorResult
import coil3.request.ImageRequest
import coil3.request.ImageResult
import com.ilyeong.flickora.core.model.Genre
import com.ilyeong.flickora.core.model.TvSeries
import com.ilyeong.flickora.core.ui.R
import com.ilyeong.flickora.feature.home.viewholder.PopularTvPosterViewHolder
import kotlinx.coroutines.CompletableDeferred
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PopularTvPosterViewHolderTest {

    @Test
    fun bind_showsNameWhenImageLoadFails() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        runOnMainThread {
            val parent = FrameLayout(context)
            val holder = PopularTvPosterViewHolder.create(parent, ErrorImageLoader(context))
            holder.bind(
                tvSeries(
                    name = "Breaking Bad",
                    originalName = "Original"
                )
            )

            assertEquals(
                "Breaking Bad",
                holder.itemView.findViewById<TextView>(R.id.tv_poster_title).text.toString()
            )
        }
    }

    @Test
    fun bind_fallsBackToOriginalNameWhenNameIsBlank() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        runOnMainThread {
            val parent = FrameLayout(context)
            val holder = PopularTvPosterViewHolder.create(parent, ErrorImageLoader(context))
            holder.bind(
                tvSeries(
                    name = "",
                    originalName = "Original Title"
                )
            )

            assertEquals(
                "Original Title",
                holder.itemView.findViewById<TextView>(R.id.tv_poster_title).text.toString()
            )
        }
    }

    private fun runOnMainThread(block: () -> Unit) {
        androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().runOnMainSync(block)
    }

    private fun tvSeries(name: String, originalName: String) = TvSeries(
        adult = false,
        backdropPath = "",
        genreList = listOf(Genre(1, "Drama")),
        id = 7,
        originCountry = listOf("US"),
        originalLanguage = "en",
        originalName = originalName,
        overview = "Overview",
        popularity = 100.0,
        posterPath = "https://image.tmdb.org/t/p/original//poster.png",
        firstAirDate = "2008-01-20",
        name = name,
        voteAverage = 9.0,
        voteCount = 1000
    )

    private class ErrorImageLoader(context: Context) : ImageLoader {
        private val delegate = SingletonImageLoader.get(context)

        override val components = delegate.components
        override val defaults = delegate.defaults
        override val diskCache = delegate.diskCache
        override val memoryCache = delegate.memoryCache

        override fun enqueue(request: ImageRequest): Disposable {
            val result = ErrorResult(
                image = null,
                request = request,
                throwable = IllegalStateException("forced failure")
            )
            request.listener?.onStart(request)
            request.listener?.onError(request, result)
            return ErrorDisposable(result)
        }

        override suspend fun execute(request: ImageRequest): ImageResult {
            val result = ErrorResult(
                image = null,
                request = request,
                throwable = IllegalStateException("forced failure")
            )
            request.listener?.onStart(request)
            request.listener?.onError(request, result)
            return result
        }

        override fun newBuilder(): ImageLoader.Builder = delegate.newBuilder()

        override fun shutdown() = Unit
    }

    private class ErrorDisposable(result: ImageResult) : Disposable {
        private val jobDelegate = CompletableDeferred<ImageResult>().apply {
            complete(result)
        }

        override val isDisposed: Boolean = true
        override val job = jobDelegate
        override fun dispose() = Unit
    }
}
