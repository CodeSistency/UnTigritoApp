package com.thecodefather.untigrito.di

import com.thecodefather.untigrito.data.database.dao.ClientUserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DaoModule {

    @Provides
    @Singleton
    fun provideClientUserDao(appDatabase: AppDatabase): ClientUserDao {
        return appDatabase.clientUserDao()
    }
}
