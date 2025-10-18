package com.example.vibecoding3.auth.di

import com.example.vibecoding3.auth.data.repository.AuthRepositoryImpl
import com.example.vibecoding3.auth.data.repository.VerificationRepositoryImpl
import com.example.vibecoding3.auth.domain.repository.IAuthRepository
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
    fun provideAuthRepository(): IAuthRepository = AuthRepositoryImpl()
    
    @Provides
    @Singleton
    fun provideVerificationRepository(): VerificationRepositoryImpl = VerificationRepositoryImpl()
}
