package com.thecodefather.untigrito.data.datasource.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage
import io.ktor.client.engine.android.*

/**
 * Cliente de Supabase para la aplicación UnTigrito
 * 
 * Este objeto proporciona acceso al cliente de Supabase configurado con:
 * - Postgrest: Para consultas a la base de datos
 * - GoTrue: Para autenticación
 * - Realtime: Para actualizaciones en tiempo real
 * - Storage: Para almacenamiento de archivos
 * 
 * IMPORTANTE: Configura las siguientes variables de entorno en tu proyecto:
 * - SUPABASE_URL: La URL de tu proyecto de Supabase
 * - SUPABASE_ANON_KEY: La clave anónima pública de tu proyecto
 * 
 * Para desarrollo, puedes crear un archivo local.properties con:
 * supabase.url=https://tu-proyecto.supabase.co
 * supabase.anonKey=tu-clave-anon
 */
object SupabaseClientProvider {
    
    // Credenciales de Supabase
    // Proyecto: wcyyphrkkudovnizwpsr
    private const val SUPABASE_URL = "https://wcyyphrkkudovnizwpsr.supabase.co"
    private const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6IndjeXlwaHJra3Vkb3ZuaXp3cHNyIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjA3MTc5MzksImV4cCI6MjA3NjI5MzkzOX0.3b60CZ0veGu8JqCoqk7E5yLMU2zAjA2YO_ca2RwUKpw"
    
    /**
     * Cliente de Supabase configurado con todos los módulos necesarios
     */
    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_ANON_KEY
        ) {
            // Motor HTTP de Android para Ktor
            httpEngine = Android.create()
            
            // Módulo Postgrest para consultas a la base de datos
            install(Postgrest)
            
            // Módulo GoTrue para autenticación
            install(Auth) {
                // Configuración adicional de autenticación si es necesaria
                // flowType = FlowType.PKCE
                // scheme = "app"
                // host = "supabase.com"
            }
            
            // Módulo Realtime para actualizaciones en tiempo real
            install(Realtime)
            
            // Módulo Storage para almacenamiento de archivos
            install(Storage)
        }
    }
    
    /**
     * Acceso directo al módulo de autenticación
     */
    val auth get() = client.pluginManager.getPlugin(Auth)
    
    /**
     * Acceso directo al módulo de base de datos
     */
    val database get() = client.pluginManager.getPlugin(Postgrest)
    
    /**
     * Acceso directo al módulo de tiempo real
     */
    val realtime get() = client.pluginManager.getPlugin(Realtime)
    
    /**
     * Acceso directo al módulo de almacenamiento
     */
    val storage get() = client.pluginManager.getPlugin(Storage)
}

