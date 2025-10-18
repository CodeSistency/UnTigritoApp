package com.thecodefather.untigrito.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.thecodefather.untigrito.data.database.entity.AuthStateEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for authentication state persistence
 */
@Dao
interface AuthStateDao {

    /**
     * Saves the authentication state (replaces existing)
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAuthState(authState: AuthStateEntity)

    /**
     * Gets the current authentication state
     */
    @Query("SELECT * FROM auth_state WHERE id = 1 LIMIT 1")
    fun getAuthState(): Flow<AuthStateEntity?>

    /**
     * Gets the current authentication state synchronously
     */
    @Query("SELECT * FROM auth_state WHERE id = 1 LIMIT 1")
    suspend fun getAuthStateSync(): AuthStateEntity?

    /**
     * Clears the authentication state
     */
    @Query("DELETE FROM auth_state WHERE id = 1")
    suspend fun clearAuthState()

    /**
     * Checks if there's any persisted auth state
     */
    @Query("SELECT COUNT(*) > 0 FROM auth_state WHERE id = 1")
    suspend fun hasAuthState(): Boolean
}