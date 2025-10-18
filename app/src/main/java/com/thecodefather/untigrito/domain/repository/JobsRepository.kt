package com.thecodefather.untigrito.domain.repository

import com.thecodefather.untigrito.domain.model.Job
import com.thecodefather.untigrito.domain.model.JobFilter
import kotlinx.coroutines.flow.Flow

interface JobsRepository {
    suspend fun getJobs(filter: JobFilter, searchQuery: String = ""): Flow<List<Job>>
    suspend fun searchJobs(query: String, page: Int = 1, perPage: Int = 20): Flow<List<Job>>
    suspend fun getJobById(jobId: String): Flow<Job?>
    suspend fun toggleFavorite(jobId: String): Flow<Boolean>
    suspend fun getFavoriteJobs(): Flow<List<Job>>
    suspend fun getRecommendedJobs(): Flow<List<Job>>
}