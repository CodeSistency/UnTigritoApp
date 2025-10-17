package com.thecodefather.untigrito.domain.usecase

import com.thecodefather.untigrito.domain.model.User
import com.thecodefather.untigrito.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving a user by ID
 *
 * This use case encapsulates the business logic for fetching a user.
 * It follows the single responsibility principle by focusing solely on
 * user retrieval operations.
 *
 * @param userRepository The repository to fetch user data from
 */
class GetUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    
    /**
     * Executes the use case to get a user by ID
     *
     * @param userId The ID of the user to retrieve
     * @return Flow emitting the user or null if not found
     */
    fun execute(userId: String): Flow<User?> {
        return userRepository.getUserById(userId)
    }
}
