package com.thecodefather.untigrito.vibecoding3.professional.data.repository

import com.thecodefather.untigrito.vibecoding3.professional.data.mapper.JobMapper
import com.thecodefather.untigrito.vibecoding3.professional.data.remote.ProfessionalJobsApiService
import com.thecodefather.untigrito.vibecoding3.professional.domain.model.Job
import com.thecodefather.untigrito.vibecoding3.professional.domain.repository.JobsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Implementación del repositorio de Trabajos
 */
class JobsRepositoryImpl(
    private val apiService: ProfessionalJobsApiService
) : JobsRepository {
    
    private val favoritesFlow = MutableStateFlow<List<String>>(emptyList())
    
    override suspend fun getJobs(
        page: Int,
        perPage: Int,
        latitude: Double?,
        longitude: Double?,
        radius: Double?,
        category: String?,
        sortBy: String?
    ): Result<List<Job>> = try {
        val response = apiService.getJobs(
            page = page,
            perPage = perPage,
            latitude = latitude,
            longitude = longitude,
            radius = radius,
            category = category,
            sortBy = sortBy
        )
        
        if (response.success && response.data != null) {
            val jobs = JobMapper.toDomainList(response.data.data)
            Result.success(jobs)
        } else {
            Result.failure(Exception(response.error ?: "Unknown error"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    override suspend fun searchJobs(
        query: String,
        page: Int,
        perPage: Int
    ): Result<List<Job>> = try {
        val response = apiService.searchJobs(
            query = query,
            page = page,
            perPage = perPage
        )
        
        if (response.success && response.data != null) {
            val jobs = JobMapper.toDomainList(response.data.data)
            Result.success(jobs)
        } else {
            Result.failure(Exception(response.error ?: "Unknown error"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    override suspend fun getJobDetails(jobId: String): Result<Job> = try {
        val response = apiService.getJobDetails(jobId)
        
        if (response.success && response.data != null) {
            val job = JobMapper.toDomain(response.data)
            Result.success(job)
        } else {
            Result.failure(Exception(response.error ?: "Unknown error"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    override suspend fun toggleFavorite(jobId: String, isFavorite: Boolean): Result<Boolean> = try {
        val response = apiService.toggleFavorite(
            jobId = jobId,
            request = mapOf("isFavorite" to isFavorite)
        )
        
        if (response.success) {
            updateFavoritesFlow(jobId, isFavorite)
            Result.success(true)
        } else {
            Result.failure(Exception(response.error ?: "Unknown error"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    override fun getFavoritesFlow(): Flow<List<String>> = favoritesFlow.asStateFlow()
    
    private fun updateFavoritesFlow(jobId: String, isFavorite: Boolean) {
        val currentFavorites = favoritesFlow.value.toMutableList()
        if (isFavorite) {
            if (!currentFavorites.contains(jobId)) {
                currentFavorites.add(jobId)
            }
        } else {
            currentFavorites.remove(jobId)
        }
        favoritesFlow.value = currentFavorites
    }
}

/**
 * Implementación del repositorio de Propuestas
 */
class ProposalsRepositoryImpl(
    private val apiService: com.thecodefather.untigrito.vibecoding3.professional.data.remote.ProfessionalProposalsApiService
) : com.thecodefather.untigrito.vibecoding3.professional.domain.repository.ProposalsRepository {
    
    override suspend fun getProposals(
        page: Int,
        perPage: Int,
        status: String?
    ): Result<List<com.thecodefather.untigrito.vibecoding3.professional.domain.model.Proposal>> = try {
        val response = apiService.getProposals(page = page, perPage = perPage, status = status)
        
        if (response.success && response.data != null) {
            val proposals = com.thecodefather.untigrito.vibecoding3.professional.data.mapper.ProposalMapper
                .toDomainList(response.data.data)
            Result.success(proposals)
        } else {
            Result.failure(Exception(response.error ?: "Unknown error"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    override suspend fun getProposalDetails(proposalId: String): Result<com.thecodefather.untigrito.vibecoding3.professional.domain.model.Proposal> = try {
        val response = apiService.getProposalDetails(proposalId)
        
        if (response.success && response.data != null) {
            val proposal = com.thecodefather.untigrito.vibecoding3.professional.data.mapper.ProposalMapper.toDomain(response.data)
            Result.success(proposal)
        } else {
            Result.failure(Exception(response.error ?: "Unknown error"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    override suspend fun createProposal(
        jobId: String,
        amount: Double,
        description: String,
        includesMaterials: Boolean,
        offerWarranty: Boolean,
        warrantyDescription: String?,
        terms: String?
    ): Result<com.thecodefather.untigrito.vibecoding3.professional.domain.model.Proposal> = try {
        val request = com.thecodefather.untigrito.vibecoding3.professional.data.dto.CreateProposalRequest(
            jobId = jobId,
            amount = amount,
            description = description,
            includesMaterials = includesMaterials,
            offerWarranty = offerWarranty,
            warrantyDescription = warrantyDescription,
            terms = terms
        )
        
        val response = apiService.createProposal(request)
        
        if (response.success && response.data != null) {
            val proposal = com.thecodefather.untigrito.vibecoding3.professional.data.mapper.ProposalMapper.toDomain(response.data)
            Result.success(proposal)
        } else {
            Result.failure(Exception(response.error ?: "Unknown error"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    override suspend fun updateProposal(
        proposalId: String,
        amount: Double,
        description: String
    ): Result<com.thecodefather.untigrito.vibecoding3.professional.domain.model.Proposal> = try {
        val request = com.thecodefather.untigrito.vibecoding3.professional.data.dto.CreateProposalRequest(
            jobId = "",
            amount = amount,
            description = description
        )
        
        val response = apiService.updateProposal(proposalId, request)
        
        if (response.success && response.data != null) {
            val proposal = com.thecodefather.untigrito.vibecoding3.professional.data.mapper.ProposalMapper.toDomain(response.data)
            Result.success(proposal)
        } else {
            Result.failure(Exception(response.error ?: "Unknown error"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    override suspend fun cancelProposal(proposalId: String): Result<Boolean> = try {
        val response = apiService.cancelProposal(proposalId)
        
        if (response.success) {
            Result.success(true)
        } else {
            Result.failure(Exception(response.error ?: "Unknown error"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}

/**
 * Implementación del repositorio de Servicios
 */
class ServicesRepositoryImpl(
    private val apiService: com.thecodefather.untigrito.vibecoding3.professional.data.remote.ProfessionalServicesApiService
) : com.thecodefather.untigrito.vibecoding3.professional.domain.repository.ServicesRepository {
    
    override suspend fun getServices(
        page: Int,
        perPage: Int,
        status: String?
    ): Result<List<com.thecodefather.untigrito.vibecoding3.professional.domain.model.Service>> = try {
        val response = apiService.getServices(page = page, perPage = perPage, status = status)
        
        if (response.success && response.data != null) {
            val services = com.thecodefather.untigrito.vibecoding3.professional.data.mapper.ServiceMapper
                .toDomainList(response.data.data)
            Result.success(services)
        } else {
            Result.failure(Exception(response.error ?: "Unknown error"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    override suspend fun getServiceDetails(serviceId: String): Result<com.thecodefather.untigrito.vibecoding3.professional.domain.model.Service> = try {
        val response = apiService.getServiceDetails(serviceId)
        
        if (response.success && response.data != null) {
            val service = com.thecodefather.untigrito.vibecoding3.professional.data.mapper.ServiceMapper.toDomain(response.data)
            Result.success(service)
        } else {
            Result.failure(Exception(response.error ?: "Unknown error"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    override suspend fun createService(
        title: String,
        description: String,
        category: String,
        priceMin: Double,
        priceMax: Double,
        location: String,
        serviceZone: String?,
        images: List<String>,
        tags: List<String>
    ): Result<com.thecodefather.untigrito.vibecoding3.professional.domain.model.Service> = try {
        val request = com.thecodefather.untigrito.vibecoding3.professional.data.dto.CreateServiceRequest(
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
        
        val response = apiService.createService(request)
        
        if (response.success && response.data != null) {
            val service = com.thecodefather.untigrito.vibecoding3.professional.data.mapper.ServiceMapper.toDomain(response.data)
            Result.success(service)
        } else {
            Result.failure(Exception(response.error ?: "Unknown error"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    override suspend fun updateService(
        serviceId: String,
        title: String,
        description: String,
        category: String,
        priceMin: Double,
        priceMax: Double,
        location: String,
        serviceZone: String?,
        images: List<String>,
        tags: List<String>
    ): Result<com.thecodefather.untigrito.vibecoding3.professional.domain.model.Service> = try {
        val request = com.thecodefather.untigrito.vibecoding3.professional.data.dto.CreateServiceRequest(
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
        
        val response = apiService.updateService(serviceId, request)
        
        if (response.success && response.data != null) {
            val service = com.thecodefather.untigrito.vibecoding3.professional.data.mapper.ServiceMapper.toDomain(response.data)
            Result.success(service)
        } else {
            Result.failure(Exception(response.error ?: "Unknown error"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    override suspend fun deleteService(serviceId: String): Result<Boolean> = try {
        val response = apiService.deleteService(serviceId)
        
        if (response.success) {
            Result.success(true)
        } else {
            Result.failure(Exception(response.error ?: "Unknown error"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}

/**
 * Implementación del repositorio de Mensajes
 */
class MessagesRepositoryImpl(
    private val apiService: com.thecodefather.untigrito.vibecoding3.professional.data.remote.ProfessionalMessagesApiService
) : com.thecodefather.untigrito.vibecoding3.professional.domain.repository.MessagesRepository {
    
    override suspend fun getConversations(
        page: Int,
        perPage: Int
    ): Result<List<com.thecodefather.untigrito.vibecoding3.professional.domain.model.Conversation>> = try {
        val response = apiService.getConversations(page, perPage)
        
        if (response.success && response.data != null) {
            val conversations = com.thecodefather.untigrito.vibecoding3.professional.data.mapper.ConversationMapper
                .toDomainList(response.data.data)
            Result.success(conversations)
        } else {
            Result.failure(Exception(response.error ?: "Unknown error"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    override suspend fun getMessages(
        conversationId: String,
        page: Int,
        perPage: Int
    ): Result<List<com.thecodefather.untigrito.vibecoding3.professional.domain.model.Message>> = try {
        val response = apiService.getMessages(conversationId, page, perPage)
        
        if (response.success && response.data != null) {
            val messages = com.thecodefather.untigrito.vibecoding3.professional.data.mapper.MessageMapper
                .toDomainList(response.data.data)
            Result.success(messages)
        } else {
            Result.failure(Exception(response.error ?: "Unknown error"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    override suspend fun sendMessage(
        conversationId: String,
        content: String,
        type: String,
        mediaUrl: String?
    ): Result<com.thecodefather.untigrito.vibecoding3.professional.domain.model.Message> = try {
        val request = com.thecodefather.untigrito.vibecoding3.professional.data.dto.SendMessageRequest(
            conversationId = conversationId,
            content = content,
            type = type,
            mediaUrl = mediaUrl
        )
        
        val response = apiService.sendMessage(request)
        
        if (response.success && response.data != null) {
            val message = com.thecodefather.untigrito.vibecoding3.professional.data.mapper.MessageMapper.toDomain(response.data)
            Result.success(message)
        } else {
            Result.failure(Exception(response.error ?: "Unknown error"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    override suspend fun markConversationAsRead(conversationId: String): Result<Boolean> = try {
        val response = apiService.markConversationAsRead(conversationId)
        
        if (response.success) {
            Result.success(true)
        } else {
            Result.failure(Exception(response.error ?: "Unknown error"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    override suspend fun getSupportConversation(): Result<com.thecodefather.untigrito.vibecoding3.professional.domain.model.Conversation> = try {
        val response = apiService.getSupportConversation()
        
        if (response.success && response.data != null) {
            val conversation = com.thecodefather.untigrito.vibecoding3.professional.data.mapper.ConversationMapper.toDomain(response.data)
            Result.success(conversation)
        } else {
            Result.failure(Exception(response.error ?: "Unknown error"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}

/**
 * Implementación del repositorio de Calificaciones
 */
class RatingsRepositoryImpl(
    private val apiService: com.thecodefather.untigrito.vibecoding3.professional.data.remote.ProfessionalRatingsApiService
) : com.thecodefather.untigrito.vibecoding3.professional.domain.repository.RatingsRepository {
    
    override suspend fun getProfessionalRatings(
        professionalId: String,
        page: Int,
        perPage: Int
    ): Result<List<com.thecodefather.untigrito.vibecoding3.professional.domain.model.Rating>> = try {
        val response = apiService.getProfessionalRatings(professionalId, page, perPage)
        
        if (response.success && response.data != null) {
            val ratings = com.thecodefather.untigrito.vibecoding3.professional.data.mapper.RatingMapper
                .toDomainList(response.data.data)
            Result.success(ratings)
        } else {
            Result.failure(Exception(response.error ?: "Unknown error"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    override suspend fun getProfessionalStats(professionalId: String): Result<Map<String, Any>> = try {
        val response = apiService.getProfessionalStats(professionalId)
        
        if (response.success && response.data != null) {
            Result.success(response.data)
        } else {
            Result.failure(Exception(response.error ?: "Unknown error"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}

/**
 * Implementación del repositorio de Notificaciones
 */
class NotificationsRepositoryImpl(
    private val apiService: com.thecodefather.untigrito.vibecoding3.professional.data.remote.ProfessionalNotificationsApiService
) : com.thecodefather.untigrito.vibecoding3.professional.domain.repository.NotificationsRepository {
    
    override suspend fun getNotifications(
        page: Int,
        perPage: Int,
        unreadOnly: Boolean
    ): Result<List<com.thecodefather.untigrito.vibecoding3.professional.domain.model.Notification>> = try {
        val response = apiService.getNotifications(page, perPage, unreadOnly)
        
        if (response.success && response.data != null) {
            val notifications = com.thecodefather.untigrito.vibecoding3.professional.data.mapper.NotificationMapper
                .toDomainList(response.data.data)
            Result.success(notifications)
        } else {
            Result.failure(Exception(response.error ?: "Unknown error"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    override suspend fun markNotificationAsRead(notificationId: String): Result<Boolean> = try {
        val response = apiService.markNotificationAsRead(notificationId)
        
        if (response.success) {
            Result.success(true)
        } else {
            Result.failure(Exception(response.error ?: "Unknown error"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    override suspend fun markAllNotificationsAsRead(): Result<Boolean> = try {
        val response = apiService.markAllNotificationsAsRead()
        
        if (response.success) {
            Result.success(true)
        } else {
            Result.failure(Exception(response.error ?: "Unknown error"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}
