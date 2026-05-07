package com.ilyeong.movieverse.core.data.oauth.api

import com.ilyeong.movieverse.core.data.oauth.model.RequestTokenResponse
import com.ilyeong.movieverse.core.data.oauth.model.SessionIdRequest
import com.ilyeong.movieverse.core.data.oauth.model.SessionIdResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

internal interface OAuthApiService {

    @GET("authentication/token/new")
    suspend fun createRequestToken(): RequestTokenResponse

    @POST("authentication/session/new")
    suspend fun createSessionId(@Body requestToken: SessionIdRequest): SessionIdResponse
}