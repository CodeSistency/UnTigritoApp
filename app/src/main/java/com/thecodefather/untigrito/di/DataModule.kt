package com.thecodefather.untigrito.di

import android.content.Context
import androidx.room.Room
import com.thecodefather.untigrito.data.database.AppDatabase
import com.thecodefather.untigrito.data.database.dao.ClientRequestDao
import com.thecodefather.untigrito.data.database.dao.ClientUserDao
import com.thecodefather.untigrito.data.database.dao.ServicePostingDao
import com.thecodefather.untigrito.data.database.dao.TransactionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt Module for data layer dependencies
 *
 * This module provides dependencies for the data layer including:
 * - API client configuration
 * - Database setup
 * - Repository implementations
 * - Data source configurations
 *
 * Currently empty as data layer will be expanded with specific implementations
 * for API clients, database, and repositories as features are added.
 */
@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    // Networking clients (Retrofit/Ktor) will be provided here
    // TODO: Implement actual API client and its dependencies

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "untigrito_database"
        ).build()
    }

    @Provides
    fun provideClientUserDao(appDatabase: AppDatabase): ClientUserDao {
        return appDatabase.clientUserDao()
    }

    @Provides
    fun provideClientRequestDao(appDatabase: AppDatabase): ClientRequestDao {
        return appDatabase.clientRequestDao()
    }

    @Provides
    fun provideServicePostingDao(appDatabase: AppDatabase): ServicePostingDao {
        return appDatabase.servicePostingDao()
    }

    @Provides
    fun provideTransactionDao(appDatabase: AppDatabase): TransactionDao {
        return appDatabase.transactionDao()
    }
}
