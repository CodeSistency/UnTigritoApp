package com.thecodefather.untigrito.domain.repository

import com.thecodefather.untigrito.domain.model.Rating
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz del repositorio para operaciones de Calificaciones
 */
interface RatingsRepository {
    
    /**
     * Obtiene las calificaciones de un profesional
     */
    suspend fun getProfessionalRatings(
        professionalId: String,
        page: Int = 1,
        perPage: Int = 20
    ): Result<List<Rating>>
    
    /**
     * Crea una nueva calificación
     */
    suspend fun createRating(
        professionalId: String,
        rating: Int,
        comment: String? = null
    ): Result<Rating>
    
    /**
     * Actualiza una calificación existente
     */
    suspend fun updateRating(
        ratingId: String,
        rating: Int,
        comment: String? = null
    ): Result<Rating>
    
    /**
     * Elimina una calificación
     */
    suspend fun deleteRating(ratingId: String): Result<Boolean>
    
    /**
     * Obtiene el promedio de calificaciones de un profesional
     */
    suspend fun getAverageRating(professionalId: String): Result<Double>
}
