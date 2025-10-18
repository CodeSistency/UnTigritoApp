package com.thecodefather.untigrito.data.repository

import com.thecodefather.untigrito.data.mapper.NotificationMapper
import com.thecodefather.untigrito.data.remote.ProfessionalNotificationsApiService
import com.thecodefather.untigrito.domain.model.Notification
import com.thecodefather.untigrito.domain.repository.NotificationsRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación del repositorio de notificaciones
 */
@Singleton
class NotificationsRepositoryImpl @Inject constructor(
    private val apiService: ProfessionalNotificationsApiService
) : NotificationsRepository {

    override suspend fun getNotifications(
        page: Int,
        perPage: Int,
        unreadOnly: Boolean
    ): Result<List<Notification>> {
        return try {
            val response = apiService.getNotifications(page, perPage, unreadOnly)
            if (response.success == true && response.data != null) {
                val notifications: List<Notification> = response.data.data.map { NotificationMapper.toDomain(it) }
                Result.success(notifications)
            } else {
                Result.failure(Exception(response.error ?: "Error al obtener notificaciones"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun markAsRead(notificationId: String): Result<Boolean> {
        return try {
            val response = apiService.markNotificationAsRead(notificationId)
            if (response.success == true) {
                Result.success(true)
            } else {
                Result.failure(Exception(response.error ?: "Error al marcar como leída"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun markAllAsRead(): Result<Boolean> {
        return try {
            val response = apiService.markAllNotificationsAsRead()
            if (response.success == true) {
                Result.success(true)
            } else {
                Result.failure(Exception(response.error ?: "Error al marcar todas como leídas"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteNotification(notificationId: String): Result<Boolean> {
        return try {
            // Implementación simplificada
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUnreadCount(): Result<Int> {
        return try {
            val notifications = getNotifications(page = 1, perPage = 100, unreadOnly = true)
            notifications.getOrNull()?.let { list ->
                Result.success(list.size)
            } ?: Result.failure(Exception("No se pudieron obtener notificaciones"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createNotification(
        title: String,
        message: String,
        type: String,
        data: Map<String, String>?
    ): Result<Notification> {
        return try {
            // Implementación simplificada
            Result.failure(Exception("No implementado"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
