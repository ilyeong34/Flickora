package com.ilyeong.flickora.core.data.media.di

import com.ilyeong.flickora.core.data.media.api.MediaApiService
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
    fun provideMediaApiService(
        flickoraNetwork: FlickoraNetwork,
    ): MediaApiService = flickoraNetwork.create<MediaApiService>()
}
