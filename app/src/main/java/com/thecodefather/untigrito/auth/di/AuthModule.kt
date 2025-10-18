package com.thecodefather.untigrito.auth.di

import com.thecodefather.untigrito.auth.data.repository.AuthRepositoryImpl
import com.thecodefather.untigrito.auth.data.repository.VerificationRepositoryImpl
import com.thecodefather.untigrito.auth.domain.repository.IAuthRepository
import com.thecodefather.untigrito.auth.domain.usecase.AuthStateManager
import com.thecodefather.untigrito.data.database.dao.AuthStateDao
import com.thecodefather.untigrito.data.database.AppDatabase
import com.thecodefather.untigrito.data.datasource.remote.AuthApiService
import com.thecodefather.untigrito.data.datasource.local.TokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {
    
    @Provides
    @Singleton
    fun provideAuthRepository(
        authApiService: AuthApiService,
        tokenManager: TokenManager
    ): IAuthRepository = AuthRepositoryImpl(authApiService, tokenManager)
    
    @Provides
    @Singleton
    fun provideVerificationRepository(): VerificationRepositoryImpl = VerificationRepositoryImpl()


    @Provides
    @Singleton
    fun provideAuthStateDao(appDatabase: AppDatabase): AuthStateDao = appDatabase.authStateDao()

    @Provides
    @Singleton
    fun provideAuthStateManager(authStateDao: AuthStateDao): AuthStateManager = AuthStateManager(authStateDao)
}
