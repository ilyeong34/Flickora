package com.ilyeong.movieverse.core.data.movie.di

import com.ilyeong.movieverse.core.data.movie.api.MovieApiService
import com.ilyeong.movieverse.core.network.MovieverseNetwork
import com.ilyeong.movieverse.core.network.create
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
        movieverseNetwork: MovieverseNetwork,
    ): MovieApiService = movieverseNetwork
        .create<MovieApiService>()
}
