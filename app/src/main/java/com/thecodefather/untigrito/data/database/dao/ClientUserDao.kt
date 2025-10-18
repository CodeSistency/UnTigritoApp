package com.thecodefather.untigrito.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.thecodefather.untigrito.data.database.entity.ClientUserEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for ClientUser entities
 */
@Dao
interface ClientUserDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: ClientUserEntity)
    
    @Update
    suspend fun update(user: ClientUserEntity)
    
    @Delete
    suspend fun delete(user: ClientUserEntity)
    
    @Query("SELECT * FROM client_users WHERE id = :userId")
    fun getUserById(userId: String): Flow<ClientUserEntity?>
    
    @Query("SELECT * FROM client_users WHERE email = :email LIMIT 1")
    fun getUserByEmail(email: String): Flow<ClientUserEntity?>
    
    @Query("SELECT * FROM client_users WHERE phone = :phone LIMIT 1")
    fun getUserByPhone(phone: String): Flow<ClientUserEntity?>
    
    @Query("SELECT * FROM client_users ORDER BY createdAt DESC LIMIT 1")
    fun getLastUser(): Flow<ClientUserEntity?>
    
    @Query("SELECT COUNT(*) FROM client_users")
    fun getUserCount(): Flow<Int>
}
