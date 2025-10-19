package com.thecodefather.untigrito.data.database.dao

import androidx.room.*
import com.thecodefather.untigrito.data.database.entity.JobFavoriteEntity

@Dao
interface JobFavoriteDao {
    @Query("SELECT jobId FROM job_favorites WHERE userId = :userId")
    suspend fun getFavoriteIdsByUser(userId: String): List<String>
    
    @Query("SELECT * FROM job_favorites WHERE userId = :userId AND jobId = :jobId")
    suspend fun getFavorite(userId: String, jobId: String): JobFavoriteEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: JobFavoriteEntity)
    
    @Query("DELETE FROM job_favorites WHERE userId = :userId AND jobId = :jobId")
    suspend fun deleteFavoriteById(userId: String, jobId: String)
}
