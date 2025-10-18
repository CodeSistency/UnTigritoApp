package com.thecodefather.untigrito.auth.data.repository

import com.thecodefather.untigrito.domain.model.User
import com.thecodefather.untigrito.auth.domain.repository.IAuthRepository
import kotlinx.coroutines.delay
import java.util.UUID

/**
 * Implementación del repositorio de autenticación (simulado)
 */
class AuthRepositoryImpl : IAuthRepository {
    private var currentUser: User? = null
    
    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            // Simular delay de red
            delay(1000)
            
            // Validar credenciales simuladas
            if (email.isNotBlank() && password.length >= 6) {
                val user = User(
                    id = UUID.randomUUID().toString(),
                    name = email.substringBefore("@"),
                    email = email
                )
                currentUser = user
                Result.success(user)
            } else {
                Result.failure(Exception("Credenciales inválidas"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun register(name: String, email: String, password: String): Result<User> {
        return try {
            // Simular delay de red
            delay(1000)
            
            // Validar datos
            if (name.isNotBlank() && email.isNotBlank() && password.length >= 6) {
                val user = User(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    email = email
                )
                currentUser = user
                Result.success(user)
            } else {
                Result.failure(Exception("Datos de registro inválidos"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun forgotPassword(email: String): Result<Boolean> {
        return try {
            // Simular delay de red
            delay(1000)
            
            if (email.isNotBlank()) {
                Result.success(true)
            } else {
                Result.failure(Exception("Email inválido"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override fun logout() {
        currentUser = null
    }
    
    override fun getCurrentUser(): User? {
        return currentUser
    }
}
