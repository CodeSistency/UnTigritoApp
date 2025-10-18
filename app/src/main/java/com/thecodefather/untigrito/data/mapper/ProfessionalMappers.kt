package com.thecodefather.untigrito.data.mapper

import android.annotation.SuppressLint
import com.thecodefather.untigrito.data.dto.*
import com.thecodefather.untigrito.domain.model.*
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

/**
 * Mapper para convertir JobResponse a Job (entidad de dominio)
 */
object JobMapper {
    fun toDomain(response: JobResponse): Job = Job(
        id = response.id,
        title = response.title,
        description = response.description,
        category = response.category,
        budget = response.budget,
        clientId = response.clientId,
        clientName = response.clientName,
        clientAvatar = response.clientImage,
        status = JobStatus.valueOf(response.status),
        location = if (response.location.isNotEmpty()) {
            Location(
                address = response.location,
                city = "", // Se puede extraer de la ubicación si es necesario
                state = "",
                coordinates = if (response.latitude != 0.0 && response.longitude != 0.0) {
                    Coordinates(response.latitude, response.longitude)
                } else null
            )
        } else null,
        deadline = response.deadline?.let { convertToDate(it) },
        createdAt = convertToDate(response.createdAt),
        isRecommended = false,
        isFavorite = false,
        urgency = UrgencyLevel.NORMAL,
        estimatedDuration = null,
        requiredSkills = response.requirements,
        attachments = response.images
    )
    
    fun toDomainList(responses: List<JobResponse>): List<Job> =
        responses.map { toDomain(it) }
}

/**
 * Mapper para convertir ProposalResponse a Proposal (entidad de dominio)
 */
object ProposalMapper {
    fun toDomain(response: ProposalResponse): Proposal = Proposal(
        id = response.id,
        jobId = response.jobId,
        jobTitle = "", // No disponible en el DTO
        clientId = "", // No disponible en el DTO
        clientName = "", // No disponible en el DTO
        clientAvatar = null, // No disponible en el DTO
        proposedPrice = response.amount,
        description = response.description,
        estimatedDuration = 0, // No disponible en el DTO
        includesMaterials = response.includesMaterials,
        offersWarranty = response.offerWarranty,
        termsAndConditions = response.terms,
        status = ProposalStatus.valueOf(response.status),
        createdAt = convertToDate(response.createdAt),
        updatedAt = convertToDate(response.updatedAt ?: response.createdAt),
        responseMessage = response.clientReview,
        responseDate = response.acceptedAt?.let { convertToDate(it) }
    )
    
    fun toDomainList(responses: List<ProposalResponse>): List<Proposal> =
        responses.map { toDomain(it) }
}

/**
 * Mapper para convertir ServiceResponse a Service (entidad de dominio)
 */
object ServiceMapper {
    fun toDomain(response: ServiceResponse): Service = Service(
        id = response.id,
        title = response.title,
        description = response.description,
        category = response.category,
        minPrice = response.priceMin,
        maxPrice = response.priceMax,
        serviceArea = response.serviceZone ?: response.location,
        status = ServiceStatus.valueOf(response.status),
        images = response.images,
        createdAt = convertToDate(response.createdAt),
        updatedAt = convertToDate(response.updatedAt ?: response.createdAt),
        isActive = response.status == "ACTIVE",
        rating = response.rating.toDouble(),
        reviewCount = response.reviewsCount,
        completedJobs = 0 // No disponible en el DTO
    )
    
    fun toDomainList(responses: List<ServiceResponse>): List<Service> =
        responses.map { toDomain(it) }
}

/**
 * Mapper para convertir MessageResponse a Message (entidad de dominio)
 */
object MessageMapper {
    fun toDomain(response: MessageResponse): Message = Message(
        id = response.id,
        conversationId = response.conversationId,
        senderId = response.senderId,
        senderName = response.senderName,
        senderAvatar = response.senderImage,
        content = response.content,
        timestamp = convertToDate(response.createdAt),
        isRead = response.isRead,
        messageType = MessageType.valueOf(response.type),
        attachments = if (response.mediaUrl != null) listOf(response.mediaUrl) else emptyList()
    )
    
    fun toDomainList(responses: List<MessageResponse>): List<Message> =
        responses.map { toDomain(it) }
}

/**
 * Mapper para convertir ConversationResponse a Conversation (entidad de dominio)
 */
object ConversationMapper {
    fun toDomain(response: ConversationResponse): Conversation = Conversation(
        id = response.id,
        participantId = response.participantIds.firstOrNull() ?: "",
        participantName = response.participantNames.firstOrNull() ?: "",
        participantAvatar = response.image,
        lastMessage = response.lastMessage,
        lastMessageTime = response.lastMessageTime?.let { convertToDate(it) },
        unreadCount = response.unreadCount,
        conversationType = ConversationType.valueOf(response.type),
        isActive = true
    )
    
    fun toDomainList(responses: List<ConversationResponse>): List<Conversation> =
        responses.map { toDomain(it) }
}

/**
 * Mapper para convertir RatingResponse a Rating (entidad de dominio)
 */
object RatingMapper {
    fun toDomain(response: RatingResponse): Rating = Rating(
        id = response.id,
        professionalId = response.professionalId,
        clientId = response.clientId,
        proposalId = response.proposalId,
        score = response.score,
        review = response.review,
        createdAt = convertToDate(response.createdAt),
        photos = response.photos
    )

    fun toDomainList(responses: List<RatingResponse>): List<Rating> =
        responses.map { toDomain(it) }
}

/**
 * Mapper para convertir NotificationResponse a Notification (entidad de dominio)
 */
object NotificationMapper {
    fun toDomain(response: NotificationResponse): Notification = Notification(
        id = response.id,
        userId = response.userId,
        type = NotificationType.valueOf(response.type),
        title = response.title,
        message = response.message,
        relatedId = response.relatedId,
        createdAt = response.createdAt, // Ya es LocalDateTime
        isRead = response.isRead,
        data = response.data
    )
    
    fun toDomainList(responses: PaginatedResponse<NotificationResponse>): List<Notification> =
        responses.data.map { toDomain(it) }
}

/**
 * Mapper para convertir entidades de dominio a DTOs (para crear/actualizar)
 */
object ProposalRequestMapper {
    fun toRequest(proposal: Proposal): CreateProposalRequest = CreateProposalRequest(
        jobId = proposal.jobId,
        amount = proposal.proposedPrice,
        description = proposal.description,
        includesMaterials = proposal.includesMaterials,
        offerWarranty = proposal.offersWarranty,
        warrantyDescription = null, // No disponible en el modelo de dominio
        terms = proposal.termsAndConditions
    )
}

/**
 * Mapper para convertir entidades de dominio a DTOs de servicios (para crear/actualizar)
 */
object ServiceRequestMapper {
    fun toRequest(service: Service): CreateServiceRequest = CreateServiceRequest(
        title = service.title,
        description = service.description,
        category = service.category,
        priceMin = service.minPrice,
        priceMax = service.maxPrice,
        location = service.serviceArea,
        serviceZone = service.serviceArea,
        images = service.images,
        tags = emptyList() // No disponible en el modelo de dominio
    )
}

/**
 * Función auxiliar para convertir LocalDateTime a Date
 * Nota: Requiere core library desugaring para soportar Java 8+ APIs en API < 26
 */
@SuppressLint("NewApi")
private fun convertToDate(localDateTime: LocalDateTime): Date {
    return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant())
}