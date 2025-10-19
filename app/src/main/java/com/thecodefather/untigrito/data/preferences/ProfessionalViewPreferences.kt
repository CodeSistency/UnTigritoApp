package com.thecodefather.untigrito.data.preferences

import android.content.Context
import android.content.SharedPreferences

/**
 * Clase para manejar las preferencias de vista profesional
 * 
 * Permite guardar y recuperar el estado de si el usuario está en la vista de profesional
 * El valor por defecto es false (no está en vista profesional)
 */
class ProfessionalViewPreferences(context: Context) {
    
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME, 
        Context.MODE_PRIVATE
    )
    
    companion object {
        private const val PREFS_NAME = "professional_view_preferences"
        private const val KEY_IS_IN_PROFESSIONAL_VIEW = "is_in_professional_view"
    }
    
    /**
     * Guarda el estado de si el usuario está en la vista profesional
     * @param isInProfessionalView true si está en vista profesional, false si no
     */
    fun setInProfessionalView(isInProfessionalView: Boolean) {
        sharedPreferences.edit()
            .putBoolean(KEY_IS_IN_PROFESSIONAL_VIEW, isInProfessionalView)
            .apply()
    }
    
    /**
     * Obtiene el estado de si el usuario está en la vista profesional
     * @return true si está en vista profesional, false si no (por defecto false)
     */
    fun isInProfessionalView(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_IN_PROFESSIONAL_VIEW, false)
    }
    
    /**
     * Cambia el estado de vista profesional al opuesto
     * @return el nuevo estado después del cambio
     */
    fun toggleProfessionalView(): Boolean {
        val currentState = isInProfessionalView()
        val newState = !currentState
        setInProfessionalView(newState)
        return newState
    }
    
    /**
     * Resetea el estado a false (no está en vista profesional)
     */
    fun resetToClientView() {
        setInProfessionalView(false)
    }
    
    /**
     * Establece explícitamente la vista a profesional
     */
    fun setToProfessionalView() {
        setInProfessionalView(true)
    }
    
    /**
     * Establece explícitamente la vista a cliente
     */
    fun setToClientView() {
        setInProfessionalView(false)
    }
    
    /**
     * Limpia todas las preferencias de vista profesional
     */
    fun clear() {
        sharedPreferences.edit()
            .remove(KEY_IS_IN_PROFESSIONAL_VIEW)
            .apply()
    }
}
