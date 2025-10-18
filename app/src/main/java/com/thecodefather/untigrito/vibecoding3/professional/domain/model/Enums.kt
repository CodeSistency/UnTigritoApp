package com.thecodefather.untigrito.vibecoding3.professional.domain.model

/**
 * Estados posibles de un trabajo
 */
enum class JobStatus {
    OPEN,           // Disponible para ofertas
    IN_PROGRESS,    // En proceso (profesional seleccionado)
    COMPLETED,      // Completado
    CANCELLED,      // Cancelado
    ARCHIVED        // Archivado
}

/**
 * Estados posibles de una propuesta
 */
enum class ProposalStatus {
    PENDING,        // Pendiente de respuesta del cliente
    ACCEPTED,       // Aceptada por el cliente
    REJECTED,       // Rechazada por el cliente
    CANCELLED,      // Cancelada por el profesional
    IN_PROGRESS,    // En proceso
    COMPLETED,      // Completada
    DISPUTED        // En disputa
}

/**
 * Estados posibles de un servicio
 */
enum class ServiceStatus {
    ACTIVE,         // Activo y disponible
    INACTIVE,       // Inactivo (oculto)
    ARCHIVED,       // Archivado
    DELETED         // Eliminado
}

/**
 * Tipos de mensajes
 */
enum class MessageType {
    TEXT,           // Mensaje de texto
    IMAGE,          // Imagen
    VIDEO,          // Video
    FILE,           // Archivo
    LOCATION,       // Ubicación
    CALL            // Llamada
}

/**
 * Tipos de conversación
 */
enum class ConversationType {
    DIRECT,         // Chat directo 1 a 1
    GROUP,          // Chat de grupo
    SUPPORT         // Chat con soporte
}

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

/**
 * Tipos de filtros para trabajos
 */
enum class JobFilter {
    RECENT,                 // Más recientes
    RECOMMENDED,            // Recomendados (basado en perfil)
    FAVORITES,              // Favoritos marcados
    BY_CATEGORY,            // Por categoría
    BY_LOCATION,            // Por ubicación
    BY_BUDGET_LOW,          // Presupuesto bajo
    BY_BUDGET_HIGH          // Presupuesto alto
}

/**
 * Tipos de filtros para propuestas
 */
enum class ProposalFilter {
    OPEN,                   // Propuestas abiertas
    IN_PROGRESS,            // En proceso
    COMPLETED,              // Completadas
    REJECTED,               // Rechazadas
    ALL                     // Todas
}

/**
 * Estados de disponibilidad del profesional
 */
enum class ProfessionalAvailability {
    AVAILABLE,              // Disponible
    BUSY,                   // Ocupado
    DO_NOT_DISTURB,         // No molestar
    OFFLINE                 // Desconectado
}

/**
 * Tipos de categorías de servicios
 */
enum class ServiceCategory {
    PLUMBING,               // Plomería
    ELECTRICITY,            // Electricidad
    CARPENTRY,              // Carpintería
    PAINTING,               // Pintura
    CLEANING,               // Limpieza
    REPAIR,                 // Reparación
    CONSTRUCTION,           // Construcción
    HVAC,                   // Calefacción/Aire
    LANDSCAPING,            // Jardinería
    OTHER                   // Otro
}

/**
 * Tipos de transacciones
 */
enum class TransactionType {
    PAYMENT_RECEIVED,       // Pago recibido
    PAYMENT_SENT,           // Pago enviado
    COMMISSION,             // Comisión
    REFUND,                 // Reembolso
    WITHDRAWAL              // Retiro
}

/**
 * Estados de transacciones
 */
enum class TransactionStatus {
    PENDING,                // Pendiente
    COMPLETED,              // Completada
    FAILED,                 // Fallida
    CANCELLED               // Cancelada
}
