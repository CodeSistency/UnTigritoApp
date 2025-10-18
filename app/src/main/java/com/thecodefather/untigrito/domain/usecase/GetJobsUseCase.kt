package com.thecodefather.untigrito.domain.usecase

import com.thecodefather.untigrito.domain.model.Job
import com.thecodefather.untigrito.domain.model.JobFilter
import com.thecodefather.untigrito.domain.repository.JobsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Caso de uso para obtener la lista de trabajos
 */
class GetJobsUseCase @Inject constructor(
    private val repository: JobsRepository
) {
    suspend operator fun invoke(
        filter: JobFilter = JobFilter.RECENT,
        searchQuery: String = ""
    ): Result<List<Job>> {
        return try {
            Result.success(repository.getJobs(filter, searchQuery).first())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
