package com.thecodefather.untigrito.di

import com.thecodefather.untigrito.data.datasource.remote.SupabaseClientProvider
import com.thecodefather.untigrito.data.datasource.remote.SupabaseDatabaseService
import com.thecodefather.untigrito.data.datasource.remote.SupabaseStorageService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage
import javax.inject.Singleton

/**
 * Módulo de Hilt para proporcionar dependencias de Supabase
 * 
 * Este módulo permite inyectar el cliente de Supabase y sus módulos
 * en cualquier parte de la aplicación que lo necesite.
 */
@Module
@InstallIn(SingletonComponent::class)
object SupabaseModule {

    /**
     * Proporciona el cliente de Supabase como singleton
     */
    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient {
        return SupabaseClientProvider.client
    }

    /**
     * Proporciona el módulo de autenticación de Supabase
     */
    @Provides
    @Singleton
    fun provideSupabaseAuth(client: SupabaseClient): Auth {
        return client.pluginManager.getPlugin(Auth)
    }

    /**
     * Proporciona el módulo de base de datos (Postgrest) de Supabase
     */
    @Provides
    @Singleton
    fun provideSupabaseDatabase(client: SupabaseClient): Postgrest {
        return client.pluginManager.getPlugin(Postgrest)
    }

    /**
     * Proporciona el módulo de tiempo real de Supabase
     */
    @Provides
    @Singleton
    fun provideSupabaseRealtime(client: SupabaseClient): Realtime {
        return client.pluginManager.getPlugin(Realtime)
    }

    /**
     * Proporciona el módulo de almacenamiento de Supabase
     */
    @Provides
    @Singleton
    fun provideSupabaseStorage(client: SupabaseClient): Storage {
        return client.pluginManager.getPlugin(Storage)
    }
    
    /**
     * Proporciona el servicio de base de datos de Supabase
     */
    @Provides
    @Singleton
    fun provideSupabaseDatabaseService(postgrest: Postgrest): SupabaseDatabaseService {
        return SupabaseDatabaseService(postgrest)
    }
    
    /**
     * Proporciona el servicio de almacenamiento de Supabase
     */
    @Provides
    @Singleton
    fun provideSupabaseStorageService(storage: Storage): SupabaseStorageService {
        return SupabaseStorageService(storage)
    }
}

