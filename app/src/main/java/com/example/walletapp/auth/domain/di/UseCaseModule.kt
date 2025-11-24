package com.example.walletapp.auth.domain.di

import com.example.walletapp.auth.domain.repository.AuthRepository
import com.example.walletapp.auth.domain.usecase.GetCurrentUserUseCase
import com.example.walletapp.auth.domain.usecase.SignOutUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides
    @Singleton
    fun provideGetCurrentUserUseCase(repo: AuthRepository): GetCurrentUserUseCase =
        GetCurrentUserUseCase(repo)

    @Provides
    @Singleton
    fun provideSignOutUseCase(repo: AuthRepository): SignOutUseCase =
        SignOutUseCase(repo)
}