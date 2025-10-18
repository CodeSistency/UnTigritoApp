package com.thecodefather.untigrito.domain.usecase

import com.thecodefather.untigrito.domain.model.Job
import com.thecodefather.untigrito.domain.repository.JobsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Caso de uso para obtener los detalles de un trabajo específico
 */
class GetJobDetailsUseCase @Inject constructor(
    private val repository: JobsRepository
) {
    suspend operator fun invoke(jobId: String): Result<Job?> {
        return try {
            Result.success(repository.getJobById(jobId).first())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
