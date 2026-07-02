package com.ilyeong.flickora.core.data.oauth.di

import com.ilyeong.flickora.core.data.oauth.api.OAuthApiService
import com.ilyeong.flickora.core.network.FlickoraNetwork
import com.ilyeong.flickora.core.network.create
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object ApiModule {

    @Provides
    @Singleton
    fun provideOAuthApiService(
        flickoraNetwork: FlickoraNetwork,
    ): OAuthApiService = flickoraNetwork
        .create<OAuthApiService>()

}
