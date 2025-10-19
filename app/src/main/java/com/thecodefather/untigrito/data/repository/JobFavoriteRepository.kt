package com.thecodefather.untigrito.data.repository

import com.thecodefather.untigrito.data.database.dao.JobFavoriteDao
import com.thecodefather.untigrito.data.database.entity.JobFavoriteEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JobFavoriteRepository @Inject constructor(
    private val jobFavoriteDao: JobFavoriteDao
) {
    suspend fun getFavoriteIdsByUser(userId: String): List<String> =
        jobFavoriteDao.getFavoriteIdsByUser(userId)
    
    suspend fun isFavorite(userId: String, jobId: String): Boolean =
        jobFavoriteDao.getFavorite(userId, jobId) != null
    
    suspend fun addToFavorites(userId: String, jobId: String) {
        val favorite = JobFavoriteEntity(
            id = "${userId}_${jobId}",
            userId = userId,
            jobId = jobId
        )
        jobFavoriteDao.insertFavorite(favorite)
    }
    
    suspend fun removeFromFavorites(userId: String, jobId: String) {
        jobFavoriteDao.deleteFavoriteById(userId, jobId)
    }
    
    suspend fun toggleFavorite(userId: String, jobId: String): Boolean {
        val isCurrentlyFavorite = isFavorite(userId, jobId)
        if (isCurrentlyFavorite) {
            removeFromFavorites(userId, jobId)
            return false
        } else {
            addToFavorites(userId, jobId)
            return true
        }
    }
}
