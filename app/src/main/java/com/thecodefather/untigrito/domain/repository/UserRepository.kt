package com.thecodefather.untigrito.domain.repository

import com.thecodefather.untigrito.domain.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for user data operations
 *
 * This interface defines the contract for all user-related data operations.
 * Implementations will handle communication with data sources (local database, remote API).
 *
 * The repository pattern provides abstraction between the domain and data layers,
 * allowing domain logic to remain independent of data sources.
 */
interface UserRepository {
    
    /**
     * Retrieves a user by their ID
     *
     * @param userId The ID of the user to retrieve
     * @return Flow emitting the user or null if not found
     */
    fun getUserById(userId: String): Flow<User?>
    
    /**
     * Retrieves all users
     *
     * @return Flow emitting list of all users
     */
    fun getAllUsers(): Flow<List<User>>
    
    /**
     * Saves or updates a user
     *
     * @param user The user to save
     */
    suspend fun saveUser(user: User)
    
    /**
     * Deletes a user by their ID
     *
     * @param userId The ID of the user to delete
     */
    suspend fun deleteUser(userId: String)
    
    /**
     * Checks if a user exists by their ID
     *
     * @param userId The ID to check
     * @return Flow emitting true if user exists, false otherwise
     */
    fun userExists(userId: String): Flow<Boolean>
}
