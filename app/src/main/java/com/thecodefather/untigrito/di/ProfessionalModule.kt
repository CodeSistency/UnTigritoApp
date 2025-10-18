package com.thecodefather.untigrito.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import com.thecodefather.untigrito.data.remote.*
import com.thecodefather.untigrito.data.repository.*
import com.thecodefather.untigrito.domain.repository.JobsRepository
import com.thecodefather.untigrito.domain.repository.ProposalsRepository
import com.thecodefather.untigrito.domain.repository.ServicesRepository
import com.thecodefather.untigrito.domain.repository.MessagesRepository
import com.thecodefather.untigrito.domain.repository.RatingsRepository
import com.thecodefather.untigrito.domain.repository.NotificationsRepository
import com.thecodefather.untigrito.domain.usecase.*
import javax.inject.Singleton

/**
 * Módulo Hilt para configurar la inyección de dependencias del módulo profesional
 */
@Module
@InstallIn(SingletonComponent::class)
object ProfessionalModule {
    
    // ==================== API SERVICES ====================
    
    /**
     * Proporciona el servicio API para operaciones de trabajos
     */
    @Provides
    @Singleton
    fun provideProfessionalJobsApiService(retrofit: Retrofit): com.thecodefather.untigrito.data.remote.ProfessionalJobsApiService {
        return retrofit.create(com.thecodefather.untigrito.data.remote.ProfessionalJobsApiService::class.java)
    }
    
    /**
     * Proporciona el servicio API para operaciones de propuestas
     */
    @Provides
    @Singleton
    fun provideProfessionalProposalsApiService(retrofit: Retrofit): com.thecodefather.untigrito.data.remote.ProfessionalProposalsApiService {
        return retrofit.create(com.thecodefather.untigrito.data.remote.ProfessionalProposalsApiService::class.java)
    }
    
    /**
     * Proporciona el servicio API para operaciones de servicios
     */
    @Provides
    @Singleton
    fun provideProfessionalServicesApiService(retrofit: Retrofit): com.thecodefather.untigrito.data.remote.ProfessionalServicesApiService {
        return retrofit.create(com.thecodefather.untigrito.data.remote.ProfessionalServicesApiService::class.java)
    }
    
    /**
     * Proporciona el servicio API para operaciones de mensajes
     */
    @Provides
    @Singleton
    fun provideProfessionalMessagesApiService(retrofit: Retrofit): com.thecodefather.untigrito.data.remote.ProfessionalMessagesApiService {
        return retrofit.create(com.thecodefather.untigrito.data.remote.ProfessionalMessagesApiService::class.java)
    }
    
    /**
     * Proporciona el servicio API para operaciones de calificaciones
     */
    @Provides
    @Singleton
    fun provideProfessionalRatingsApiService(retrofit: Retrofit): com.thecodefather.untigrito.data.remote.ProfessionalRatingsApiService {
        return retrofit.create(com.thecodefather.untigrito.data.remote.ProfessionalRatingsApiService::class.java)
    }
    
    /**
     * Proporciona el servicio API para operaciones de notificaciones
     */
    @Provides
    @Singleton
    fun provideProfessionalNotificationsApiService(retrofit: Retrofit): com.thecodefather.untigrito.data.remote.ProfessionalNotificationsApiService {
        return retrofit.create(com.thecodefather.untigrito.data.remote.ProfessionalNotificationsApiService::class.java)
    }
    
    // ==================== REPOSITORIES ====================
    
    /**
     * Proporciona el repositorio de trabajos
     */
    @Provides
    @Singleton
    fun provideJobsRepository(): JobsRepository {
        return com.thecodefather.untigrito.data.repository.JobsRepositoryImpl()
    }
    
    /**
     * Proporciona el repositorio de propuestas
     */
    @Provides
    @Singleton
    fun provideProposalsRepository(): ProposalsRepository {
        return com.thecodefather.untigrito.data.repository.ProposalsRepositoryImpl()
    }
    
    /**
     * Proporciona el repositorio de servicios
     */
    @Provides
    @Singleton
    fun provideServicesRepository(): ServicesRepository {
        return com.thecodefather.untigrito.data.repository.ServicesRepositoryImpl()
    }
    
    /**
     * Proporciona el repositorio de mensajes
     */
    @Provides
    @Singleton
    fun provideMessagesRepository(): MessagesRepository {
        return com.thecodefather.untigrito.data.repository.MessagesRepositoryImpl()
    }
    
    /**
     * Proporciona el repositorio de calificaciones
     */
    @Provides
    @Singleton
    fun provideRatingsRepository(
        apiService: com.thecodefather.untigrito.data.remote.ProfessionalRatingsApiService
    ): RatingsRepository {
        return RatingsRepositoryImpl(apiService)
    }
    
    /**
     * Proporciona el repositorio de notificaciones
     */
    @Provides
    @Singleton
    fun provideNotificationsRepository(
        apiService: com.thecodefather.untigrito.data.remote.ProfessionalNotificationsApiService
    ): NotificationsRepository {
        return NotificationsRepositoryImpl(apiService)
    }
    
    // ==================== USE CASES ====================
    
    // --- Casos de uso de trabajos ---
    @Provides
    @Singleton
    fun provideGetJobsUseCase(repository: JobsRepository): GetJobsUseCase {
        return GetJobsUseCase(repository)
    }
    
    @Provides
    @Singleton
    fun provideSearchJobsUseCase(repository: JobsRepository): SearchJobsUseCase {
        return SearchJobsUseCase(repository)
    }
    
    @Provides
    @Singleton
    fun provideGetJobDetailsUseCase(repository: JobsRepository): GetJobDetailsUseCase {
        return GetJobDetailsUseCase(repository)
    }
    
    @Provides
    @Singleton
    fun provideToggleFavoriteUseCase(repository: JobsRepository): ToggleFavoriteUseCase {
        return ToggleFavoriteUseCase(repository)
    }
    
    // --- Casos de uso de propuestas ---
    @Provides
    @Singleton
    fun provideGetProposalsUseCase(repository: ProposalsRepository): GetProposalsUseCase {
        return GetProposalsUseCase(repository)
    }
    
    @Provides
    @Singleton
    fun provideGetProposalDetailsUseCase(repository: ProposalsRepository): GetProposalDetailsUseCase {
        return GetProposalDetailsUseCase(repository)
    }
    
    @Provides
    @Singleton
    fun provideCreateProposalUseCase(repository: ProposalsRepository): CreateProposalUseCase {
        return CreateProposalUseCase(repository)
    }
    
    @Provides
    @Singleton
    fun provideCancelProposalUseCase(repository: ProposalsRepository): CancelProposalUseCase {
        return CancelProposalUseCase(repository)
    }
    
    // --- Casos de uso de servicios ---
    @Provides
    @Singleton
    fun provideGetServicesUseCase(repository: ServicesRepository): GetServicesUseCase {
        return GetServicesUseCase(repository)
    }
    
    @Provides
    @Singleton
    fun provideGetServiceDetailsUseCase(repository: ServicesRepository): GetServiceDetailsUseCase {
        return GetServiceDetailsUseCase(repository)
    }
    
    @Provides
    @Singleton
    fun provideCreateServiceUseCase(repository: ServicesRepository): CreateServiceUseCase {
        return CreateServiceUseCase(repository)
    }
    
    @Provides
    @Singleton
    fun provideUpdateServiceUseCase(repository: ServicesRepository): UpdateServiceUseCase {
        return UpdateServiceUseCase(repository)
    }
    
    // --- Casos de uso de mensajes ---
    @Provides
    @Singleton
    fun provideGetConversationsUseCase(repository: MessagesRepository): GetConversationsUseCase {
        return GetConversationsUseCase(repository)
    }
    
    @Provides
    @Singleton
    fun provideGetMessagesUseCase(repository: MessagesRepository): GetMessagesUseCase {
        return GetMessagesUseCase(repository)
    }
    
    @Provides
    @Singleton
    fun provideSendMessageUseCase(repository: MessagesRepository): SendMessageUseCase {
        return SendMessageUseCase(repository)
    }
    
    // --- Casos de uso de calificaciones ---
    @Provides
    @Singleton
    fun provideGetRatingsUseCase(repository: RatingsRepository): GetRatingsUseCase {
        return GetRatingsUseCase(repository)
    }
    
    // --- Casos de uso de notificaciones ---
    @Provides
    @Singleton
    fun provideGetNotificationsUseCase(repository: NotificationsRepository): GetNotificationsUseCase {
        return GetNotificationsUseCase(repository)
    }
}
