package com.thecodefather.untigrito.domain.repository

import com.thecodefather.untigrito.domain.model.Notification
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz del repositorio para operaciones de Notificaciones
 */
interface NotificationsRepository {
    
    /**
     * Obtiene la lista de notificaciones
     */
    suspend fun getNotifications(
        page: Int = 1,
        perPage: Int = 20,
        unreadOnly: Boolean = false
    ): Result<List<Notification>>
    
    /**
     * Marca una notificación como leída
     */
    suspend fun markAsRead(notificationId: String): Result<Boolean>
    
    /**
     * Marca todas las notificaciones como leídas
     */
    suspend fun markAllAsRead(): Result<Boolean>
    
    /**
     * Elimina una notificación
     */
    suspend fun deleteNotification(notificationId: String): Result<Boolean>
    
    /**
     * Obtiene el conteo de notificaciones no leídas
     */
    suspend fun getUnreadCount(): Result<Int>
    
    /**
     * Crea una nueva notificación
     */
    suspend fun createNotification(
        title: String,
        message: String,
        type: String,
        data: Map<String, String>? = null
    ): Result<Notification>
}
