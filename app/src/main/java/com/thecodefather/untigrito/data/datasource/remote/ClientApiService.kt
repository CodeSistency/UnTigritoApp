package com.thecodefather.untigrito.data.datasource.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Consolidated API service interface for client module
 * Defines all remote API endpoints for client operations
 */
interface ClientApiService {
    
    // ========== Authentication Endpoints ==========
    @POST("/api/auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): ApiResponse<AuthData>
    
    @POST("/api/auth/register")
    suspend fun register(@Body registerRequest: RegisterRequest): ApiResponse<AuthData>
    
    @POST("/api/auth/logout")
    suspend fun logout(): ApiResponse<MessageResponse>
    
    @POST("/api/auth/refresh")
    suspend fun refreshToken(@Body refreshRequest: RefreshRequest): ApiResponse<TokenData>
    
    // ========== User Profile Endpoints ==========
    @GET("/api/users/profile")
    suspend fun getUserProfile(): ApiResponse<UserProfileData>
    
    @PUT("/api/users/profile/update")
    suspend fun updateUserProfile(@Body updateRequest: UpdateProfileRequest): ApiResponse<UserProfileData>
    
    @GET("/api/users/{id}/stats")
    suspend fun getUserStats(@Path("id") userId: String): ApiResponse<UserStatsData>
    
    // ========== Professional Endpoints ==========
    @GET("/api/professionals/list")
    suspend fun listProfessionals(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10,
        @Query("specialty") specialty: String? = null,
        @Query("minRating") minRating: Double? = null,
        @Query("search") search: String? = null
    ): ApiResponse<ProfessionalsListData>
    
    @GET("/api/professionals/{id}")
    suspend fun getProfessionalDetails(@Path("id") professionalId: String): ApiResponse<ProfessionalDetailData>
    
    @GET("/api/professionals/{id}/stats")
    suspend fun getProfessionalStats(@Path("id") professionalId: String): ApiResponse<ProfessionalStatsData>
    
    // ========== Service Posting Endpoints ==========
    @GET("/api/services/postings/list")
    suspend fun listServicePostings(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10,
        @Query("status") status: String? = null,
        @Query("category") category: String? = null,
        @Query("search") search: String? = null
    ): ApiResponse<ServicePostingsListData>
    
    @POST("/api/services/postings/create")
    suspend fun createServicePosting(@Body createRequest: CreatePostingRequest): ApiResponse<ServicePostingData>
    
    @GET("/api/services/postings/{id}")
    suspend fun getServicePostingDetails(@Path("id") postingId: String): ApiResponse<ServicePostingData>
    
    @PUT("/api/services/postings/{id}")
    suspend fun updateServicePosting(
        @Path("id") postingId: String,
        @Body updateRequest: UpdatePostingRequest
    ): ApiResponse<ServicePostingData>
    
    // ========== Service Request/Offer Endpoints ==========
    @POST("/api/services/offers/create")
    suspend fun createServiceOffer(@Body offerRequest: CreateOfferRequest): ApiResponse<OfferData>
    
    @GET("/api/services/postings/{id}/offers")
    suspend fun getServiceOffers(@Path("id") postingId: String): ApiResponse<OffersListData>
    
    @PUT("/api/services/offers/{id}/status")
    suspend fun updateOfferStatus(
        @Path("id") offerId: String,
        @Body statusRequest: UpdateOfferStatusRequest
    ): ApiResponse<OfferData>
    
    // ========== Data Classes ==========
    
    data class LoginRequest(
        val email: String? = null,
        val phone: String? = null,
        val password: String
    )
    
    data class RegisterRequest(
        val email: String? = null,
        val phone: String? = null,
        val password: String,
        val name: String,
        val role: String = "CLIENT"
    )
    
    data class RefreshRequest(
        val token: String
    )
    
    data class UpdateProfileRequest(
        val name: String? = null,
        val phone: String? = null,
        val locationLat: Double? = null,
        val locationLng: Double? = null,
        val locationAddress: String? = null
    )
    
    data class CreatePostingRequest(
        val title: String,
        val description: String,
        val category: String,
        val budget: Double,
        val deadline: String? = null,
        val location: String? = null,
        val locationLat: Double? = null,
        val locationLng: Double? = null
    )
    
    data class UpdatePostingRequest(
        val status: String? = null,
        val title: String? = null,
        val description: String? = null
    )
    
    data class CreateOfferRequest(
        val postingId: String,
        val proposedPrice: Double,
        val description: String,
        val estimatedDuration: Int
    )
    
    data class UpdateOfferStatusRequest(
        val status: String
    )
    
    data class AuthData(
        val user: UserProfileData,
        val token: String,
        val refreshToken: String
    )
    
    data class TokenData(
        val token: String
    )
    
    data class MessageResponse(
        val message: String
    )
    
    data class UserProfileData(
        val id: String,
        val email: String? = null,
        val phone: String? = null,
        val name: String? = null,
        val role: String,
        val isVerified: Boolean,
        val isIDVerified: Boolean,
        val balance: Double,
        val isSuspended: Boolean,
        val createdAt: String,
        val updatedAt: String,
        val locationLat: Double? = null,
        val locationLng: Double? = null,
        val locationAddress: String? = null
    )
    
    data class UserStatsData(
        val totalPostings: Int,
        val totalOffers: Int,
        val totalTransactions: Int,
        val totalReviews: Int,
        val averageRating: Double
    )
    
    data class ProfessionalDetailData(
        val id: String,
        val userId: String,
        val bio: String? = null,
        val rating: Double? = null,
        val totalReviews: Int? = null,
        val yearsOfExperience: Int? = null,
        val specialties: List<String>,
        val hourlyRate: Double? = null,
        val isVerified: Boolean
    )
    
    data class ProfessionalStatsData(
        val totalClients: Int,
        val totalCompletedServices: Int,
        val totalEarnings: Double,
        val averageRating: Double,
        val totalReviews: Int,
        val successRate: Double
    )
    
    data class ServicePostingData(
        val id: String,
        val clientId: String,
        val title: String,
        val description: String,
        val category: String,
        val budget: Double,
        val deadline: String? = null,
        val status: String,
        val location: String? = null,
        val locationLat: Double? = null,
        val locationLng: Double? = null,
        val createdAt: String,
        val updatedAt: String
    )
    
    data class OfferData(
        val id: String,
        val postingId: String,
        val professionalId: String,
        val proposedPrice: Double,
        val description: String,
        val estimatedDuration: Int,
        val status: String,
        val createdAt: String,
        val updatedAt: String
    )
    
    data class ProfessionalsListData(
        val professionals: List<ProfessionalDetailData>,
        val total: Int,
        val page: Int,
        val limit: Int,
        val totalPages: Int
    )
    
    data class ServicePostingsListData(
        val postings: List<ServicePostingData>,
        val total: Int,
        val page: Int,
        val limit: Int,
        val totalPages: Int
    )
    
    data class OffersListData(
        val offers: List<OfferData>
    )
    
    data class ApiResponse<T>(
        val success: Boolean,
        val message: String,
        val data: T? = null,
        val error: ErrorData? = null
    )
    
    data class ErrorData(
        val code: String,
        val message: String,
        val details: Map<String, Any>? = null
    )
}
