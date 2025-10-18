package com.example.vibecoding3.auth.domain.repository

import com.example.vibecoding3.auth.domain.model.User

/**
 * Interfaz del repositorio de autenticación
 */
interface IAuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(name: String, email: String, password: String): Result<User>
    suspend fun forgotPassword(email: String): Result<Boolean>
    fun logout()
    fun getCurrentUser(): User?
}
