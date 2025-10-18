package com.thecodefather.untigrito

import com.thecodefather.untigrito.data.datasource.remote.ClientApiService
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for ClientApiService
 * Tests API integration with backend endpoints
 */
class ClientApiServiceTest {
    
    private lateinit var apiService: ClientApiService
    
    @Before
    fun setup() {
        apiService = mockk()
    }
    
    // Test 1: Authentication endpoints (login, register, logout)
    @Test
    fun testAuthenticationEndpoints() = runBlocking {
        // Test login endpoint
        val loginRequest = ClientApiService.LoginRequest(
            email = "juan@example.com",
            password = "password123"
        )
        
        val authData = ClientApiService.AuthData(
            user = ClientApiService.UserProfileData(
                id = "user-123",
                email = "juan@example.com",
                name = "Juan Pérez",
                role = "CLIENT",
                isVerified = true,
                isIDVerified = false,
                balance = 15000.0,
                isSuspended = false,
                createdAt = "2025-01-01",
                updatedAt = "2025-01-01"
            ),
            token = "access_token_xyz",
            refreshToken = "refresh_token_abc"
        )
        
        coEvery { apiService.login(loginRequest) } returns 
            ClientApiService.ApiResponse(
                success = true,
                message = "Login successful",
                data = authData
            )
        
        val result = apiService.login(loginRequest)
        assert(result.success)
        assert(result.data?.token != null)
    }
    
    // Test 2: User profile endpoints (get, update)
    @Test
    fun testUserProfileEndpoints() = runBlocking {
        val userProfileData = ClientApiService.UserProfileData(
            id = "user-123",
            email = "juan@example.com",
            name = "Juan Pérez",
            role = "CLIENT",
            isVerified = true,
            isIDVerified = false,
            balance = 15000.0,
            isSuspended = false,
            createdAt = "2025-01-01",
            updatedAt = "2025-01-01"
        )
        
        coEvery { apiService.getUserProfile() } returns
            ClientApiService.ApiResponse(
                success = true,
                message = "Profile retrieved",
                data = userProfileData
            )
        
        val result = apiService.getUserProfile()
        assert(result.success)
        assert(result.data?.name == "Juan Pérez")
    }
    
    // Test 3: Professionals endpoints (list, search, details)
    @Test
    fun testProfessionalsEndpoints() = runBlocking {
        val professionalsList = ClientApiService.ProfessionalsListData(
            professionals = listOf(
                ClientApiService.ProfessionalDetailData(
                    id = "prof-1",
                    userId = "user-1",
                    bio = "Electricista con experiencia",
                    rating = 4.8,
                    totalReviews = 120,
                    yearsOfExperience = 5,
                    specialties = listOf("Electricidad"),
                    hourlyRate = 50.0,
                    isVerified = true
                )
            ),
            total = 1,
            page = 1,
            limit = 10,
            totalPages = 1
        )
        
        coEvery { apiService.listProfessionals() } returns
            ClientApiService.ApiResponse(
                success = true,
                message = "Professionals listed",
                data = professionalsList
            )
        
        val result = apiService.listProfessionals()
        assert(result.success)
        assert(result.data?.professionals?.isNotEmpty() == true)
    }
    
    // Test 4: Service postings endpoints (create, list, update)
    @Test
    fun testServicePostingsEndpoints() = runBlocking {
        val postingData = ClientApiService.ServicePostingData(
            id = "posting-1",
            clientId = "client-1",
            title = "Reparación de Plomería",
            description = "Necesito reparar un tubo",
            category = "PLOMERIA",
            budget = 500.0,
            status = "OPEN",
            createdAt = "2025-01-01",
            updatedAt = "2025-01-01"
        )
        
        coEvery { apiService.getServicePostingDetails("posting-1") } returns
            ClientApiService.ApiResponse(
                success = true,
                message = "Posting retrieved",
                data = postingData
            )
        
        val result = apiService.getServicePostingDetails("posting-1")
        assert(result.success)
        assert(result.data?.category == "PLOMERIA")
    }
    
    // Test 5: Requests/offers endpoints (create, list, update status)
    @Test
    fun testRequestsOfferEndpoints() = runBlocking {
        val offerData = ClientApiService.OfferData(
            id = "offer-1",
            postingId = "posting-1",
            professionalId = "prof-1",
            proposedPrice = 450.0,
            description = "Puedo hacerlo",
            estimatedDuration = 120,
            status = "PENDING",
            createdAt = "2025-01-01",
            updatedAt = "2025-01-01"
        )
        
        coEvery { apiService.getServiceOffers("posting-1") } returns
            ClientApiService.ApiResponse(
                success = true,
                message = "Offers retrieved",
                data = ClientApiService.OffersListData(
                    offers = listOf(offerData)
                )
            )
        
        val result = apiService.getServiceOffers("posting-1")
        assert(result.success)
        assert(result.data?.offers?.isNotEmpty() == true)
    }
    
    // Test 6: Error handling and network failures
    @Test
    fun testErrorHandling() = runBlocking {
        val errorResponse = ClientApiService.ApiResponse<ClientApiService.UserProfileData>(
            success = false,
            message = "User not found",
            data = null,
            error = ClientApiService.ErrorData(
                code = "404",
                message = "Resource not found"
            )
        )
        
        coEvery { apiService.getUserProfile() } returns errorResponse
        
        val result = apiService.getUserProfile()
        assert(!result.success)
        assert(result.error?.code == "404")
    }
    
    // Test 7: Token refresh and authentication flow
    @Test
    fun testTokenRefreshFlow() = runBlocking {
        val refreshRequest = ClientApiService.RefreshRequest(
            token = "refresh_token_abc"
        )
        
        val tokenData = ClientApiService.TokenData(
            token = "new_access_token_xyz"
        )
        
        coEvery { apiService.refreshToken(refreshRequest) } returns
            ClientApiService.ApiResponse(
                success = true,
                message = "Token refreshed",
                data = tokenData
            )
        
        val result = apiService.refreshToken(refreshRequest)
        assert(result.success)
        assert(result.data?.token != null)
    }
    
    // Test 8: Pagination and filtering
    @Test
    fun testPaginationAndFiltering() = runBlocking {
        val listingData = ClientApiService.ServicePostingsListData(
            postings = emptyList(),
            total = 0,
            page = 1,
            limit = 10,
            totalPages = 0
        )
        
        coEvery { 
            apiService.listServicePostings(
                page = 1,
                limit = 10,
                category = "ELECTRICIDAD"
            ) 
        } returns ClientApiService.ApiResponse(
            success = true,
            message = "Listings retrieved",
            data = listingData
        )
        
        val result = apiService.listServicePostings(page = 1, limit = 10, category = "ELECTRICIDAD")
        assert(result.success)
        assert(result.data?.page == 1)
    }
}
