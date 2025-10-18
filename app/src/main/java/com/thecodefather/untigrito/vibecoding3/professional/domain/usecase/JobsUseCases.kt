package com.thecodefather.untigrito.vibecoding3.professional.domain.usecase

import com.thecodefather.untigrito.vibecoding3.professional.domain.model.Job
import com.thecodefather.untigrito.vibecoding3.professional.domain.repository.JobsRepository
import kotlinx.coroutines.flow.Flow

/**
 * Caso de uso para obtener la lista de trabajos disponibles
 */
class GetJobsUseCase(private val repository: JobsRepository) {
    suspend operator fun invoke(
        page: Int = 1,
        perPage: Int = 20,
        latitude: Double? = null,
        longitude: Double? = null,
        radius: Double? = null,
        category: String? = null,
        sortBy: String? = "recent"
    ): Result<List<Job>> = repository.getJobs(
        page = page,
        perPage = perPage,
        latitude = latitude,
        longitude = longitude,
        radius = radius,
        category = category,
        sortBy = sortBy
    )
}

/**
 * Caso de uso para buscar trabajos por palabras clave
 */
class SearchJobsUseCase(private val repository: JobsRepository) {
    suspend operator fun invoke(
        query: String,
        page: Int = 1,
        perPage: Int = 20
    ): Result<List<Job>> = repository.searchJobs(
        query = query,
        page = page,
        perPage = perPage
    )
}

/**
 * Caso de uso para obtener los detalles de un trabajo
 */
class GetJobDetailsUseCase(private val repository: JobsRepository) {
    suspend operator fun invoke(jobId: String): Result<Job> = 
        repository.getJobDetails(jobId)
}

/**
 * Caso de uso para marcar/desmarcar un trabajo como favorito
 */
class ToggleFavoriteUseCase(private val repository: JobsRepository) {
    suspend operator fun invoke(jobId: String, isFavorite: Boolean): Result<Boolean> =
        repository.toggleFavorite(jobId, isFavorite)
    
    fun getFavoritesFlow(): Flow<List<String>> = repository.getFavoritesFlow()
}

/**
 * Caso de uso para obtener la lista de propuestas del profesional
 */
class GetProposalsUseCase(private val repository: com.thecodefather.untigrito.vibecoding3.professional.domain.repository.ProposalsRepository) {
    suspend operator fun invoke(
        page: Int = 1,
        perPage: Int = 20,
        status: String? = null
    ): Result<List<com.thecodefather.untigrito.vibecoding3.professional.domain.model.Proposal>> = 
        repository.getProposals(page, perPage, status)
}

/**
 * Caso de uso para obtener los detalles de una propuesta
 */
class GetProposalDetailsUseCase(private val repository: com.thecodefather.untigrito.vibecoding3.professional.domain.repository.ProposalsRepository) {
    suspend operator fun invoke(proposalId: String): Result<com.thecodefather.untigrito.vibecoding3.professional.domain.model.Proposal> =
        repository.getProposalDetails(proposalId)
}

/**
 * Caso de uso para crear una nueva propuesta
 */
class CreateProposalUseCase(private val repository: com.thecodefather.untigrito.vibecoding3.professional.domain.repository.ProposalsRepository) {
    suspend operator fun invoke(
        jobId: String,
        amount: Double,
        description: String,
        includesMaterials: Boolean,
        offerWarranty: Boolean,
        warrantyDescription: String? = null,
        terms: String? = null
    ): Result<com.thecodefather.untigrito.vibecoding3.professional.domain.model.Proposal> =
        repository.createProposal(
            jobId = jobId,
            amount = amount,
            description = description,
            includesMaterials = includesMaterials,
            offerWarranty = offerWarranty,
            warrantyDescription = warrantyDescription,
            terms = terms
        )
}

/**
 * Caso de uso para cancelar una propuesta
 */
class CancelProposalUseCase(private val repository: com.thecodefather.untigrito.vibecoding3.professional.domain.repository.ProposalsRepository) {
    suspend operator fun invoke(proposalId: String): Result<Boolean> =
        repository.cancelProposal(proposalId)
}

/**
 * Caso de uso para obtener la lista de servicios del profesional
 */
class GetServicesUseCase(private val repository: com.thecodefather.untigrito.vibecoding3.professional.domain.repository.ServicesRepository) {
    suspend operator fun invoke(
        page: Int = 1,
        perPage: Int = 20,
        status: String? = null
    ): Result<List<com.thecodefather.untigrito.vibecoding3.professional.domain.model.Service>> =
        repository.getServices(page, perPage, status)
}

/**
 * Caso de uso para obtener los detalles de un servicio
 */
class GetServiceDetailsUseCase(private val repository: com.thecodefather.untigrito.vibecoding3.professional.domain.repository.ServicesRepository) {
    suspend operator fun invoke(serviceId: String): Result<com.thecodefather.untigrito.vibecoding3.professional.domain.model.Service> =
        repository.getServiceDetails(serviceId)
}

/**
 * Caso de uso para crear un nuevo servicio
 */
class CreateServiceUseCase(private val repository: com.thecodefather.untigrito.vibecoding3.professional.domain.repository.ServicesRepository) {
    suspend operator fun invoke(
        title: String,
        description: String,
        category: String,
        priceMin: Double,
        priceMax: Double,
        location: String,
        serviceZone: String? = null,
        images: List<String> = emptyList(),
        tags: List<String> = emptyList()
    ): Result<com.thecodefather.untigrito.vibecoding3.professional.domain.model.Service> =
        repository.createService(
            title = title,
            description = description,
            category = category,
            priceMin = priceMin,
            priceMax = priceMax,
            location = location,
            serviceZone = serviceZone,
            images = images,
            tags = tags
        )
}

/**
 * Caso de uso para actualizar un servicio existente
 */
class UpdateServiceUseCase(private val repository: com.thecodefather.untigrito.vibecoding3.professional.domain.repository.ServicesRepository) {
    suspend operator fun invoke(
        serviceId: String,
        title: String,
        description: String,
        category: String,
        priceMin: Double,
        priceMax: Double,
        location: String,
        serviceZone: String? = null,
        images: List<String> = emptyList(),
        tags: List<String> = emptyList()
    ): Result<com.thecodefather.untigrito.vibecoding3.professional.domain.model.Service> =
        repository.updateService(
            serviceId = serviceId,
            title = title,
            description = description,
            category = category,
            priceMin = priceMin,
            priceMax = priceMax,
            location = location,
            serviceZone = serviceZone,
            images = images,
            tags = tags
        )
}

/**
 * Caso de uso para obtener las conversaciones
 */
class GetConversationsUseCase(private val repository: com.thecodefather.untigrito.vibecoding3.professional.domain.repository.MessagesRepository) {
    suspend operator fun invoke(
        page: Int = 1,
        perPage: Int = 20
    ): Result<List<com.thecodefather.untigrito.vibecoding3.professional.domain.model.Conversation>> =
        repository.getConversations(page, perPage)
}

/**
 * Caso de uso para obtener los mensajes de una conversación
 */
class GetMessagesUseCase(private val repository: com.thecodefather.untigrito.vibecoding3.professional.domain.repository.MessagesRepository) {
    suspend operator fun invoke(
        conversationId: String,
        page: Int = 1,
        perPage: Int = 50
    ): Result<List<com.thecodefather.untigrito.vibecoding3.professional.domain.model.Message>> =
        repository.getMessages(conversationId, page, perPage)
}

/**
 * Caso de uso para enviar un mensaje
 */
class SendMessageUseCase(private val repository: com.thecodefather.untigrito.vibecoding3.professional.domain.repository.MessagesRepository) {
    suspend operator fun invoke(
        conversationId: String,
        content: String,
        type: String = "TEXT",
        mediaUrl: String? = null
    ): Result<com.thecodefather.untigrito.vibecoding3.professional.domain.model.Message> =
        repository.sendMessage(conversationId, content, type, mediaUrl)
}

/**
 * Caso de uso para obtener las calificaciones del profesional
 */
class GetRatingsUseCase(private val repository: com.thecodefather.untigrito.vibecoding3.professional.domain.repository.RatingsRepository) {
    suspend operator fun invoke(
        professionalId: String,
        page: Int = 1,
        perPage: Int = 20
    ): Result<List<com.thecodefather.untigrito.vibecoding3.professional.domain.model.Rating>> =
        repository.getProfessionalRatings(professionalId, page, perPage)
}

/**
 * Caso de uso para obtener las notificaciones del profesional
 */
class GetNotificationsUseCase(private val repository: com.thecodefather.untigrito.vibecoding3.professional.domain.repository.NotificationsRepository) {
    suspend operator fun invoke(
        page: Int = 1,
        perPage: Int = 20,
        unreadOnly: Boolean = false
    ): Result<List<com.thecodefather.untigrito.vibecoding3.professional.domain.model.Notification>> =
        repository.getNotifications(page, perPage, unreadOnly)
}
