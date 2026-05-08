package com.ilyeong.movieverse.core.data.oauth.di

import com.ilyeong.movieverse.core.data.oauth.repository.OAuthRepository
import com.ilyeong.movieverse.core.data.oauth.repository.OAuthRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class OAuthModule {

    @Binds
    @Singleton
    abstract fun bindOAuthRepository(
        oAuthRepository: OAuthRepositoryImpl
    ): OAuthRepository
}