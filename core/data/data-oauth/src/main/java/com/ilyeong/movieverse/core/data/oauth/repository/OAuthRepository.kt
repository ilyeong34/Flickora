package com.ilyeong.movieverse.core.data.oauth.repository

import com.ilyeong.movieverse.core.model.RequestToken
import kotlinx.coroutines.flow.Flow

interface OAuthRepository {

    fun createRequestToken(): Flow<RequestToken>
    fun createSessionId(requestToken: String): Flow<Unit>
    fun logout(): Flow<Unit>
}