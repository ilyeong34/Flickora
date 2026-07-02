package com.ilyeong.flickora.core.datastore.user.di

import com.ilyeong.flickora.core.datastore.user.UserPreferenceDataSource
import com.ilyeong.flickora.core.datastore.user.UserPreferenceDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class UserModule {

    @Binds
    @Singleton
    abstract fun bindUserPreferenceDataSourceImpl(
        dataSource: UserPreferenceDataSourceImpl
    ): UserPreferenceDataSource
}