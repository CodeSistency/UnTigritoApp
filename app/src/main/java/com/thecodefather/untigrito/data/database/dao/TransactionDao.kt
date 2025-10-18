package com.thecodefather.untigrito.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.thecodefather.untigrito.data.database.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Transaction entities
 */
@Dao
interface TransactionDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: TransactionEntity)
    
    @Update
    suspend fun update(transaction: TransactionEntity)
    
    @Delete
    suspend fun delete(transaction: TransactionEntity)
    
    @Query("SELECT * FROM transactions WHERE id = :transactionId")
    fun getTransactionById(transactionId: String): Flow<TransactionEntity?>
    
    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY createdAt DESC")
    fun getTransactionsByUser(userId: String): Flow<List<TransactionEntity>>
    
    @Query("SELECT * FROM transactions WHERE userId = :userId AND type = :type ORDER BY createdAt DESC")
    fun getTransactionsByUserAndType(userId: String, type: String): Flow<List<TransactionEntity>>
    
    @Query("SELECT * FROM transactions WHERE userId = :userId AND status = :status ORDER BY createdAt DESC")
    fun getTransactionsByUserAndStatus(userId: String, status: String): Flow<List<TransactionEntity>>
    
    @Query("SELECT SUM(amount) FROM transactions WHERE userId = :userId AND type = 'RECHARGE' AND status = 'COMPLETED'")
    fun getTotalRecharged(userId: String): Flow<Double?>
    
    @Query("SELECT SUM(amount) FROM transactions WHERE userId = :userId AND type = 'WITHDRAWAL' AND status = 'COMPLETED'")
    fun getTotalWithdrawn(userId: String): Flow<Double?>
    
    @Query("SELECT COUNT(*) FROM transactions WHERE userId = :userId")
    fun getTransactionCount(userId: String): Flow<Int>
}
