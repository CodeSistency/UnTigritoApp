package com.thecodefather.untigrito.data.datasource.remote

import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.gotrue.user.UserInfo
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import timber.log.Timber

/**
 * Servicio de autenticación usando Supabase GoTrue
 * 
 * Este servicio proporciona métodos para:
 * - Registro de usuarios con email/password
 * - Login con email/password
 * - Autenticación con Google OAuth
 * - Logout
 * - Recuperación de contraseña
 * - Verificación de email
 * - Gestión de sesiones
 */
@Singleton
class SupabaseAuthService @Inject constructor(
    private val auth: Auth
) {

    /**
     * Registra un nuevo usuario con email y contraseña
     * 
     * @param email Email del usuario
     * @param password Contraseña del usuario
     * @return UserInfo si el registro fue exitoso
     * @throws Exception si hubo un error en el registro
     */
    suspend fun signUpWithEmail(email: String, password: String): Result<UserInfo?> =
        withContext(Dispatchers.IO) {
            try {
                auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                }
                
                val user = auth.currentUserOrNull()
                Timber.d("Usuario registrado: ${user?.email}")
                Result.success(user)
            } catch (e: Exception) {
                Timber.e(e, "Error al registrar usuario")
                Result.failure(e)
            }
        }

    /**
     * Inicia sesión con email y contraseña
     * 
     * @param email Email del usuario
     * @param password Contraseña del usuario
     * @return UserInfo si el login fue exitoso
     * @throws Exception si hubo un error en el login
     */
    suspend fun signInWithEmail(email: String, password: String): Result<UserInfo?> =
        withContext(Dispatchers.IO) {
            try {
                auth.signInWith(Email) {
                    this.email = email
                    this.password = password
                }
                
                val user = auth.currentUserOrNull()
                Timber.d("Usuario autenticado: ${user?.email}")
                Result.success(user)
            } catch (e: Exception) {
                Timber.e(e, "Error al iniciar sesión")
                Result.failure(e)
            }
        }

    /**
     * Cierra la sesión del usuario actual
     */
    suspend fun signOut(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            auth.signOut()
            Timber.d("Sesión cerrada")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error al cerrar sesión")
            Result.failure(e)
        }
    }

    /**
     * Envía un email de recuperación de contraseña
     * 
     * @param email Email del usuario
     */
    suspend fun resetPasswordForEmail(email: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                auth.resetPasswordForEmail(email)
                Timber.d("Email de recuperación enviado a: $email")
                Result.success(Unit)
            } catch (e: Exception) {
                Timber.e(e, "Error al enviar email de recuperación")
                Result.failure(e)
            }
        }

    /**
     * Obtiene el usuario actual
     * 
     * @return UserInfo si hay un usuario autenticado, null en caso contrario
     */
    suspend fun getCurrentUser(): UserInfo? = withContext(Dispatchers.IO) {
        try {
            auth.currentUserOrNull()
        } catch (e: Exception) {
            Timber.e(e, "Error al obtener usuario actual")
            null
        }
    }

    /**
     * Verifica si hay un usuario autenticado
     * 
     * @return true si hay un usuario autenticado, false en caso contrario
     */
    suspend fun isUserAuthenticated(): Boolean = withContext(Dispatchers.IO) {
        try {
            auth.currentSessionOrNull() != null
        } catch (e: Exception) {
            Timber.e(e, "Error al verificar autenticación")
            false
        }
    }

    /**
     * Actualiza el perfil del usuario actual
     * 
     * @param data Mapa con los datos a actualizar
     */
    suspend fun updateUser(data: Map<String, Any>): Result<UserInfo?> =
        withContext(Dispatchers.IO) {
            try {
                // Convertir Map a JsonObject
                val jsonData = buildJsonObject {
                    data.forEach { (key, value) ->
                        when (value) {
                            is String -> put(key, value)
                            is Number -> put(key, value)
                            is Boolean -> put(key, value)
                            else -> put(key, value.toString())
                        }
                    }
                }
                
                auth.modifyUser {
                    this.data = jsonData
                }
                val user = auth.currentUserOrNull()
                Timber.d("Usuario actualizado: ${user?.email}")
                Result.success(user)
            } catch (e: Exception) {
                Timber.e(e, "Error al actualizar usuario")
                Result.failure(e)
            }
        }

    /**
     * Refresca la sesión del usuario actual
     */
    suspend fun refreshSession(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            auth.refreshCurrentSession()
            Timber.d("Sesión refrescada")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error al refrescar sesión")
            Result.failure(e)
        }
    }
}

