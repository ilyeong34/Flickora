package com.ilyeong.flickora.core.data.movie.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.ilyeong.flickora.core.data.movie.api.MovieApiService
import com.ilyeong.flickora.core.data.movie.model.toDomain
import com.ilyeong.flickora.core.data.movie.paging.GenreMoviePagingSource
import com.ilyeong.flickora.core.data.movie.paging.NowPlayingPagingSource
import com.ilyeong.flickora.core.data.movie.paging.PopularPagingSource
import com.ilyeong.flickora.core.data.movie.paging.ReviewPagingSource
import com.ilyeong.flickora.core.data.movie.paging.TopRatedPagingSource
import com.ilyeong.flickora.core.data.movie.paging.TrendingPagingSource
import com.ilyeong.flickora.core.data.movie.paging.UpcomingPagingSource
import com.ilyeong.flickora.core.model.Credit
import com.ilyeong.flickora.core.model.Genre
import com.ilyeong.flickora.core.model.Movie
import com.ilyeong.flickora.core.model.MovieVideo
import com.ilyeong.flickora.core.model.Review
import com.ilyeong.flickora.core.model.TimeWindow
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

internal class MovieRepositoryImpl @Inject constructor(
    private val apiService: MovieApiService
) : MovieRepository {

    // Improvement: emit the base data first, then emit the enriched collection data again.
    override fun getMovieDetail(movieId: Int) = flow<Movie> {
        val movieDetail = apiService.getMovieDetail(movieId).toDomain()
        val collection = movieDetail.collection

        when (collection) {
            null -> emit(movieDetail)
            else -> {
                val collection = apiService.getMovieCollection(collection.id).toDomain()
                emit(movieDetail.copy(collection = collection))
            }
        }
    }

    override fun getMovieCredit(movieId: Int) = flow<Credit> {
        val movieCredit = apiService.getMovieCredit(movieId).toDomain()
        emit(movieCredit)
    }

    override fun getMovieRecommendationList(movieId: Int) = flow<List<Movie>> {
        val recommendationList =
            apiService.getMovieRecommendationList(movieId).recommendationList.filter { it.mediaType == "movie" }
                .map { it.toDomain() }
        emit(recommendationList)
    }

    override fun getMovieSimilarList(movieId: Int) = flow<List<Movie>> {
        val similarList =
            apiService.getMovieSimilarList(movieId).similarList.map { it.toDomain() }
        emit(similarList)
    }

    override fun getMovieReviewPaging(movieId: Int): Flow<PagingData<Review>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { ReviewPagingSource(apiService, movieId) }
        ).flow
    }

    override fun getMovieListByGenrePaging(genreId: Int): Flow<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { GenreMoviePagingSource(apiService, genreId) }
        ).flow
    }

    override fun getTopRatedMoviePaging(maxPage: Int): Flow<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { TopRatedPagingSource(apiService, maxPage) }
        ).flow
    }

    override fun getUpcomingMoviePaging(maxPage: Int): Flow<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { UpcomingPagingSource(apiService, maxPage) }
        ).flow
    }

    override fun getPopularMoviePaging(maxPage: Int): Flow<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { PopularPagingSource(apiService, maxPage) }
        ).flow
    }

    override fun getNowPlayingMoviePaging(maxPage: Int): Flow<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { NowPlayingPagingSource(apiService, maxPage) }
        ).flow
    }

    override fun getNowPlayingMovieListWithVideos(limit: Int) = flow<List<Movie>> {
        val movieListWithVideos = mutableListOf<Movie>()

        for (page in 1..MAX_TRAILER_SEARCH_PAGE) {
            if (movieListWithVideos.size >= limit) break

            val candidateList = apiService.getNowPlayingMovieList(page)
                .resultList
                .map { it.toDomain() }
                .filter { it.backdropPath.isNotBlank() && !it.backdropPath.endsWith("/") }

            val candidateListWithVideos = coroutineScope {
                candidateList.map { movie ->
                    async {
                        val videoList = runCatching {
                            apiService.getMovieVideoList(movie.id)
                                .results
                                .map { it.toDomain() }
                                .filter { it.site == YOUTUBE_SITE }
                        }.getOrElse { emptyList() }

                        videoList.pickPlayableTrailer()?.let { playableVideo ->
                            movie.copy(videos = listOf(playableVideo))
                        }
                    }
                }.awaitAll()
            }

            candidateListWithVideos
                .filterNotNull()
                .take(limit - movieListWithVideos.size)
                .also { movieListWithVideos += it }
        }

        emit(movieListWithVideos.toList())
    }

    override fun getTrendingMovieList(timeWindow: TimeWindow) = flow<List<Movie>> {
        val trendingMovieList =
            apiService.getTrendingMovieList(timeWindow.name.lowercase()).resultList.map { it.toDomain() }
        emit(trendingMovieList)
    }

    override fun getTrendingMoviePaging(
        timeWindow: TimeWindow,
        maxPage: Int
    ): Flow<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                TrendingPagingSource(
                    apiService = apiService,
                    timeWindow = timeWindow.name.lowercase(),
                    maxPage = maxPage
                )
            }
        ).flow
    }

    override fun getMovieGenreList() = flow<List<Genre>> {
        val genreList = apiService.getMovieGenreList().genreList.map { it.toDomain() }
        emit(genreList)
    }

    private companion object {
        const val MAX_TRAILER_SEARCH_PAGE = 3
    }
}

private fun List<MovieVideo>.pickPlayableTrailer(): MovieVideo? {
    return firstOrNull { it.site == YOUTUBE_SITE && it.type == TRAILER_TYPE && it.official }
        ?: firstOrNull { it.site == YOUTUBE_SITE && it.type == TRAILER_TYPE }
        ?: firstOrNull { it.site == YOUTUBE_SITE && it.type == TEASER_TYPE && it.official }
        ?: firstOrNull { it.site == YOUTUBE_SITE && it.type == TEASER_TYPE }
}

private const val YOUTUBE_SITE = "YouTube"
private const val TRAILER_TYPE = "Trailer"
private const val TEASER_TYPE = "Teaser"
