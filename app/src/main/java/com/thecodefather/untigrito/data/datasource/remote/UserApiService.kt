package com.thecodefather.untigrito.data.datasource.remote

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Retrofit API service interface for user operations
 *
 * Defines all remote API endpoints for user-related operations.
 * Retrofit will implement this interface at runtime.
 */
interface UserApiService {
    
    /**
     * Retrieves a user by ID from the API
     *
     * @param userId The ID of the user
     * @return The user DTO
     */
    @GET("/api/v1/users/{id}")
    suspend fun getUserById(@Path("id") userId: String): UserDto
    
    /**
     * Retrieves all users from the API
     *
     * @return List of user DTOs
     */
    @GET("/api/v1/users")
    suspend fun getAllUsers(): List<UserDto>
    
    /**
     * Creates a new user via the API
     *
     * @param userDto The user data to create
     * @return The created user DTO
     */
    @POST("/api/v1/users")
    suspend fun createUser(@Body userDto: UserDto): UserDto
    
    /**
     * Updates an existing user via the API
     *
     * @param userId The ID of the user to update
     * @param userDto The updated user data
     * @return The updated user DTO
     */
    @PUT("/api/v1/users/{id}")
    suspend fun updateUser(
        @Path("id") userId: String,
        @Body userDto: UserDto
    ): UserDto
    
    /**
     * Deletes a user via the API
     *
     * @param userId The ID of the user to delete
     */
    @DELETE("/api/v1/users/{id}")
    suspend fun deleteUser(@Path("id") userId: String)
}

