package com.thecodefather.untigrito.domain.usecase

import com.thecodefather.untigrito.domain.model.Job
import com.thecodefather.untigrito.domain.repository.JobsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Caso de uso para buscar trabajos por palabras clave
 */
class SearchJobsUseCase @Inject constructor(
    private val repository: JobsRepository
) {
    suspend operator fun invoke(
        query: String,
        page: Int = 1,
        perPage: Int = 20
    ): Result<List<Job>> {
        return try {
            Result.success(repository.searchJobs(query, page, perPage).first())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
