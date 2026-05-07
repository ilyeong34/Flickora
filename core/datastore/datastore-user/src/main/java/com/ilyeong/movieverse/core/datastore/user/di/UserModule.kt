package com.ilyeong.movieverse.core.datastore.user.di

import com.ilyeong.movieverse.core.datastore.user.UserPreferenceDataSource
import com.ilyeong.movieverse.core.datastore.user.UserPreferenceDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class UserModule {

    @Binds
    abstract fun bindUserPreferenceDataSourceImpl(
        dataSource: UserPreferenceDataSourceImpl
    ): UserPreferenceDataSource
}