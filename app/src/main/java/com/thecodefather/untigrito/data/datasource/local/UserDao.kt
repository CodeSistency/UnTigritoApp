package com.thecodefather.untigrito.data.datasource.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for User entity
 *
 * Defines database operations for the User entity using Room.
 * All database queries are defined here.
 */
@Dao
interface UserDao {
    
    /**
     * Retrieves a user by ID
     *
     * @param userId The ID of the user
     * @return Flow emitting the user or null
     */
    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserById(userId: String): Flow<UserEntity?>
    
    /**
     * Retrieves all users
     *
     * @return Flow emitting list of all users
     */
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>
    
    /**
     * Inserts a new user
     *
     * @param user The user to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)
    
    /**
     * Updates an existing user
     *
     * @param user The user to update
     */
    @Update
    suspend fun updateUser(user: UserEntity)
    
    /**
     * Deletes a user by ID
     *
     * @param userId The ID of the user to delete
     */
    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUserById(userId: String)
    
    /**
     * Checks if a user exists
     *
     * @param userId The ID to check
     * @return Flow emitting true if exists, false otherwise
     */
    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE id = :userId)")
    fun userExists(userId: String): Flow<Boolean>
}

