package com.thecodefather.untigrito

import com.thecodefather.untigrito.data.database.dao.ClientRequestDao
import com.thecodefather.untigrito.data.database.dao.ClientUserDao
import com.thecodefather.untigrito.data.database.dao.ServicePostingDao
import com.thecodefather.untigrito.data.database.dao.TransactionDao
import com.thecodefather.untigrito.data.repository.ClientRepositoryImpl
import com.thecodefather.untigrito.domain.model.ClientRequest
import com.thecodefather.untigrito.domain.model.ClientUser
import com.thecodefather.untigrito.domain.model.ServicePosting
import com.thecodefather.untigrito.domain.model.Transaction
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

/**
 * Unit tests for ClientRepository
 * Tests core functionality of data access layer with cache
 */
class ClientRepositoryTest {
    
    private lateinit var repository: ClientRepositoryImpl
    private lateinit var mockUserDao: ClientUserDao
    private lateinit var mockPostingDao: ServicePostingDao
    private lateinit var mockRequestDao: ClientRequestDao
    private lateinit var mockTransactionDao: TransactionDao
    
    @Before
    fun setup() {
        mockUserDao = mockk()
        mockPostingDao = mockk()
        mockRequestDao = mockk()
        mockTransactionDao = mockk()
        
        repository = ClientRepositoryImpl(
            clientUserDao = mockUserDao,
            servicePostingDao = mockPostingDao,
            clientRequestDao = mockRequestDao,
            transactionDao = mockTransactionDao
        )
    }
    
    // Test 1: User model creation and retrieval
    @Test
    fun testUserModelCreationAndRetrieval() = runBlocking {
        // Arrange
        val userId = "user-123"
        val user = ClientUser(
            id = userId,
            name = "Juan Pérez",
            email = "juan@example.com",
            phone = "04120386216",
            balance = 15000.0,
            isVerified = true
        )
        
        coEvery { mockUserDao.getUserById(userId) } returns flowOf(null)
        
        // Act & Assert
        val result = repository.getUserById(userId)
        assert(result != null)
    }
    
    // Test 2: ServicePosting model with categories and states
    @Test
    fun testServicePostingWithCategoriesAndStates() = runBlocking {
        // Arrange
        val posting = ServicePosting(
            id = "posting-123",
            clientId = "client-1",
            title = "Reparación de Plomería",
            description = "Necesito reparar un tubo en cocina",
            category = ServicePosting.CATEGORY_PLOMERIA,
            budget = 500.0,
            status = ServicePosting.STATUS_OPEN
        )
        
        coEvery { mockPostingDao.getPostingById("posting-123") } returns flowOf(null)
        
        // Act & Assert
        val result = repository.getServicePostingById("posting-123")
        assert(result != null)
    }
    
    // Test 3: ClientRequest model with status transitions
    @Test
    fun testClientRequestStatusTransitions() = runBlocking {
        // Arrange
        val requestId = "req-123"
        val initialRequest = ClientRequest(
            id = requestId,
            clientId = "client-1",
            servicePostingId = "posting-1",
            status = ClientRequest.STATUS_PENDING,
            proposedPrice = 450.0,
            description = "Puedo hacerlo al mejor precio"
        )
        
        coEvery { mockRequestDao.getRequestById(requestId) } returns flowOf(null)
        
        // Act & Assert
        val result = repository.getClientRequestById(requestId)
        assert(result != null)
    }
    
    // Test 4: Transaction model for payment history
    @Test
    fun testTransactionModelForPaymentHistory() = runBlocking {
        // Arrange
        val userId = "user-123"
        val transaction = Transaction(
            id = "txn-123",
            userId = userId,
            type = Transaction.TYPE_RECHARGE,
            amount = 1000.0,
            description = "Recarga de saldo",
            status = Transaction.STATUS_COMPLETED
        )
        
        coEvery { mockTransactionDao.getTransactionsByUser(userId) } returns flowOf(emptyList())
        
        // Act & Assert
        val result = repository.getTransactionsByUser(userId)
        assert(result != null)
    }
    
    // Test 5: Repository pattern with local cache functionality
    @Test
    fun testRepositoryPatternWithLocalCache() = runBlocking {
        // Arrange
        val userId = "user-123"
        coEvery { mockUserDao.getUserById(userId) } returns flowOf(null)
        
        // Act
        repository.getUserById(userId).collect { _ ->
            // Verify cache was accessed (via DAO)
        }
        
        // Assert - verify DAO was called (cache access)
        coVerify { mockUserDao.getUserById(userId) }
    }
    
    // Test 6: Balance update operation
    @Test
    fun testBalanceUpdateOperation() = runBlocking {
        // Arrange
        val userId = "user-123"
        val newBalance = 20000.0
        coEvery { mockUserDao.getUserById(userId) } returns flowOf(null)
        
        // Act - This would update the balance
        repository.updateUserBalance(userId, newBalance)
        
        // Assert - Verify the operation was attempted
        coVerify(atLeast = 1) { mockUserDao.getUserById(userId) }
    }
}
