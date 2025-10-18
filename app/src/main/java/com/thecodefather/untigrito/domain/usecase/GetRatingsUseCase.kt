package com.thecodefather.untigrito.domain.usecase

import com.thecodefather.untigrito.domain.model.Rating
import com.thecodefather.untigrito.domain.repository.RatingsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Caso de uso para obtener las calificaciones
 */
class GetRatingsUseCase @Inject constructor(
    private val repository: RatingsRepository
) {
    suspend operator fun invoke(
        professionalId: String,
        page: Int = 1,
        perPage: Int = 20
    ): Result<List<Rating>> {
        return try {
            repository.getProfessionalRatings(professionalId, page, perPage)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
