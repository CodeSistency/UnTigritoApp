package com.thecodefather.untigrito.domain.model

import java.time.LocalDateTime

/**
 * Entidad de dominio para Notificación
 */
data class Notification(
    val id: String,
    val userId: String,
    val type: NotificationType,
    val title: String,
    val message: String,
    val relatedId: String? = null,
    val createdAt: LocalDateTime,
    val isRead: Boolean = false,
    val data: Map<String, String> = emptyMap()
)

/**
 * Tipos de notificaciones
 */
enum class NotificationType {
    PROPOSAL_ACCEPTED,      // Propuesta aceptada
    PROPOSAL_REJECTED,      // Propuesta rechazada
    NEW_JOB,                // Nuevo trabajo disponible
    NEW_MESSAGE,            // Nuevo mensaje
    SERVICE_CREATED,        // Servicio creado
    SERVICE_UPDATED,        // Servicio actualizado
    RATING_RECEIVED,        // Calificación recibida
    PAYMENT_RECEIVED,       // Pago recibido
    PAYMENT_FAILED,         // Pago fallido
    JOB_EXPIRED,            // Trabajo expirado
    REMINDER                // Recordatorio general
}
