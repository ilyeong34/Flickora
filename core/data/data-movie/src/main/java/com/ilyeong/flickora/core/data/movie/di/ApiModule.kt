package com.ilyeong.flickora.core.data.movie.di

import com.ilyeong.flickora.core.data.movie.api.MovieApiService
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
    fun provideMovieApiService(
        flickoraNetwork: FlickoraNetwork,
    ): MovieApiService = flickoraNetwork
        .create<MovieApiService>()
}
