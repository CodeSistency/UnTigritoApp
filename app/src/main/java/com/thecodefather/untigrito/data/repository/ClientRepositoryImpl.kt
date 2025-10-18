package com.thecodefather.untigrito.data.repository

import com.thecodefather.untigrito.data.database.dao.ClientRequestDao
import com.thecodefather.untigrito.data.database.dao.ClientUserDao
import com.thecodefather.untigrito.data.database.dao.ServicePostingDao
import com.thecodefather.untigrito.data.database.dao.TransactionDao
import com.thecodefather.untigrito.data.database.entity.ClientRequestEntity
import com.thecodefather.untigrito.data.database.entity.ClientUserEntity
import com.thecodefather.untigrito.data.database.entity.ServicePostingEntity
import com.thecodefather.untigrito.data.database.entity.TransactionEntity
import com.thecodefather.untigrito.domain.model.ClientRequest
import com.thecodefather.untigrito.domain.model.ClientUser
import com.thecodefather.untigrito.domain.model.Professional
import com.thecodefather.untigrito.domain.model.ServicePosting
import com.thecodefather.untigrito.domain.model.Transaction
import com.thecodefather.untigrito.domain.repository.ClientRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementation of ClientRepository
 * Handles data access with local Room cache
 */
class ClientRepositoryImpl @Inject constructor(
    private val clientUserDao: ClientUserDao,
    private val servicePostingDao: ServicePostingDao,
    private val clientRequestDao: ClientRequestDao,
    private val transactionDao: TransactionDao
) : ClientRepository {
    
    // ========== User Operations ==========
    
    override suspend fun saveUser(user: ClientUser) {
        clientUserDao.insert(user.toEntity())
    }
    
    override fun getUserById(userId: String): Flow<ClientUser?> {
        return clientUserDao.getUserById(userId).map { it?.toModel() }
    }
    
    override fun getUserByEmail(email: String): Flow<ClientUser?> {
        return clientUserDao.getUserByEmail(email).map { it?.toModel() }
    }
    
    override fun getUserByPhone(phone: String): Flow<ClientUser?> {
        return clientUserDao.getUserByPhone(phone).map { it?.toModel() }
    }
    
    override suspend fun updateUserBalance(userId: String, newBalance: Double) {
        val user = clientUserDao.getUserById(userId).map { it?.toModel() }
        user.collect { currentUser ->
            if (currentUser != null) {
                val updatedUser = currentUser.copy(balance = newBalance)
                clientUserDao.update(updatedUser.toEntity())
            }
        }
    }
    
    // ========== Service Posting Operations ==========
    
    override suspend fun saveServicePosting(posting: ServicePosting) {
        servicePostingDao.insert(posting.toEntity())
    }
    
    override fun getServicePostingById(postingId: String): Flow<ServicePosting?> {
        return servicePostingDao.getPostingById(postingId).map { it?.toModel() }
    }
    
    override fun getServicePostingsByClient(clientId: String): Flow<List<ServicePosting>> {
        return servicePostingDao.getPostingsByClient(clientId).map { list ->
            list.map { it.toModel() }
        }
    }
    
    override fun getServicePostingsByStatus(status: String): Flow<List<ServicePosting>> {
        return servicePostingDao.getPostingsByStatus(status).map { list ->
            list.map { it.toModel() }
        }
    }
    
    override fun getServicePostingsByCategory(category: String, limit: Int): Flow<List<ServicePosting>> {
        return servicePostingDao.getPostingsByCategory(category, limit).map { list ->
            list.map { it.toModel() }
        }
    }
    
    override fun getServicePostingsPaginated(page: Int, pageSize: Int): Flow<List<ServicePosting>> {
        val offset = (page - 1) * pageSize
        return servicePostingDao.getPostingsPaginated(pageSize, offset).map { list ->
            list.map { it.toModel() }
        }
    }
    
    override suspend fun updateServicePostingStatus(postingId: String, newStatus: String) {
        val posting = servicePostingDao.getPostingById(postingId).map { it?.toModel() }
        posting.collect { currentPosting ->
            if (currentPosting != null) {
                val updatedPosting = currentPosting.copy(status = newStatus)
                servicePostingDao.update(updatedPosting.toEntity())
            }
        }
    }
    
    override suspend fun deleteServicePosting(postingId: String) {
        val posting = servicePostingDao.getPostingById(postingId).map { it?.toModel() }
        posting.collect { currentPosting ->
            if (currentPosting != null) {
                servicePostingDao.delete(currentPosting.toEntity())
            }
        }
    }
    
    // ========== Client Request Operations ==========
    
    override suspend fun saveClientRequest(request: ClientRequest) {
        clientRequestDao.insert(request.toEntity())
    }
    
    override fun getClientRequestById(requestId: String): Flow<ClientRequest?> {
        return clientRequestDao.getRequestById(requestId).map { it?.toModel() }
    }
    
    override fun getClientRequestsByClient(clientId: String): Flow<List<ClientRequest>> {
        return clientRequestDao.getRequestsByClient(clientId).map { list ->
            list.map { it.toModel() }
        }
    }
    
    override fun getClientRequestsByPosting(postingId: String): Flow<List<ClientRequest>> {
        return clientRequestDao.getRequestsByPosting(postingId).map { list ->
            list.map { it.toModel() }
        }
    }
    
    override fun getClientRequestsByStatus(status: String): Flow<List<ClientRequest>> {
        return clientRequestDao.getRequestsByStatus(status).map { list ->
            list.map { it.toModel() }
        }
    }
    
    override suspend fun updateClientRequestStatus(requestId: String, newStatus: String) {
        val request = clientRequestDao.getRequestById(requestId).map { it?.toModel() }
        request.collect { currentRequest ->
            if (currentRequest != null) {
                val updatedRequest = currentRequest.copy(status = newStatus)
                clientRequestDao.update(updatedRequest.toEntity())
            }
        }
    }
    
    override fun getRequestCountForPosting(postingId: String): Flow<Int> {
        return clientRequestDao.getRequestCountForPosting(postingId)
    }
    
    // ========== Transaction Operations ==========
    
    override suspend fun saveTransaction(transaction: Transaction) {
        transactionDao.insert(transaction.toEntity())
    }
    
    override fun getTransactionsByUser(userId: String): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByUser(userId).map { list ->
            list.map { it.toModel() }
        }
    }
    
    override fun getTransactionsByUserAndType(userId: String, type: String): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByUserAndType(userId, type).map { list ->
            list.map { it.toModel() }
        }
    }
    
    override fun getTotalRecharged(userId: String): Flow<Double> {
        return transactionDao.getTotalRecharged(userId).map { it ?: 0.0 }
    }
    
    override fun getTotalWithdrawn(userId: String): Flow<Double> {
        return transactionDao.getTotalWithdrawn(userId).map { it ?: 0.0 }
    }
    
    // ========== Professional Operations ==========
    
    override suspend fun saveProfessional(professional: Professional) {
        // This would require a ProfessionalDao and entity
        // Placeholder for now
    }
    
    override fun getProfessionalById(professionalId: String): Flow<Professional?> {
        // Placeholder - would be implemented with ProfessionalDao
        return kotlinx.coroutines.flow.flowOf(null)
    }
    
    override fun getProfessionalsBySpecialty(specialty: String): Flow<List<Professional>> {
        // Placeholder - would be implemented with ProfessionalDao
        return kotlinx.coroutines.flow.flowOf(emptyList())
    }
    
    // ========== Cache Operations ==========
    
    override suspend fun clearAllCache() {
        // In a real implementation, would clear all tables
        // For now, this is a no-op placeholder
    }
    
    override suspend fun clearUserCache(userId: String) {
        // In a real implementation, would delete user-related data
        // For now, this is a no-op placeholder
    }
    
    // ========== Mapper Functions ==========
    
    private fun ClientUser.toEntity() = ClientUserEntity(
        id = id,
        email = email,
        phone = phone,
        name = name,
        role = role,
        isVerified = isVerified,
        isIDVerified = isIDVerified,
        balance = balance,
        isSuspended = isSuspended,
        createdAt = createdAt,
        updatedAt = updatedAt,
        locationLat = locationLat,
        locationLng = locationLng,
        locationAddress = locationAddress
    )
    
    private fun ClientUserEntity.toModel() = ClientUser(
        id = id,
        email = email,
        phone = phone,
        name = name,
        role = role,
        isVerified = isVerified,
        isIDVerified = isIDVerified,
        balance = balance,
        isSuspended = isSuspended,
        createdAt = createdAt,
        updatedAt = updatedAt,
        locationLat = locationLat,
        locationLng = locationLng,
        locationAddress = locationAddress
    )
    
    private fun ServicePosting.toEntity() = ServicePostingEntity(
        id = id,
        clientId = clientId,
        title = title,
        description = description,
        category = category,
        budget = budget,
        deadline = deadline,
        status = status,
        location = location,
        locationLat = locationLat,
        locationLng = locationLng,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
    
    private fun ServicePostingEntity.toModel() = ServicePosting(
        id = id,
        clientId = clientId,
        title = title,
        description = description,
        category = category,
        budget = budget,
        deadline = deadline,
        status = status,
        location = location,
        locationLat = locationLat,
        locationLng = locationLng,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
    
    private fun ClientRequest.toEntity() = ClientRequestEntity(
        id = id,
        clientId = clientId,
        servicePostingId = servicePostingId,
        professionalId = professionalId,
        status = status,
        proposedPrice = proposedPrice,
        description = description,
        estimatedDuration = estimatedDuration,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
    
    private fun ClientRequestEntity.toModel() = ClientRequest(
        id = id,
        clientId = clientId,
        servicePostingId = servicePostingId,
        professionalId = professionalId,
        status = status,
        proposedPrice = proposedPrice,
        description = description,
        estimatedDuration = estimatedDuration,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
    
    private fun Transaction.toEntity() = TransactionEntity(
        id = id,
        userId = userId,
        type = type,
        amount = amount,
        description = description,
        status = status,
        createdAt = createdAt
    )
    
    private fun TransactionEntity.toModel() = Transaction(
        id = id,
        userId = userId,
        type = type,
        amount = amount,
        description = description,
        status = status,
        createdAt = createdAt
    )
}
