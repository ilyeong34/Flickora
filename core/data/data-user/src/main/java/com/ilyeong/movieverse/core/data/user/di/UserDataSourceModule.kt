package com.ilyeong.movieverse.core.data.user.di

import com.ilyeong.movieverse.core.data.user.datasource.UserLocalDataSource
import com.ilyeong.movieverse.core.data.user.datasource.UserLocalDataSourceImpl
import com.ilyeong.movieverse.core.data.user.datasource.UserRemoteDataSource
import com.ilyeong.movieverse.core.data.user.datasource.UserRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class UserDataSourceModule {

    @Binds
    @Singleton
    abstract fun bindUserLocalDataSource(
        userLocalDataSourceImpl: UserLocalDataSourceImpl
    ): UserLocalDataSource

    @Binds
    @Singleton
    abstract fun bindUserRemoteDataSource(
        userRemoteDataSourceImpl: UserRemoteDataSourceImpl
    ): UserRemoteDataSource
}
