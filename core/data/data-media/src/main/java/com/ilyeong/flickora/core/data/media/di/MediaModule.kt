package com.ilyeong.flickora.core.data.media.di

import com.ilyeong.flickora.core.data.media.repository.MediaRepository
import com.ilyeong.flickora.core.data.media.repository.MediaRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class MediaModule {

    @Binds
    @Singleton
    abstract fun bindMediaRepository(
        mediaRepository: MediaRepositoryImpl
    ): MediaRepository
}
