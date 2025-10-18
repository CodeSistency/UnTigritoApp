package com.thecodefather.untigrito.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.thecodefather.untigrito.data.database.entity.ClientRequestEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for ClientRequest entities
 */
@Dao
interface ClientRequestDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(request: ClientRequestEntity)
    
    @Update
    suspend fun update(request: ClientRequestEntity)
    
    @Delete
    suspend fun delete(request: ClientRequestEntity)
    
    @Query("SELECT * FROM client_requests WHERE id = :requestId")
    fun getRequestById(requestId: String): Flow<ClientRequestEntity?>
    
    @Query("SELECT * FROM client_requests WHERE clientId = :clientId ORDER BY createdAt DESC")
    fun getRequestsByClient(clientId: String): Flow<List<ClientRequestEntity>>
    
    @Query("SELECT * FROM client_requests WHERE servicePostingId = :postingId ORDER BY createdAt DESC")
    fun getRequestsByPosting(postingId: String): Flow<List<ClientRequestEntity>>
    
    @Query("SELECT * FROM client_requests WHERE servicePostingId = :postingId AND status = :status")
    fun getRequestsByPostingAndStatus(postingId: String, status: String): Flow<List<ClientRequestEntity>>
    
    @Query("SELECT * FROM client_requests WHERE status = :status ORDER BY createdAt DESC LIMIT :limit")
    fun getRequestsByStatus(status: String, limit: Int = 50): Flow<List<ClientRequestEntity>>
    
    @Query("SELECT COUNT(*) FROM client_requests WHERE servicePostingId = :postingId")
    fun getRequestCountForPosting(postingId: String): Flow<Int>
}
