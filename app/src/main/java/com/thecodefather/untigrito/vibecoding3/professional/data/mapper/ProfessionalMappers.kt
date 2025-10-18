package com.thecodefather.untigrito.vibecoding3.professional.data.mapper

import com.thecodefather.untigrito.vibecoding3.professional.data.dto.*
import com.thecodefather.untigrito.vibecoding3.professional.domain.model.*

/**
 * Mapper para convertir JobResponse a Job (entidad de dominio)
 */
object JobMapper {
    fun toDomain(response: JobResponse): Job = Job(
        id = response.id,
        title = response.title,
        description = response.description,
        clientId = response.clientId,
        clientName = response.clientName,
        clientRating = response.clientRating,
        clientImage = response.clientImage,
        category = response.category,
        budget = response.budget,
        location = response.location,
        latitude = response.latitude,
        longitude = response.longitude,
        radius = response.radius,
        requirements = response.requirements,
        image = response.image,
        images = response.images,
        createdAt = response.createdAt,
        deadline = response.deadline,
        status = JobStatus.valueOf(response.status),
        proposalsCount = response.proposalsCount,
        selectedProposalId = response.selectedProposalId,
        tags = response.tags
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
        professionalId = response.professionalId,
        professionalName = response.professionalName,
        professionalRating = response.professionalRating,
        professionalImage = response.professionalImage,
        amount = response.amount,
        description = response.description,
        includesMaterials = response.includesMaterials,
        offerWarranty = response.offerWarranty,
        warrantyDescription = response.warrantyDescription,
        terms = response.terms,
        status = ProposalStatus.valueOf(response.status),
        createdAt = response.createdAt,
        updatedAt = response.updatedAt,
        acceptedAt = response.acceptedAt,
        completedAt = response.completedAt,
        clientRating = response.clientRating,
        clientReview = response.clientReview
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
        professionalId = response.professionalId,
        title = response.title,
        description = response.description,
        category = response.category,
        priceMin = response.priceMin,
        priceMax = response.priceMax,
        location = response.location,
        serviceZone = response.serviceZone,
        image = response.image,
        images = response.images,
        rating = response.rating,
        reviewsCount = response.reviewsCount,
        status = ServiceStatus.valueOf(response.status),
        createdAt = response.createdAt,
        updatedAt = response.updatedAt,
        tags = response.tags
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
        senderImage = response.senderImage,
        content = response.content,
        type = MessageType.valueOf(response.type),
        mediaUrl = response.mediaUrl,
        createdAt = response.createdAt,
        isRead = response.isRead,
        reactions = response.reactions
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
        participantIds = response.participantIds,
        participantNames = response.participantNames,
        lastMessage = response.lastMessage,
        lastMessageTime = response.lastMessageTime,
        unreadCount = response.unreadCount,
        type = ConversationType.valueOf(response.type),
        image = response.image
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
        createdAt = response.createdAt,
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
        createdAt = response.createdAt,
        isRead = response.isRead,
        data = response.data
    )
    
    fun toDomainList(responses: List<NotificationResponse>): List<Notification> =
        responses.map { toDomain(it) }
}

/**
 * Mapper para convertir entidades de dominio a DTOs (para crear/actualizar)
 */
object ProposalRequestMapper {
    fun toRequest(proposal: Proposal): CreateProposalRequest = CreateProposalRequest(
        jobId = proposal.jobId,
        amount = proposal.amount,
        description = proposal.description,
        includesMaterials = proposal.includesMaterials,
        offerWarranty = proposal.offerWarranty,
        warrantyDescription = proposal.warrantyDescription,
        terms = proposal.terms
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
        priceMin = service.priceMin,
        priceMax = service.priceMax,
        location = service.location,
        serviceZone = service.serviceZone,
        images = service.images,
        tags = service.tags
    )
}
