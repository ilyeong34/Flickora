package com.ilyeong.flickora.core.data.user.api

import com.ilyeong.flickora.core.data.user.BuildConfig
import com.ilyeong.flickora.core.data.user.model.AccountResponse
import com.ilyeong.flickora.core.data.user.model.AccountStatesResponse
import com.ilyeong.flickora.core.data.user.model.WatchlistPostRequest
import com.ilyeong.flickora.core.data.user.model.WatchlistPostResponse
import com.ilyeong.flickora.core.data.user.model.WatchlistMovieResponse
import com.ilyeong.flickora.core.data.user.model.WatchlistTvResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

internal interface UserApiService {

    @GET("account/${BuildConfig.ACCOUNT_ID}")
    suspend fun getAccount(): AccountResponse

    @GET("account/${BuildConfig.ACCOUNT_ID}/watchlist/movies")
    suspend fun getMovieWatchlist(@Query("page") page: Int): WatchlistMovieResponse

    @GET("account/${BuildConfig.ACCOUNT_ID}/watchlist/tv")
    suspend fun getTvWatchlist(@Query("page") page: Int): WatchlistTvResponse

    @GET("movie/{movie_id}/account_states")
    suspend fun getMovieAccountStates(@Path("movie_id") movieId: Int): AccountStatesResponse

    @GET("tv/{series_id}/account_states")
    suspend fun getTvAccountStates(@Path("series_id") tvSeriesId: Int): AccountStatesResponse

    @POST("account/${BuildConfig.ACCOUNT_ID}/watchlist")
    suspend fun addMovieToWatchlist(@Body request: WatchlistPostRequest): WatchlistPostResponse
}
