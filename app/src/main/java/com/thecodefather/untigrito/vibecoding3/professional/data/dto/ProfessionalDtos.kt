package com.thecodefather.untigrito.vibecoding3.professional.data.dto

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

/**
 * DTO para respuesta de Trabajo desde la API
 */
data class JobResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("client_id")
    val clientId: String,
    @SerializedName("client_name")
    val clientName: String,
    @SerializedName("client_rating")
    val clientRating: Float = 0f,
    @SerializedName("client_image")
    val clientImage: String? = null,
    @SerializedName("category")
    val category: String,
    @SerializedName("budget")
    val budget: Double,
    @SerializedName("location")
    val location: String,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("radius")
    val radius: Double? = null,
    @SerializedName("requirements")
    val requirements: List<String> = emptyList(),
    @SerializedName("image")
    val image: String? = null,
    @SerializedName("images")
    val images: List<String> = emptyList(),
    @SerializedName("created_at")
    val createdAt: LocalDateTime,
    @SerializedName("deadline")
    val deadline: LocalDateTime? = null,
    @SerializedName("status")
    val status: String = "OPEN",
    @SerializedName("proposals_count")
    val proposalsCount: Int = 0,
    @SerializedName("selected_proposal_id")
    val selectedProposalId: String? = null,
    @SerializedName("tags")
    val tags: List<String> = emptyList()
)

/**
 * DTO para respuesta de Propuesta desde la API
 */
data class ProposalResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("job_id")
    val jobId: String,
    @SerializedName("professional_id")
    val professionalId: String,
    @SerializedName("professional_name")
    val professionalName: String,
    @SerializedName("professional_rating")
    val professionalRating: Float = 0f,
    @SerializedName("professional_image")
    val professionalImage: String? = null,
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("description")
    val description: String,
    @SerializedName("includes_materials")
    val includesMaterials: Boolean,
    @SerializedName("offer_warranty")
    val offerWarranty: Boolean,
    @SerializedName("warranty_description")
    val warrantyDescription: String? = null,
    @SerializedName("terms")
    val terms: String? = null,
    @SerializedName("status")
    val status: String = "PENDING",
    @SerializedName("created_at")
    val createdAt: LocalDateTime,
    @SerializedName("updated_at")
    val updatedAt: LocalDateTime? = null,
    @SerializedName("accepted_at")
    val acceptedAt: LocalDateTime? = null,
    @SerializedName("completed_at")
    val completedAt: LocalDateTime? = null,
    @SerializedName("client_rating")
    val clientRating: Float? = null,
    @SerializedName("client_review")
    val clientReview: String? = null
)

/**
 * DTO para respuesta de Servicio desde la API
 */
data class ServiceResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("professional_id")
    val professionalId: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("category")
    val category: String,
    @SerializedName("price_min")
    val priceMin: Double,
    @SerializedName("price_max")
    val priceMax: Double,
    @SerializedName("location")
    val location: String,
    @SerializedName("service_zone")
    val serviceZone: String? = null,
    @SerializedName("image")
    val image: String? = null,
    @SerializedName("images")
    val images: List<String> = emptyList(),
    @SerializedName("rating")
    val rating: Float = 0f,
    @SerializedName("reviews_count")
    val reviewsCount: Int = 0,
    @SerializedName("status")
    val status: String = "ACTIVE",
    @SerializedName("created_at")
    val createdAt: LocalDateTime,
    @SerializedName("updated_at")
    val updatedAt: LocalDateTime? = null,
    @SerializedName("tags")
    val tags: List<String> = emptyList()
)

/**
 * DTO para respuesta de Mensaje desde la API
 */
data class MessageResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("conversation_id")
    val conversationId: String,
    @SerializedName("sender_id")
    val senderId: String,
    @SerializedName("sender_name")
    val senderName: String,
    @SerializedName("sender_image")
    val senderImage: String? = null,
    @SerializedName("content")
    val content: String,
    @SerializedName("type")
    val type: String = "TEXT",
    @SerializedName("media_url")
    val mediaUrl: String? = null,
    @SerializedName("created_at")
    val createdAt: LocalDateTime,
    @SerializedName("is_read")
    val isRead: Boolean = false,
    @SerializedName("reactions")
    val reactions: List<String> = emptyList()
)

/**
 * DTO para respuesta de Conversación desde la API
 */
data class ConversationResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("participant_ids")
    val participantIds: List<String>,
    @SerializedName("participant_names")
    val participantNames: List<String>,
    @SerializedName("last_message")
    val lastMessage: String? = null,
    @SerializedName("last_message_time")
    val lastMessageTime: LocalDateTime? = null,
    @SerializedName("unread_count")
    val unreadCount: Int = 0,
    @SerializedName("type")
    val type: String = "DIRECT",
    @SerializedName("image")
    val image: String? = null
)

/**
 * DTO para respuesta de Calificación desde la API
 */
data class RatingResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("professional_id")
    val professionalId: String,
    @SerializedName("client_id")
    val clientId: String,
    @SerializedName("proposal_id")
    val proposalId: String,
    @SerializedName("score")
    val score: Int,
    @SerializedName("review")
    val review: String? = null,
    @SerializedName("created_at")
    val createdAt: LocalDateTime,
    @SerializedName("photos")
    val photos: List<String> = emptyList()
)

/**
 * DTO para respuesta de Notificación desde la API
 */
data class NotificationResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("related_id")
    val relatedId: String? = null,
    @SerializedName("created_at")
    val createdAt: LocalDateTime,
    @SerializedName("is_read")
    val isRead: Boolean = false,
    @SerializedName("data")
    val data: Map<String, String> = emptyMap()
)

/**
 * DTO genérico para respuestas paginadas
 */
data class PaginatedResponse<T>(
    @SerializedName("data")
    val data: List<T>,
    @SerializedName("page")
    val page: Int,
    @SerializedName("per_page")
    val perPage: Int,
    @SerializedName("total")
    val total: Int,
    @SerializedName("total_pages")
    val totalPages: Int
)

/**
 * DTO genérico para respuestas de éxito
 */
data class ApiResponse<T>(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("data")
    val data: T? = null,
    @SerializedName("error")
    val error: String? = null
)

/**
 * DTO para crear/actualizar una Propuesta
 */
data class CreateProposalRequest(
    @SerializedName("job_id")
    val jobId: String,
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("description")
    val description: String,
    @SerializedName("includes_materials")
    val includesMaterials: Boolean = false,
    @SerializedName("offer_warranty")
    val offerWarranty: Boolean = false,
    @SerializedName("warranty_description")
    val warrantyDescription: String? = null,
    @SerializedName("terms")
    val terms: String? = null
)

/**
 * DTO para crear/actualizar un Servicio
 */
data class CreateServiceRequest(
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("category")
    val category: String,
    @SerializedName("price_min")
    val priceMin: Double,
    @SerializedName("price_max")
    val priceMax: Double,
    @SerializedName("location")
    val location: String,
    @SerializedName("service_zone")
    val serviceZone: String? = null,
    @SerializedName("images")
    val images: List<String> = emptyList(),
    @SerializedName("tags")
    val tags: List<String> = emptyList()
)

/**
 * DTO para enviar un Mensaje
 */
data class SendMessageRequest(
    @SerializedName("conversation_id")
    val conversationId: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("type")
    val type: String = "TEXT",
    @SerializedName("media_url")
    val mediaUrl: String? = null
)
