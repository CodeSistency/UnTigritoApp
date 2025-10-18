package com.thecodefather.untigrito.domain.usecase

import com.thecodefather.untigrito.domain.model.Notification
import com.thecodefather.untigrito.domain.repository.NotificationsRepository
import javax.inject.Inject

/**
 * Caso de uso para obtener las notificaciones
 */
class GetNotificationsUseCase @Inject constructor(
    private val repository: NotificationsRepository
) {
    suspend operator fun invoke(
        page: Int = 1,
        perPage: Int = 20,
        unreadOnly: Boolean = false
    ): Result<List<Notification>> {
        return repository.getNotifications(page, perPage, unreadOnly)
    }
}
