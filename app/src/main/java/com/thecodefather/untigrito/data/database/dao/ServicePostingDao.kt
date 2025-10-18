package com.thecodefather.untigrito.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.thecodefather.untigrito.data.database.entity.ServicePostingEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for ServicePosting entities
 */
@Dao
interface ServicePostingDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(posting: ServicePostingEntity)
    
    @Update
    suspend fun update(posting: ServicePostingEntity)
    
    @Delete
    suspend fun delete(posting: ServicePostingEntity)
    
    @Query("SELECT * FROM service_postings WHERE id = :postingId")
    fun getPostingById(postingId: String): Flow<ServicePostingEntity?>
    
    @Query("SELECT * FROM service_postings WHERE clientId = :clientId ORDER BY createdAt DESC")
    fun getPostingsByClient(clientId: String): Flow<List<ServicePostingEntity>>
    
    @Query("SELECT * FROM service_postings WHERE status = :status ORDER BY createdAt DESC")
    fun getPostingsByStatus(status: String): Flow<List<ServicePostingEntity>>
    
    @Query("SELECT * FROM service_postings WHERE category = :category ORDER BY createdAt DESC LIMIT :limit")
    fun getPostingsByCategory(category: String, limit: Int = 10): Flow<List<ServicePostingEntity>>
    
    @Query("SELECT * FROM service_postings ORDER BY createdAt DESC LIMIT :limit OFFSET :offset")
    fun getPostingsPaginated(limit: Int, offset: Int): Flow<List<ServicePostingEntity>>
    
    @Query("SELECT COUNT(*) FROM service_postings")
    fun getPostingCount(): Flow<Int>
}
