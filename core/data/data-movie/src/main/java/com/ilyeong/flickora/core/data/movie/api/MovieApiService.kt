package com.ilyeong.flickora.core.data.movie.api

import com.ilyeong.flickora.core.data.movie.model.CollectionResponse
import com.ilyeong.flickora.core.data.movie.model.CreditResponse
import com.ilyeong.flickora.core.data.movie.model.DiscoverResponse
import com.ilyeong.flickora.core.data.movie.model.GenreListResponse
import com.ilyeong.flickora.core.data.movie.model.MovieDetailResponse
import com.ilyeong.flickora.core.data.movie.model.NowPlayingResponse
import com.ilyeong.flickora.core.data.movie.model.PopularResponse
import com.ilyeong.flickora.core.data.movie.model.RecommendationListResponse
import com.ilyeong.flickora.core.data.movie.model.ReviewListResponse
import com.ilyeong.flickora.core.data.movie.model.SimilarListResponse
import com.ilyeong.flickora.core.data.movie.model.TopRatedResponse
import com.ilyeong.flickora.core.data.movie.model.TrendingResponse
import com.ilyeong.flickora.core.data.movie.model.UpComingResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

internal interface MovieApiService {

    @GET("movie/{movie_id}")
    suspend fun getMovieDetail(@Path("movie_id") movieId: Int): MovieDetailResponse

    @GET("movie/{movie_id}/credits")
    suspend fun getMovieCredit(@Path("movie_id") movieId: Int): CreditResponse

    @GET("collection/{collection_id}")
    suspend fun getMovieCollection(@Path("collection_id") collectionId: Int): CollectionResponse

    @GET("movie/{movie_id}/recommendations")
    suspend fun getMovieRecommendationList(@Path("movie_id") movieId: Int): RecommendationListResponse

    @GET("movie/{movie_id}/similar")
    suspend fun getMovieSimilarList(@Path("movie_id") movieId: Int): SimilarListResponse

    @GET("discover/movie")
    suspend fun getMovieListByGenre(
        @Query("with_genres") genreId: Int,
        @Query("page") page: Int
    ): DiscoverResponse

    @GET("movie/{movie_id}/reviews")
    suspend fun getMovieReviewList(
        @Path("movie_id") movieId: Int,
        @Query("page") page: Int = 1
    ): ReviewListResponse

    @GET("movie/top_rated")
    suspend fun getTopRatedMovieList(
        @Query("page") page: Int = 1
    ): TopRatedResponse

    @GET("movie/upcoming")
    suspend fun getUpcomingMovieList(
        @Query("page") page: Int = 1
    ): UpComingResponse

    @GET("movie/popular")
    suspend fun getPopularMovieList(
        @Query("page") page: Int = 1
    ): PopularResponse

    @GET("movie/now_playing")
    suspend fun getNowPlayingMovieList(
        @Query("page") page: Int = 1
    ): NowPlayingResponse

    @GET("trending/movie/{time_window}")
    suspend fun getTrendingMovieList(
        @Path("time_window") timeWindow: String,
        @Query("page") page: Int = 1
    ): TrendingResponse

    @GET("genre/movie/list")
    suspend fun getMovieGenreList(): GenreListResponse
}
