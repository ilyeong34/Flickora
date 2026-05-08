package com.ilyeong.movieverse.core.data.user.di

import com.ilyeong.movieverse.core.data.user.repository.UserRepository
import com.ilyeong.movieverse.core.data.user.repository.UserRepositoryImpl
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
    abstract fun bindUserRepository(
        userRepository: UserRepositoryImpl
    ): UserRepository
}