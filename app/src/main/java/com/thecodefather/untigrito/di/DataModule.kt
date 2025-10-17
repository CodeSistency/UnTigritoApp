package com.thecodefather.untigrito.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

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
    // Database and Room setup will be configured here
    // Repository implementations will be provided here
    
}
