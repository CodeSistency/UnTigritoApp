package com.thecodefather.untigrito.di

import android.content.Context
import androidx.room.Room
import com.thecodefather.untigrito.data.database.AppDatabase
import com.thecodefather.untigrito.data.database.dao.ClientRequestDao
import com.thecodefather.untigrito.data.database.dao.ClientUserDao
import com.thecodefather.untigrito.data.database.dao.ServicePostingDao
import com.thecodefather.untigrito.data.database.dao.TransactionDao
import com.thecodefather.untigrito.data.repository.ClientRepositoryImpl
import com.thecodefather.untigrito.domain.repository.ClientRepository
import dagger.Binds
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
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindClientRepository(impl: ClientRepositoryImpl): ClientRepository

    companion object {

        // Networking clients (Retrofit/Ktor) will be provided here
        // TODO: Implement actual API client and its dependencies

        @Provides
        @Singleton
        @JvmStatic
        fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "untigrito_database"
            ).build()
        }

        @Provides
        @JvmStatic
        fun provideClientUserDao(appDatabase: AppDatabase): ClientUserDao {
            return appDatabase.clientUserDao()
        }

        @Provides
        @JvmStatic
        fun provideClientRequestDao(appDatabase: AppDatabase): ClientRequestDao {
            return appDatabase.clientRequestDao()
        }

        @Provides
        @JvmStatic
        fun provideServicePostingDao(appDatabase: AppDatabase): ServicePostingDao {
            return appDatabase.servicePostingDao()
        }

        @Provides
        @JvmStatic
        fun provideTransactionDao(appDatabase: AppDatabase): TransactionDao {
            return appDatabase.transactionDao()
        }
    }
}

