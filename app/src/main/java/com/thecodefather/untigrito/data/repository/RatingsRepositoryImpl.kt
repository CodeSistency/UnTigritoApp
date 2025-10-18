package com.thecodefather.untigrito.data.repository

import com.thecodefather.untigrito.data.mapper.RatingMapper
import com.thecodefather.untigrito.data.remote.ProfessionalRatingsApiService
import com.thecodefather.untigrito.domain.model.Rating
import com.thecodefather.untigrito.domain.repository.RatingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación del repositorio de calificaciones
 */
@Singleton
class RatingsRepositoryImpl @Inject constructor(
    private val apiService: ProfessionalRatingsApiService
) : RatingsRepository {

    override suspend fun getProfessionalRatings(
        professionalId: String,
        page: Int,
        perPage: Int
    ): Result<List<Rating>> {
        return try {
            val response = apiService.getProfessionalRatings(professionalId, page, perPage)
            if (response.success == true && response.data != null) {
                val ratings: List<Rating> = response.data.data.map { RatingMapper.toDomain(it) }
                Result.success(ratings)
            } else {
                Result.failure(Exception(response.error ?: "Error al obtener calificaciones"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createRating(
        professionalId: String,
        rating: Int,
        comment: String?
    ): Result<Rating> {
        return try {
            // Implementación simplificada - crear una calificación básica
            val newRating = Rating(
                id = "temp_${System.currentTimeMillis()}",
                professionalId = professionalId,
                clientId = "current_user", // Debería obtenerse del contexto
                proposalId = "", // No disponible
                score = rating,
                review = comment ?: "",
                createdAt = java.util.Date(),
                photos = emptyList()
            )
            Result.success(newRating)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateRating(
        ratingId: String,
        rating: Int,
        comment: String?
    ): Result<Rating> {
        return try {
            // Implementación simplificada - devolver una calificación actualizada
            val updatedRating = Rating(
                id = ratingId,
                professionalId = "temp", // Debería obtenerse del original
                clientId = "current_user",
                proposalId = "",
                score = rating,
                review = comment ?: "",
                createdAt = java.util.Date(),
                photos = emptyList()
            )
            Result.success(updatedRating)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteRating(ratingId: String): Result<Boolean> {
        return try {
            // Implementación simplificada
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAverageRating(professionalId: String): Result<Double> {
        return try {
            val response = apiService.getProfessionalStats(professionalId)
            if (response.success == true && response.data != null) {
                val average = response.data["averageRating"] as? Double ?: 0.0
                Result.success(average)
            } else {
                Result.failure(Exception(response.error ?: "Error al obtener promedio"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Método auxiliar para compatibilidad con el módulo Hilt
    fun getRatings(page: Int = 1, perPage: Int = 20): Flow<Result<List<Rating>>> = flow {
        // Implementación simplificada para compatibilidad
        emit(Result.success(emptyList()))
    }
}
