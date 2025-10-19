package com.thecodefather.untigrito.data.repository

import com.thecodefather.untigrito.data.database.dao.ClientRequestDao
import com.thecodefather.untigrito.data.database.dao.ClientUserDao
import com.thecodefather.untigrito.data.database.dao.ServicePostingDao
import com.thecodefather.untigrito.data.database.dao.TransactionDao
import com.thecodefather.untigrito.data.database.entity.ClientRequestEntity
import com.thecodefather.untigrito.data.database.entity.ClientUserEntity
import com.thecodefather.untigrito.data.database.entity.ServicePostingEntity
import com.thecodefather.untigrito.data.database.entity.TransactionEntity
import com.thecodefather.untigrito.data.datasource.remote.SupabaseDatabaseService
import com.thecodefather.untigrito.data.datasource.remote.SupabaseUser
import com.thecodefather.untigrito.data.datasource.remote.SupabaseServicePosting
import com.thecodefather.untigrito.data.datasource.remote.SupabasePayment
import com.thecodefather.untigrito.data.preferences.FeatureFlags
import com.thecodefather.untigrito.domain.model.ClientRequest
import com.thecodefather.untigrito.domain.model.ClientUser
import com.thecodefather.untigrito.domain.model.Professional
import com.thecodefather.untigrito.domain.model.ServicePosting
import com.thecodefather.untigrito.domain.model.Transaction
import com.thecodefather.untigrito.domain.model.toClientUser
import com.thecodefather.untigrito.domain.model.toSupabaseUser
import com.thecodefather.untigrito.domain.model.toServicePosting
import com.thecodefather.untigrito.domain.model.toTransaction
import com.thecodefather.untigrito.domain.repository.ClientRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

/**
 * Implementation of ClientRepository
 * Handles data access with Supabase as primary source and Room as fallback
 */
class ClientRepositoryImpl @Inject constructor(
    private val clientUserDao: ClientUserDao,
    private val servicePostingDao: ServicePostingDao,
    private val clientRequestDao: ClientRequestDao,
    private val transactionDao: TransactionDao,
    private val supabaseDatabaseService: SupabaseDatabaseService
) : ClientRepository {
    
    // ========== User Operations ==========
    
    override suspend fun saveUser(user: ClientUser) {
        if (FeatureFlags.useSupabaseIntegration) {
            try {
                // First check if user exists
                supabaseDatabaseService.getById<SupabaseUser>("User", user.id)
                    .onSuccess { existingUser ->
                        if (existingUser != null) {
                            // User exists, update instead of insert
                            supabaseDatabaseService.update("User", user.id, user.toSupabaseUser())
                                .onSuccess {
                                    Timber.d("User updated in Supabase successfully")
                                }
                                .onFailure { exception ->
                                    Timber.e(exception, "Error updating user in Supabase, using fallback")
                                    clientUserDao.insert(user.toEntity())
                                }
                        } else {
                            // User doesn't exist, insert
                            supabaseDatabaseService.insert("User", user.toSupabaseUser())
                                .onSuccess {
                                    Timber.d("User saved to Supabase successfully")
                                }
                                .onFailure { exception ->
                                    Timber.e(exception, "Error saving user to Supabase, using fallback")
                                    clientUserDao.insert(user.toEntity())
                                }
                        }
                    }
                    .onFailure { exception ->
                        Timber.e(exception, "Error checking user existence in Supabase, trying insert")
                        // If we can't check existence, try insert and handle duplicate key error
                        supabaseDatabaseService.insert("User", user.toSupabaseUser())
                            .onSuccess {
                                Timber.d("User saved to Supabase successfully")
                            }
                            .onFailure { insertException ->
                                if (insertException.message?.contains("duplicate key") == true) {
                                    // User already exists, try update instead
                                    supabaseDatabaseService.update("User", user.id, user.toSupabaseUser())
                                        .onSuccess {
                                            Timber.d("User updated in Supabase after duplicate key error")
                                        }
                                        .onFailure { updateException ->
                                            Timber.e(updateException, "Error updating user after duplicate key, using fallback")
                                            clientUserDao.insert(user.toEntity())
                                        }
                                } else {
                                    Timber.e(insertException, "Error saving user to Supabase, using fallback")
                                    clientUserDao.insert(user.toEntity())
                                }
                            }
                    }
            } catch (e: Exception) {
                Timber.e(e, "Exception saving user to Supabase, using fallback")
                clientUserDao.insert(user.toEntity())
            }
        } else {
            clientUserDao.insert(user.toEntity())
        }
    }
    
    override fun getUserById(userId: String): Flow<ClientUser?> = flow {
        if (FeatureFlags.useSupabaseIntegration) {
            try {
                supabaseDatabaseService.getById<SupabaseUser>("User", userId)
                    .onSuccess { supabaseUser ->
                        emit(supabaseUser?.toClientUser())
                    }
                    .onFailure { exception ->
                        Timber.e(exception, "Error getting user from Supabase, using fallback")
                        clientUserDao.getUserById(userId).collect { entity ->
                            emit(entity?.toModel())
                        }
                    }
            } catch (e: Exception) {
                Timber.e(e, "Exception getting user from Supabase, using fallback")
                clientUserDao.getUserById(userId).collect { entity ->
                    emit(entity?.toModel())
                }
            }
        } else {
            clientUserDao.getUserById(userId).collect { entity ->
                emit(entity?.toModel())
            }
        }
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
        if (FeatureFlags.useSupabaseIntegration) {
            try {
                val supabasePosting = SupabaseServicePosting(
                    id = posting.id,
                    clientId = posting.clientId,
                    title = posting.title,
                    description = posting.description,
                    categoryId = posting.category,
                    budget = posting.budget,
                    status = posting.status,
                    address = posting.location,
                    locationLat = posting.locationLat,
                    locationLng = posting.locationLng,
                    createdAt = posting.createdAt,
                    updatedAt = posting.updatedAt
                )
                supabaseDatabaseService.insert("ServicePosting", supabasePosting)
                    .onFailure { exception ->
                        Timber.e(exception, "Error saving posting to Supabase, using fallback")
                        servicePostingDao.insert(posting.toEntity())
                    }
            } catch (e: Exception) {
                Timber.e(e, "Exception saving posting, using fallback")
                servicePostingDao.insert(posting.toEntity())
            }
        } else {
            servicePostingDao.insert(posting.toEntity())
        }
    }
    
    override fun getServicePostingById(postingId: String): Flow<ServicePosting?> = flow {
        if (FeatureFlags.useSupabaseIntegration) {
            try {
                supabaseDatabaseService.getById<SupabaseServicePosting>("ServicePosting", postingId)
                    .onSuccess { supabasePosting ->
                        emit(supabasePosting?.toServicePosting())
                    }
                    .onFailure {
                        servicePostingDao.getPostingById(postingId).collect { emit(it?.toModel()) }
                    }
            } catch (e: Exception) {
                servicePostingDao.getPostingById(postingId).collect { emit(it?.toModel()) }
            }
        } else {
            servicePostingDao.getPostingById(postingId).collect { emit(it?.toModel()) }
        }
    }
    
    override fun getServicePostingsByClient(clientId: String): Flow<List<ServicePosting>> = flow {
        if (FeatureFlags.useSupabaseIntegration) {
            try {
                supabaseDatabaseService.findBy<SupabaseServicePosting>("ServicePosting", "clientId", clientId)
                    .onSuccess { postings ->
                        emit(postings.map { it.toServicePosting() })
                    }
                    .onFailure {
                        servicePostingDao.getPostingsByClient(clientId).collect { list ->
                            emit(list.map { it.toModel() })
                        }
                    }
            } catch (e: Exception) {
                servicePostingDao.getPostingsByClient(clientId).collect { list ->
                    emit(list.map { it.toModel() })
                }
            }
        } else {
            servicePostingDao.getPostingsByClient(clientId).collect { list ->
                emit(list.map { it.toModel() })
            }
        }
    }
    
    override fun getServicePostingsByStatus(status: String): Flow<List<ServicePosting>> = flow {
        if (FeatureFlags.useSupabaseIntegration) {
            try {
                supabaseDatabaseService.findBy<SupabaseServicePosting>("ServicePosting", "status", status)
                    .onSuccess { postings ->
                        emit(postings.map { it.toServicePosting() })
                    }
                    .onFailure {
                        servicePostingDao.getPostingsByStatus(status).collect { list ->
                            emit(list.map { it.toModel() })
                        }
                    }
            } catch (e: Exception) {
                servicePostingDao.getPostingsByStatus(status).collect { list ->
                    emit(list.map { it.toModel() })
                }
            }
        } else {
            servicePostingDao.getPostingsByStatus(status).collect { list ->
                emit(list.map { it.toModel() })
            }
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
        if (FeatureFlags.useSupabaseIntegration) {
            try {
                val supabasePayment = SupabasePayment(
                    id = transaction.id,
                    userId = transaction.userId,
                    amount = transaction.amount,
                    method = transaction.type,
                    status = transaction.status,
                    details = transaction.description,
                    createdAt = transaction.createdAt
                )
                supabaseDatabaseService.insert("payments", supabasePayment)
                    .onFailure { exception ->
                        Timber.e(exception, "Error saving transaction to Supabase, using fallback")
                        transactionDao.insert(transaction.toEntity())
                    }
            } catch (e: Exception) {
                Timber.e(e, "Exception saving transaction, using fallback")
                transactionDao.insert(transaction.toEntity())
            }
        } else {
            transactionDao.insert(transaction.toEntity())
        }
    }
    
    override fun getTransactionsByUser(userId: String): Flow<List<Transaction>> = flow {
        if (FeatureFlags.useSupabaseIntegration) {
            try {
                supabaseDatabaseService.findBy<SupabasePayment>("payments", "userId", userId)
                    .onSuccess { payments ->
                        emit(payments.map { it.toTransaction() })
                    }
                    .onFailure {
                        transactionDao.getTransactionsByUser(userId).collect { list ->
                            emit(list.map { it.toModel() })
                        }
                    }
            } catch (e: Exception) {
                transactionDao.getTransactionsByUser(userId).collect { list ->
                    emit(list.map { it.toModel() })
                }
            }
        } else {
            transactionDao.getTransactionsByUser(userId).collect { list ->
                emit(list.map { it.toModel() })
            }
        }
    }
    
    override fun getTransactionsByUserAndType(userId: String, type: String): Flow<List<Transaction>> = flow {
        if (FeatureFlags.useSupabaseIntegration) {
            try {
                supabaseDatabaseService.findBy<SupabasePayment>("payments", "userId", userId)
                    .onSuccess { payments ->
                        emit(payments.filter { it.method == type }.map { it.toTransaction() })
                    }
                    .onFailure {
                        transactionDao.getTransactionsByUserAndType(userId, type).collect { list ->
                            emit(list.map { it.toModel() })
                        }
                    }
            } catch (e: Exception) {
                transactionDao.getTransactionsByUserAndType(userId, type).collect { list ->
                    emit(list.map { it.toModel() })
                }
            }
        } else {
            transactionDao.getTransactionsByUserAndType(userId, type).collect { list ->
                emit(list.map { it.toModel() })
            }
        }
    }
    
    override fun getTotalRecharged(userId: String): Flow<Double> = flow {
        if (FeatureFlags.useSupabaseIntegration) {
            try {
                supabaseDatabaseService.findBy<SupabasePayment>("payments", "userId", userId)
                    .onSuccess { payments ->
                        val total = payments.filter { it.method == "RECHARGE" }.sumOf { it.amount }
                        emit(total)
                    }
                    .onFailure {
                        transactionDao.getTotalRecharged(userId).collect { emit(it ?: 0.0) }
                    }
            } catch (e: Exception) {
                transactionDao.getTotalRecharged(userId).collect { emit(it ?: 0.0) }
            }
        } else {
            transactionDao.getTotalRecharged(userId).collect { emit(it ?: 0.0) }
        }
    }
    
    override fun getTotalWithdrawn(userId: String): Flow<Double> = flow {
        if (FeatureFlags.useSupabaseIntegration) {
            try {
                supabaseDatabaseService.findBy<SupabasePayment>("payments", "userId", userId)
                    .onSuccess { payments ->
                        val total = payments.filter { it.method == "WITHDRAWAL" }.sumOf { it.amount }
                        emit(total)
                    }
                    .onFailure {
                        transactionDao.getTotalWithdrawn(userId).collect { emit(it ?: 0.0) }
                    }
            } catch (e: Exception) {
                transactionDao.getTotalWithdrawn(userId).collect { emit(it ?: 0.0) }
            }
        } else {
            transactionDao.getTotalWithdrawn(userId).collect { emit(it ?: 0.0) }
        }
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
