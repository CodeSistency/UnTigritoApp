package com.thecodefather.untigrito.data.preferences

/**
 * Feature Flags para control de funcionalidades experimentales
 * 
 * Permite habilitar/deshabilitar features sin modificar código de producción
 */
object FeatureFlags {
    
    /**
     * Controla si se usa Supabase como fuente principal de datos
     * - true: Usa Supabase y Room como fallback
     * - false: Usa solo Room (comportamiento anterior)
     */
    var useSupabaseIntegration: Boolean = true
    
    /**
     * Controla si se muestran logs de debug para Supabase
     */
    var enableSupabaseLogging: Boolean = true
    
    /**
     * Timeout en segundos para operaciones de Supabase antes de usar fallback
     */
    var supabaseTimeoutSeconds: Long = 10
}

