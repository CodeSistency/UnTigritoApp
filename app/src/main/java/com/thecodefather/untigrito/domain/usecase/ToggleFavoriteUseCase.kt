package com.thecodefather.untigrito.domain.usecase

import com.thecodefather.untigrito.domain.repository.JobsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Caso de uso para marcar/desmarcar un trabajo como favorito
 */
class ToggleFavoriteUseCase @Inject constructor(
    private val repository: JobsRepository
) {
    suspend operator fun invoke(jobId: String): Result<Boolean> {
        return try {
            val result = repository.toggleFavorite(jobId).first()
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
