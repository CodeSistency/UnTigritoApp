package com.thecodefather.untigrito.domain.repository

import com.thecodefather.untigrito.domain.model.ClientRequest
import com.thecodefather.untigrito.domain.model.ClientUser
import com.thecodefather.untigrito.domain.model.Professional
import com.thecodefather.untigrito.domain.model.ServicePosting
import com.thecodefather.untigrito.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for client module data operations
 * Defines contract for accessing and managing client-related data
 */
interface ClientRepository {
    
    // User operations
    suspend fun saveUser(user: ClientUser)
    fun getUserById(userId: String): Flow<ClientUser?>
    fun getUserByEmail(email: String): Flow<ClientUser?>
    fun getUserByPhone(phone: String): Flow<ClientUser?>
    suspend fun updateUserBalance(userId: String, newBalance: Double)
    
    // Service Posting operations
    suspend fun saveServicePosting(posting: ServicePosting)
    fun getServicePostingById(postingId: String): Flow<ServicePosting?>
    fun getServicePostingsByClient(clientId: String): Flow<List<ServicePosting>>
    fun getServicePostingsByStatus(status: String): Flow<List<ServicePosting>>
    fun getServicePostingsByCategory(category: String, limit: Int = 10): Flow<List<ServicePosting>>
    fun getServicePostingsPaginated(page: Int, pageSize: Int): Flow<List<ServicePosting>>
    suspend fun updateServicePostingStatus(postingId: String, newStatus: String)
    suspend fun deleteServicePosting(postingId: String)
    
    // Client Request operations
    suspend fun saveClientRequest(request: ClientRequest)
    fun getClientRequestById(requestId: String): Flow<ClientRequest?>
    fun getClientRequestsByClient(clientId: String): Flow<List<ClientRequest>>
    fun getClientRequestsByPosting(postingId: String): Flow<List<ClientRequest>>
    fun getClientRequestsByStatus(status: String): Flow<List<ClientRequest>>
    suspend fun updateClientRequestStatus(requestId: String, newStatus: String)
    fun getRequestCountForPosting(postingId: String): Flow<Int>
    
    // Transaction operations
    suspend fun saveTransaction(transaction: Transaction)
    fun getTransactionsByUser(userId: String): Flow<List<Transaction>>
    fun getTransactionsByUserAndType(userId: String, type: String): Flow<List<Transaction>>
    fun getTotalRecharged(userId: String): Flow<Double>
    fun getTotalWithdrawn(userId: String): Flow<Double>
    
    // Professional operations
    suspend fun saveProfessional(professional: Professional)
    fun getProfessionalById(professionalId: String): Flow<Professional?>
    fun getProfessionalsBySpecialty(specialty: String): Flow<List<Professional>>
    
    // Cache operations
    suspend fun clearAllCache()
    suspend fun clearUserCache(userId: String)
}
