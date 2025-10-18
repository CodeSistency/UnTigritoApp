package com.thecodefather.untigrito.vibecoding3.professional.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import com.thecodefather.untigrito.vibecoding3.professional.data.remote.*
import com.thecodefather.untigrito.vibecoding3.professional.data.repository.*
import com.thecodefather.untigrito.vibecoding3.professional.domain.repository.*
import com.thecodefather.untigrito.vibecoding3.professional.domain.usecase.*
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
    fun provideProfessionalJobsApiService(retrofit: Retrofit): ProfessionalJobsApiService {
        return retrofit.create(ProfessionalJobsApiService::class.java)
    }
    
    /**
     * Proporciona el servicio API para operaciones de propuestas
     */
    @Provides
    @Singleton
    fun provideProfessionalProposalsApiService(retrofit: Retrofit): ProfessionalProposalsApiService {
        return retrofit.create(ProfessionalProposalsApiService::class.java)
    }
    
    /**
     * Proporciona el servicio API para operaciones de servicios
     */
    @Provides
    @Singleton
    fun provideProfessionalServicesApiService(retrofit: Retrofit): ProfessionalServicesApiService {
        return retrofit.create(ProfessionalServicesApiService::class.java)
    }
    
    /**
     * Proporciona el servicio API para operaciones de mensajes
     */
    @Provides
    @Singleton
    fun provideProfessionalMessagesApiService(retrofit: Retrofit): ProfessionalMessagesApiService {
        return retrofit.create(ProfessionalMessagesApiService::class.java)
    }
    
    /**
     * Proporciona el servicio API para operaciones de calificaciones
     */
    @Provides
    @Singleton
    fun provideProfessionalRatingsApiService(retrofit: Retrofit): ProfessionalRatingsApiService {
        return retrofit.create(ProfessionalRatingsApiService::class.java)
    }
    
    /**
     * Proporciona el servicio API para operaciones de notificaciones
     */
    @Provides
    @Singleton
    fun provideProfessionalNotificationsApiService(retrofit: Retrofit): ProfessionalNotificationsApiService {
        return retrofit.create(ProfessionalNotificationsApiService::class.java)
    }
    
    // ==================== REPOSITORIES ====================
    
    /**
     * Proporciona el repositorio de trabajos
     */
    @Provides
    @Singleton
    fun provideJobsRepository(
        apiService: ProfessionalJobsApiService
    ): JobsRepository {
        return JobsRepositoryImpl(apiService)
    }
    
    /**
     * Proporciona el repositorio de propuestas
     */
    @Provides
    @Singleton
    fun provideProposalsRepository(
        apiService: ProfessionalProposalsApiService
    ): ProposalsRepository {
        return ProposalsRepositoryImpl(apiService)
    }
    
    /**
     * Proporciona el repositorio de servicios
     */
    @Provides
    @Singleton
    fun provideServicesRepository(
        apiService: ProfessionalServicesApiService
    ): ServicesRepository {
        return ServicesRepositoryImpl(apiService)
    }
    
    /**
     * Proporciona el repositorio de mensajes
     */
    @Provides
    @Singleton
    fun provideMessagesRepository(
        apiService: ProfessionalMessagesApiService
    ): MessagesRepository {
        return MessagesRepositoryImpl(apiService)
    }
    
    /**
     * Proporciona el repositorio de calificaciones
     */
    @Provides
    @Singleton
    fun provideRatingsRepository(
        apiService: ProfessionalRatingsApiService
    ): RatingsRepository {
        return RatingsRepositoryImpl(apiService)
    }
    
    /**
     * Proporciona el repositorio de notificaciones
     */
    @Provides
    @Singleton
    fun provideNotificationsRepository(
        apiService: ProfessionalNotificationsApiService
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
