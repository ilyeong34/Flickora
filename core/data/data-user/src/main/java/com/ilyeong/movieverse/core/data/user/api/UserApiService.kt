package com.ilyeong.movieverse.core.data.user.api

import com.ilyeong.movieverse.core.data.user.BuildConfig
import com.ilyeong.movieverse.core.data.user.model.AccountResponse
import com.ilyeong.movieverse.core.data.user.model.AccountStatesResponse
import com.ilyeong.movieverse.core.data.user.model.WatchlistPostRequest
import com.ilyeong.movieverse.core.data.user.model.WatchlistPostResponse
import com.ilyeong.movieverse.core.data.user.model.WatchlistResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

internal interface UserApiService {

    @GET("account/${BuildConfig.ACCOUNT_ID}")
    suspend fun getAccount(): AccountResponse

    @GET("account/${BuildConfig.ACCOUNT_ID}/watchlist/movies")
    suspend fun getWatchlist(@Query("page") page: Int): WatchlistResponse

    @GET("movie/{movie_id}/account_states")
    suspend fun getMovieAccountStates(@Path("movie_id") movieId: Int): AccountStatesResponse

    @POST("account/${BuildConfig.ACCOUNT_ID}/watchlist")
    suspend fun addMovieToWatchlist(@Body request: WatchlistPostRequest): WatchlistPostResponse
}
