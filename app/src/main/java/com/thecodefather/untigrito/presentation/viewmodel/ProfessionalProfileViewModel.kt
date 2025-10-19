package com.thecodefather.untigrito.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecodefather.untigrito.auth.domain.usecase.AuthStateManager
import com.thecodefather.untigrito.data.datasource.remote.SupabaseDatabaseService
import com.thecodefather.untigrito.data.datasource.remote.SupabaseProfessionalProfile
import com.thecodefather.untigrito.data.datasource.remote.SupabaseService
import com.thecodefather.untigrito.data.datasource.remote.SupabaseReview
import com.thecodefather.untigrito.data.datasource.remote.SupabaseUser
import com.thecodefather.untigrito.domain.model.Professional
import com.thecodefather.untigrito.domain.model.ProfessionalService
// import com.thecodefather.untigrito.presentation.screens.professionals.profile.Review
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.json.JsonArray
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ProfessionalProfileViewModel @Inject constructor(
    private val supabaseDatabase: SupabaseDatabaseService,
    private val authStateManager: AuthStateManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfessionalProfileUiState())
    val uiState: StateFlow<ProfessionalProfileUiState> = _uiState.asStateFlow()

    // Cargar perfil profesional completo
    fun loadProfessionalProfile(professionalId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                // Obtener información del usuario primero (professionalId es el userId)
                val userResult = supabaseDatabase.getById<SupabaseUser>("User", professionalId)
                
                // Obtener perfil profesional usando el userId
                val profileResult = supabaseDatabase.findBy<SupabaseProfessionalProfile>(
                    "ProfessionalProfile",
                    "userId",
                    professionalId
                )
                
                // Obtener servicios del profesional
                val servicesResult = supabaseDatabase.findBy<SupabaseService>(
                    "ProfessionalService",
                    "professionalId",
                    professionalId
                )
                
                // Obtener reseñas
                val reviewsResult = supabaseDatabase.findBy<SupabaseReview>(
                    "Review",
                    "professionalId",
                    professionalId
                )
                
                userResult.onSuccess { user ->
                    profileResult.onSuccess { profiles ->
                        val profile = profiles.firstOrNull() // Tomar el primer perfil si existe
                        servicesResult.onSuccess { services ->
                            reviewsResult.onSuccess { reviews ->
                                val professional = mapToProfessional(profile, user, services, reviews)
                                
                                _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    professional = professional,
                                    errorMessage = null
                                )
                            }.onFailure { exception ->
                                _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    errorMessage = "Error al cargar reseñas: ${exception.message}"
                                )
                            }
                        }.onFailure { exception ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                errorMessage = "Error al cargar servicios: ${exception.message}"
                            )
                        }
                    }.onFailure { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Error al cargar perfil: ${exception.message}"
                        )
                    }
                }.onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Error al cargar usuario: ${exception.message}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error inesperado"
                )
            }
        }
    }

    // Cargar servicios del profesional
    fun loadProfessionalServices(professionalId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingServices = true, errorMessage = null)
            
            try {
                val result = supabaseDatabase.findBy<SupabaseService>(
                    "ProfessionalService",
                    "professionalId",
                    professionalId
                )
                
                result.onSuccess { services ->
                    val domainServices = services
                        .filter { it.isActive }
                        .map { service -> mapToProfessionalService(service) }
                        .sortedByDescending { it.createdAt.toString() }
                    
                    _uiState.value = _uiState.value.copy(
                        isLoadingServices = false,
                        services = domainServices,
                        errorMessage = null
                    )
                }.onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoadingServices = false,
                        errorMessage = exception.message ?: "Error al cargar servicios"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoadingServices = false,
                    errorMessage = e.message ?: "Error inesperado"
                )
            }
        }
    }

    // Cargar reseñas del profesional
    fun loadProfessionalReviews(professionalId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingReviews = true, errorMessage = null)
            
            try {
                val result = supabaseDatabase.findBy<SupabaseReview>(
                    "Review",
                    "professionalId",
                    professionalId
                )
                
                result.onSuccess { reviews ->
                    // val domainReviews = reviews
                    //     .map { review -> mapToReview(review) }
                    //     .sortedByDescending { it.createdAt.toString() }
                    
                    _uiState.value = _uiState.value.copy(
                        isLoadingReviews = false,
                        // reviews = domainReviews,
                        errorMessage = null
                    )
                }.onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoadingReviews = false,
                        errorMessage = exception.message ?: "Error al cargar reseñas"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoadingReviews = false,
                    errorMessage = e.message ?: "Error inesperado"
                )
            }
        }
    }

    // Cargar estadísticas del profesional
    fun loadProfessionalStats(professionalId: String) {
        viewModelScope.launch {
            try {
                // Obtener servicios
                val servicesResult = supabaseDatabase.findBy<SupabaseService>(
                    "ProfessionalService",
                    "professionalId",
                    professionalId
                )
                
                // Obtener reseñas
                val reviewsResult = supabaseDatabase.findBy<SupabaseReview>(
                    "Review",
                    "professionalId",
                    professionalId
                )
                
                servicesResult.onSuccess { services ->
                    reviewsResult.onSuccess { reviews ->
                        val stats = ProfessionalStats(
                            totalServices = services.size,
                            activeServices = services.count { it.isActive },
                            totalReviews = reviews.size,
                            averageRating = if (reviews.isNotEmpty()) {
                                reviews.map { it.rating }.average()
                            } else 0.0,
                            completedJobs = 0, // Esto requeriría una consulta adicional
                            yearsOfExperience = 0 // Esto se obtendría del perfil
                        )
                        
                        _uiState.value = _uiState.value.copy(
                            stats = stats
                        )
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading professional stats")
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Error al cargar estadísticas"
                )
            }
        }
    }

    // Mappers
    private fun mapToProfessional(
        profile: SupabaseProfessionalProfile?,
        user: SupabaseUser?,
        services: List<SupabaseService>,
        reviews: List<SupabaseReview>
    ): Professional {
        val averageRating = if (reviews.isNotEmpty()) {
            reviews.map { it.rating }.average()
        } else 0.0
        
        return Professional(
            id = user?.id ?: "",
            userId = user?.name ?: "",
            bio = profile?.bio ?: "",
            rating = averageRating.toDouble(),
            totalReviews = reviews.size,
            yearsOfExperience = profile?.yearsOfExperience ?: 0,
            specialties = profile?.specialties?.let { jsonElement ->
                try {
                    if (jsonElement is kotlinx.serialization.json.JsonArray) {
                        jsonElement.map { it.toString().trim('"') }
                    } else {
                        jsonElement.toString().trim('"').split(",").map { it.trim() }
                    }
                } catch (e: Exception) {
                    emptyList()
                }
            } ?: emptyList(),
            hourlyRate = profile?.hourlyRate ?: 0.0,
            isVerified = user?.isVerified ?: false,
            imageUrl = null
        )
    }

    private fun mapToProfessionalService(service: SupabaseService): ProfessionalService {
        return ProfessionalService(
            id = service.id,
            professionalId = service.professionalId,
            title = service.title,
            description = service.description,
            price = service.price,
            categoryId = service.categoryId,
            isActive = service.isActive,
            createdAt = service.createdAt ?: "",
            updatedAt = service.updatedAt ?: ""
        )
    }

    // private fun mapToReview(review: SupabaseReview): Review {
    //     return Review(
    //         rating = review.rating,
    //         comment = review.comment ?: "",
    //         author = "Cliente",
    //         createdAt = review.createdAt?.let { 
    //             try {
    //                 java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault()).parse(it) ?: Date()
    //             } catch (e: Exception) {
    //                 Date()
    //             }
    //         } ?: Date()
    //     )
    // }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

data class ProfessionalProfileUiState(
    val isLoading: Boolean = false,
    val isLoadingServices: Boolean = false,
    val isLoadingReviews: Boolean = false,
    val professional: Professional? = null,
    val services: List<ProfessionalService> = emptyList(),
    // val reviews: List<Review> = emptyList(),
    val stats: ProfessionalStats? = null,
    val errorMessage: String? = null
)

data class ProfessionalStats(
    val totalServices: Int,
    val activeServices: Int,
    val totalReviews: Int,
    val averageRating: Double,
    val completedJobs: Int,
    val yearsOfExperience: Int
)
