package com.thecodefather.untigrito.vibecoding3.professional.domain.model

import java.time.LocalDateTime

/**
 * Entidad de dominio para representar un Trabajo
 * Contiene la información completa de una oportunidad de trabajo disponible
 */
data class Job(
    val id: String,
    val title: String,
    val description: String,
    val clientId: String,
    val clientName: String,
    val clientRating: Float = 0f,
    val clientImage: String? = null,
    val category: String,
    val budget: Double,
    val location: String,
    val latitude: Double,
    val longitude: Double,
    val radius: Double? = null, // Radio de búsqueda en km
    val requirements: List<String>,
    val image: String? = null,
    val images: List<String> = emptyList(),
    val createdAt: LocalDateTime,
    val deadline: LocalDateTime? = null,
    val status: JobStatus = JobStatus.OPEN,
    val isFavorite: Boolean = false,
    val proposalsCount: Int = 0,
    val selectedProposalId: String? = null,
    val tags: List<String> = emptyList()
)

/**
 * Entidad de dominio para representar una Propuesta
 * Contiene la información de una oferta enviada por un profesional
 */
data class Proposal(
    val id: String,
    val jobId: String,
    val professionalId: String,
    val professionalName: String,
    val professionalRating: Float = 0f,
    val professionalImage: String? = null,
    val amount: Double,
    val description: String,
    val includesMaterials: Boolean,
    val offerWarranty: Boolean,
    val warrantyDescription: String? = null,
    val terms: String? = null,
    val status: ProposalStatus = ProposalStatus.PENDING,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime? = null,
    val acceptedAt: LocalDateTime? = null,
    val completedAt: LocalDateTime? = null,
    val clientRating: Float? = null,
    val clientReview: String? = null
)

/**
 * Entidad de dominio para representar un Servicio
 * Contiene los servicios publicados por un profesional
 */
data class Service(
    val id: String,
    val professionalId: String,
    val title: String,
    val description: String,
    val category: String,
    val priceMin: Double,
    val priceMax: Double,
    val location: String,
    val serviceZone: String? = null,
    val image: String? = null,
    val images: List<String> = emptyList(),
    val rating: Float = 0f,
    val reviewsCount: Int = 0,
    val status: ServiceStatus = ServiceStatus.ACTIVE,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime? = null,
    val tags: List<String> = emptyList()
)

/**
 * Entidad de dominio para representar un Mensaje
 */
data class Message(
    val id: String,
    val conversationId: String,
    val senderId: String,
    val senderName: String,
    val senderImage: String? = null,
    val content: String,
    val type: MessageType = MessageType.TEXT,
    val mediaUrl: String? = null,
    val createdAt: LocalDateTime,
    val isRead: Boolean = false,
    val reactions: List<String> = emptyList()
)

/**
 * Entidad de dominio para representar una Conversación
 */
data class Conversation(
    val id: String,
    val participantIds: List<String>,
    val participantNames: List<String>,
    val lastMessage: String? = null,
    val lastMessageTime: LocalDateTime? = null,
    val unreadCount: Int = 0,
    val type: ConversationType = ConversationType.DIRECT,
    val image: String? = null
)

/**
 * Entidad de dominio para Calificación
 */
data class Rating(
    val id: String,
    val professionalId: String,
    val clientId: String,
    val proposalId: String,
    val score: Int, // 1-5
    val review: String? = null,
    val createdAt: LocalDateTime,
    val photos: List<String> = emptyList()
)

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
