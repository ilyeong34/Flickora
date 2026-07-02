package com.ilyeong.flickora.core.data.tv.di

import com.ilyeong.flickora.core.data.tv.repository.TvRepository
import com.ilyeong.flickora.core.data.tv.repository.TvRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class TvModule {

    @Binds
    @Singleton
    abstract fun bindTvRepository(
        tvRepository: TvRepositoryImpl
    ): TvRepository
}
