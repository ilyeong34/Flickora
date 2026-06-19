package com.ilyeong.flickora.core.data.movie.repository

import androidx.paging.PagingData
import com.ilyeong.flickora.core.model.Credit
import com.ilyeong.flickora.core.model.Genre
import com.ilyeong.flickora.core.model.Movie
import com.ilyeong.flickora.core.model.Review
import com.ilyeong.flickora.core.model.TimeWindow
import kotlinx.coroutines.flow.Flow

interface MovieRepository {

    fun getMovieDetail(movieId: Int): Flow<Movie>
    fun getMovieCredit(movieId: Int): Flow<Credit>
    fun getMovieRecommendationList(movieId: Int): Flow<List<Movie>>
    fun getMovieSimilarList(movieId: Int): Flow<List<Movie>>
    fun getMovieReviewPaging(movieId: Int): Flow<PagingData<Review>>
    fun getMovieListByGenrePaging(genreId: Int): Flow<PagingData<Movie>>

    fun searchMoviePaging(query: String): Flow<PagingData<Movie>>

    fun getTopRatedMoviePaging(maxPage: Int = Int.MAX_VALUE): Flow<PagingData<Movie>>
    fun getUpcomingMoviePaging(maxPage: Int = Int.MAX_VALUE): Flow<PagingData<Movie>>
    fun getPopularMoviePaging(maxPage: Int = Int.MAX_VALUE): Flow<PagingData<Movie>>
    fun getNowPlayingMoviePaging(maxPage: Int = Int.MAX_VALUE): Flow<PagingData<Movie>>
    fun getTrendingMovieList(timeWindow: TimeWindow): Flow<List<Movie>>
    fun getTrendingMoviePaging(
        timeWindow: TimeWindow,
        maxPage: Int = Int.MAX_VALUE
    ): Flow<PagingData<Movie>>

    fun getMovieGenreList(): Flow<List<Genre>>
}