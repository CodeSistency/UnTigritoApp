package com.thecodefather.untigrito.vibecoding3.professional.data.remote

import com.thecodefather.untigrito.vibecoding3.professional.data.dto.*
import retrofit2.http.*

/**
 * Servicio API para operaciones relacionadas con Trabajos
 */
interface ProfessionalJobsApiService {
    
    /**
     * Obtiene la lista de trabajos disponibles con paginación
     */
    @GET("/api/services/postings/list")
    suspend fun getJobs(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20,
        @Query("latitude") latitude: Double? = null,
        @Query("longitude") longitude: Double? = null,
        @Query("radius") radius: Double? = null,
        @Query("category") category: String? = null,
        @Query("sort_by") sortBy: String? = "recent"
    ): ApiResponse<PaginatedResponse<JobResponse>>
    
    /**
     * Busca trabajos por palabras clave
     */
    @GET("/api/services/postings/list")
    suspend fun searchJobs(
        @Query("search") query: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20
    ): ApiResponse<PaginatedResponse<JobResponse>>
    
    /**
     * Obtiene los detalles de un trabajo específico
     */
    @GET("/api/services/postings/{jobId}")
    suspend fun getJobDetails(
        @Path("jobId") jobId: String
    ): ApiResponse<JobResponse>
    
    /**
     * Marca/desmarca un trabajo como favorito
     */
    @POST("/api/professionals/favorites/{jobId}")
    suspend fun toggleFavorite(
        @Path("jobId") jobId: String,
        @Body request: Map<String, Boolean>
    ): ApiResponse<Map<String, Any>>
}

/**
 * Servicio API para operaciones relacionadas con Propuestas
 */
interface ProfessionalProposalsApiService {
    
    /**
     * Obtiene la lista de propuestas enviadas por el profesional
     */
    @GET("/api/services/offers/list")
    suspend fun getProposals(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20,
        @Query("status") status: String? = null
    ): ApiResponse<PaginatedResponse<ProposalResponse>>
    
    /**
     * Obtiene los detalles de una propuesta específica
     */
    @GET("/api/services/offers/{proposalId}")
    suspend fun getProposalDetails(
        @Path("proposalId") proposalId: String
    ): ApiResponse<ProposalResponse>
    
    /**
     * Crea una nueva propuesta para un trabajo
     */
    @POST("/api/services/offers/create")
    suspend fun createProposal(
        @Body request: CreateProposalRequest
    ): ApiResponse<ProposalResponse>
    
    /**
     * Actualiza una propuesta existente
     */
    @PUT("/api/services/offers/{proposalId}")
    suspend fun updateProposal(
        @Path("proposalId") proposalId: String,
        @Body request: CreateProposalRequest
    ): ApiResponse<ProposalResponse>
    
    /**
     * Cancela una propuesta
     */
    @DELETE("/api/services/offers/{proposalId}")
    suspend fun cancelProposal(
        @Path("proposalId") proposalId: String
    ): ApiResponse<Map<String, Any>>
}

/**
 * Servicio API para operaciones relacionadas con Servicios del Profesional
 */
interface ProfessionalServicesApiService {
    
    /**
     * Obtiene la lista de servicios publicados por el profesional
     */
    @GET("/api/professionals/services")
    suspend fun getServices(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20,
        @Query("status") status: String? = null
    ): ApiResponse<PaginatedResponse<ServiceResponse>>
    
    /**
     * Obtiene los detalles de un servicio específico
     */
    @GET("/api/professionals/services/{serviceId}")
    suspend fun getServiceDetails(
        @Path("serviceId") serviceId: String
    ): ApiResponse<ServiceResponse>
    
    /**
     * Crea un nuevo servicio
     */
    @POST("/api/professionals/services/create")
    suspend fun createService(
        @Body request: CreateServiceRequest
    ): ApiResponse<ServiceResponse>
    
    /**
     * Actualiza un servicio existente
     */
    @PUT("/api/professionals/services/{serviceId}")
    suspend fun updateService(
        @Path("serviceId") serviceId: String,
        @Body request: CreateServiceRequest
    ): ApiResponse<ServiceResponse>
    
    /**
     * Elimina un servicio
     */
    @DELETE("/api/professionals/services/{serviceId}")
    suspend fun deleteService(
        @Path("serviceId") serviceId: String
    ): ApiResponse<Map<String, Any>>
}

/**
 * Servicio API para operaciones relacionadas con Mensajes y Conversaciones
 */
interface ProfessionalMessagesApiService {
    
    /**
     * Obtiene la lista de conversaciones del profesional
     */
    @GET("/api/messages/conversations")
    suspend fun getConversations(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20
    ): ApiResponse<PaginatedResponse<ConversationResponse>>
    
    /**
     * Obtiene los mensajes de una conversación específica
     */
    @GET("/api/messages/conversations/{conversationId}")
    suspend fun getMessages(
        @Path("conversationId") conversationId: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 50
    ): ApiResponse<PaginatedResponse<MessageResponse>>
    
    /**
     * Envía un nuevo mensaje
     */
    @POST("/api/messages/send")
    suspend fun sendMessage(
        @Body request: SendMessageRequest
    ): ApiResponse<MessageResponse>
    
    /**
     * Marca todos los mensajes de una conversación como leídos
     */
    @PUT("/api/messages/conversations/{conversationId}/read")
    suspend fun markConversationAsRead(
        @Path("conversationId") conversationId: String
    ): ApiResponse<Map<String, Any>>
    
    /**
     * Obtiene la conversación de soporte
     */
    @GET("/api/messages/support")
    suspend fun getSupportConversation(): ApiResponse<ConversationResponse>
}

/**
 * Servicio API para operaciones relacionadas con Calificaciones y Reseñas
 */
interface ProfessionalRatingsApiService {
    
    /**
     * Obtiene las calificaciones del profesional
     */
    @GET("/api/professionals/{professionalId}/ratings")
    suspend fun getProfessionalRatings(
        @Path("professionalId") professionalId: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20
    ): ApiResponse<PaginatedResponse<RatingResponse>>
    
    /**
     * Obtiene las estadísticas del profesional
     */
    @GET("/api/professionals/{professionalId}/stats")
    suspend fun getProfessionalStats(
        @Path("professionalId") professionalId: String
    ): ApiResponse<Map<String, Any>>
}

/**
 * Servicio API para operaciones relacionadas con Notificaciones
 */
interface ProfessionalNotificationsApiService {
    
    /**
     * Obtiene las notificaciones del profesional
     */
    @GET("/api/notifications")
    suspend fun getNotifications(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20,
        @Query("unread_only") unreadOnly: Boolean = false
    ): ApiResponse<PaginatedResponse<NotificationResponse>>
    
    /**
     * Marca una notificación como leída
     */
    @PUT("/api/notifications/{notificationId}/read")
    suspend fun markNotificationAsRead(
        @Path("notificationId") notificationId: String
    ): ApiResponse<Map<String, Any>>
    
    /**
     * Marca todas las notificaciones como leídas
     */
    @PUT("/api/notifications/read-all")
    suspend fun markAllNotificationsAsRead(): ApiResponse<Map<String, Any>>
}
