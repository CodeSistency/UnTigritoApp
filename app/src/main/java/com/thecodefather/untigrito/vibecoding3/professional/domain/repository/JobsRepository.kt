package com.thecodefather.untigrito.vibecoding3.professional.domain.repository

import com.thecodefather.untigrito.vibecoding3.professional.domain.model.Job
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz del repositorio para operaciones de Trabajos
 */
interface JobsRepository {
    
    /**
     * Obtiene la lista de trabajos disponibles
     */
    suspend fun getJobs(
        page: Int = 1,
        perPage: Int = 20,
        latitude: Double? = null,
        longitude: Double? = null,
        radius: Double? = null,
        category: String? = null,
        sortBy: String? = "recent"
    ): Result<List<Job>>
    
    /**
     * Busca trabajos por palabras clave
     */
    suspend fun searchJobs(
        query: String,
        page: Int = 1,
        perPage: Int = 20
    ): Result<List<Job>>
    
    /**
     * Obtiene los detalles de un trabajo específico
     */
    suspend fun getJobDetails(jobId: String): Result<Job>
    
    /**
     * Marca/desmarca un trabajo como favorito
     */
    suspend fun toggleFavorite(jobId: String, isFavorite: Boolean): Result<Boolean>
    
    /**
     * Obtiene los trabajos favoritos localmente
     */
    fun getFavoritesFlow(): Flow<List<String>>
}

/**
 * Interfaz del repositorio para operaciones de Propuestas
 */
interface ProposalsRepository {
    
    /**
     * Obtiene la lista de propuestas del profesional
     */
    suspend fun getProposals(
        page: Int = 1,
        perPage: Int = 20,
        status: String? = null
    ): Result<List<com.thecodefather.untigrito.vibecoding3.professional.domain.model.Proposal>>
    
    /**
     * Obtiene los detalles de una propuesta específica
     */
    suspend fun getProposalDetails(proposalId: String): Result<com.thecodefather.untigrito.vibecoding3.professional.domain.model.Proposal>
    
    /**
     * Crea una nueva propuesta
     */
    suspend fun createProposal(
        jobId: String,
        amount: Double,
        description: String,
        includesMaterials: Boolean,
        offerWarranty: Boolean,
        warrantyDescription: String? = null,
        terms: String? = null
    ): Result<com.thecodefather.untigrito.vibecoding3.professional.domain.model.Proposal>
    
    /**
     * Actualiza una propuesta existente
     */
    suspend fun updateProposal(
        proposalId: String,
        amount: Double,
        description: String
    ): Result<com.thecodefather.untigrito.vibecoding3.professional.domain.model.Proposal>
    
    /**
     * Cancela una propuesta
     */
    suspend fun cancelProposal(proposalId: String): Result<Boolean>
}

/**
 * Interfaz del repositorio para operaciones de Servicios
 */
interface ServicesRepository {
    
    /**
     * Obtiene la lista de servicios del profesional
     */
    suspend fun getServices(
        page: Int = 1,
        perPage: Int = 20,
        status: String? = null
    ): Result<List<com.thecodefather.untigrito.vibecoding3.professional.domain.model.Service>>
    
    /**
     * Obtiene los detalles de un servicio específico
     */
    suspend fun getServiceDetails(serviceId: String): Result<com.thecodefather.untigrito.vibecoding3.professional.domain.model.Service>
    
    /**
     * Crea un nuevo servicio
     */
    suspend fun createService(
        title: String,
        description: String,
        category: String,
        priceMin: Double,
        priceMax: Double,
        location: String,
        serviceZone: String? = null,
        images: List<String> = emptyList(),
        tags: List<String> = emptyList()
    ): Result<com.thecodefather.untigrito.vibecoding3.professional.domain.model.Service>
    
    /**
     * Actualiza un servicio existente
     */
    suspend fun updateService(
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
    ): Result<com.thecodefather.untigrito.vibecoding3.professional.domain.model.Service>
    
    /**
     * Elimina un servicio
     */
    suspend fun deleteService(serviceId: String): Result<Boolean>
}

/**
 * Interfaz del repositorio para operaciones de Mensajes
 */
interface MessagesRepository {
    
    /**
     * Obtiene la lista de conversaciones
     */
    suspend fun getConversations(
        page: Int = 1,
        perPage: Int = 20
    ): Result<List<com.thecodefather.untigrito.vibecoding3.professional.domain.model.Conversation>>
    
    /**
     * Obtiene los mensajes de una conversación
     */
    suspend fun getMessages(
        conversationId: String,
        page: Int = 1,
        perPage: Int = 50
    ): Result<List<com.thecodefather.untigrito.vibecoding3.professional.domain.model.Message>>
    
    /**
     * Envía un nuevo mensaje
     */
    suspend fun sendMessage(
        conversationId: String,
        content: String,
        type: String = "TEXT",
        mediaUrl: String? = null
    ): Result<com.thecodefather.untigrito.vibecoding3.professional.domain.model.Message>
    
    /**
     * Marca una conversación como leída
     */
    suspend fun markConversationAsRead(conversationId: String): Result<Boolean>
    
    /**
     * Obtiene la conversación de soporte
     */
    suspend fun getSupportConversation(): Result<com.thecodefather.untigrito.vibecoding3.professional.domain.model.Conversation>
}

/**
 * Interfaz del repositorio para operaciones de Calificaciones
 */
interface RatingsRepository {
    
    /**
     * Obtiene las calificaciones del profesional
     */
    suspend fun getProfessionalRatings(
        professionalId: String,
        page: Int = 1,
        perPage: Int = 20
    ): Result<List<com.thecodefather.untigrito.vibecoding3.professional.domain.model.Rating>>
    
    /**
     * Obtiene las estadísticas del profesional
     */
    suspend fun getProfessionalStats(professionalId: String): Result<Map<String, Any>>
}

/**
 * Interfaz del repositorio para operaciones de Notificaciones
 */
interface NotificationsRepository {
    
    /**
     * Obtiene las notificaciones del profesional
     */
    suspend fun getNotifications(
        page: Int = 1,
        perPage: Int = 20,
        unreadOnly: Boolean = false
    ): Result<List<com.thecodefather.untigrito.vibecoding3.professional.domain.model.Notification>>
    
    /**
     * Marca una notificación como leída
     */
    suspend fun markNotificationAsRead(notificationId: String): Result<Boolean>
    
    /**
     * Marca todas las notificaciones como leídas
     */
    suspend fun markAllNotificationsAsRead(): Result<Boolean>
}
