package com.thecodefather.untigrito.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecodefather.untigrito.auth.domain.usecase.AuthStateManager
import com.thecodefather.untigrito.data.datasource.remote.SupabaseDatabaseService
import com.thecodefather.untigrito.data.datasource.remote.SupabaseStorageService
import com.thecodefather.untigrito.data.datasource.remote.SupabaseProfessionalProfile
import com.thecodefather.untigrito.data.datasource.remote.SupabaseUser
import com.thecodefather.untigrito.domain.model.User
import com.thecodefather.untigrito.domain.model.UserType
import android.content.Context
import android.net.Uri
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class ProfessionalViewModel @Inject constructor(
    private val supabaseDatabase: SupabaseDatabaseService,
    private val supabaseStorage: SupabaseStorageService,
    private val postgrest: Postgrest,
    private val authStateManager: AuthStateManager
) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _professionalProfile = MutableStateFlow<SupabaseProfessionalProfile?>(null)
    val professionalProfile: StateFlow<SupabaseProfessionalProfile?> = _professionalProfile.asStateFlow()

    private val _balance = MutableStateFlow(0.0)
    val balance: StateFlow<Double> = _balance.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _logoutSuccess = MutableStateFlow(false)
    val logoutSuccess: StateFlow<Boolean> = _logoutSuccess.asStateFlow()

    // Estados editables para el perfil
    private val _editableName = MutableStateFlow("")
    val editableName: StateFlow<String> = _editableName.asStateFlow()

    private val _editableEmail = MutableStateFlow("")
    val editableEmail: StateFlow<String> = _editableEmail.asStateFlow()

    private val _editableBio = MutableStateFlow("")
    val editableBio: StateFlow<String> = _editableBio.asStateFlow()

    private val _editableHourlyRate = MutableStateFlow("")
    val editableHourlyRate: StateFlow<String> = _editableHourlyRate.asStateFlow()

    private val _editableCertifications = MutableStateFlow("")
    val editableCertifications: StateFlow<String> = _editableCertifications.asStateFlow()

    private val _editableResponseTime = MutableStateFlow("")
    val editableResponseTime: StateFlow<String> = _editableResponseTime.asStateFlow()

    private val _editableBankAccount = MutableStateFlow("")
    val editableBankAccount: StateFlow<String> = _editableBankAccount.asStateFlow()

    private val _editableTaxId = MutableStateFlow("")
    val editableTaxId: StateFlow<String> = _editableTaxId.asStateFlow()

    private val _editableSpecialties = MutableStateFlow("")
    val editableSpecialties: StateFlow<String> = _editableSpecialties.asStateFlow()

    // Estados de éxito/error para feedback
    private val _updateSuccess = MutableStateFlow(false)
    val updateSuccess: StateFlow<Boolean> = _updateSuccess.asStateFlow()

    private val _isUpdating = MutableStateFlow(false)
    val isUpdating: StateFlow<Boolean> = _isUpdating.asStateFlow()

    // Estados para gestión de imágenes
    private val _profileImageUrl = MutableStateFlow<String?>(null)
    val profileImageUrl: StateFlow<String?> = _profileImageUrl.asStateFlow()

    private val _isUploadingImage = MutableStateFlow(false)
    val isUploadingImage: StateFlow<Boolean> = _isUploadingImage.asStateFlow()

    init {
        loadCurrentUser()
    }

    /**
     * Carga el usuario actual desde AuthStateManager
     */
    fun loadCurrentUser() {
        viewModelScope.launch {
            try {
                val user = authStateManager.getCurrentUser()
                _currentUser.value = user
                
                if (user != null) {
                    loadProfessionalProfile(user.id)
                    loadUserBalance(user.id)
                    loadProfileImage(user.id)
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading current user")
                _errorMessage.value = e.message ?: "Error al cargar usuario"
            }
        }
    }

    /**
     * Carga el perfil profesional desde Supabase
     */
    fun loadProfessionalProfile(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                Timber.d("🔍 PROFESSIONAL VIEWMODEL - Loading professional profile for user: $userId")
                
                val result = postgrest.from("ProfessionalProfile")
                    .select {
                        filter {
                            eq("userId", userId)
                        }
                    }
                    .decodeSingleOrNull<SupabaseProfessionalProfile>()
                
                if (result != null) {
                    _professionalProfile.value = result
                    // Cargar datos editables
                    loadEditableData()
                    Timber.d("✅ PROFESSIONAL VIEWMODEL - Profile loaded successfully")
                } else {
                    Timber.w("⚠️ PROFESSIONAL VIEWMODEL - No profile found for user: $userId")
                    _errorMessage.value = "No se encontró perfil profesional"
                }
                
            } catch (e: Exception) {
                Timber.e(e, "❌ PROFESSIONAL VIEWMODEL - Error loading profile")
                _errorMessage.value = e.message ?: "Error al cargar perfil profesional"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Carga el saldo del usuario desde la tabla User
     */
    fun loadUserBalance(userId: String) {
        viewModelScope.launch {
            try {
                Timber.d("💰 PROFESSIONAL VIEWMODEL - Loading balance for user: $userId")
                
                val result = postgrest.from("User")
                    .select {
                        filter {
                            eq("id", userId)
                        }
                    }
                    .decodeSingleOrNull<SupabaseUser>()
                
                if (result != null) {
                    _balance.value = result.balance
                    Timber.d("✅ PROFESSIONAL VIEWMODEL - Balance loaded: ${result.balance}")
                } else {
                    Timber.w("⚠️ PROFESSIONAL VIEWMODEL - User not found for balance")
                    _balance.value = 0.0
                }
                
            } catch (e: Exception) {
                Timber.e(e, "❌ PROFESSIONAL VIEWMODEL - Error loading balance")
                _balance.value = 0.0
            }
        }
    }

    /**
     * Actualiza el perfil profesional
     */
    fun updateProfile(profile: SupabaseProfessionalProfile) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                Timber.d("💾 PROFESSIONAL VIEWMODEL - Updating profile: ${profile.id}")
                
                val result = supabaseDatabase.update(
                    "ProfessionalProfile",
                    profile.id,
                    profile
                )
                
                result.onSuccess { updatedProfile ->
                    _professionalProfile.value = updatedProfile
                    Timber.d("✅ PROFESSIONAL VIEWMODEL - Profile updated successfully")
                }.onFailure { exception ->
                    Timber.e(exception, "❌ PROFESSIONAL VIEWMODEL - Error updating profile")
                    _errorMessage.value = exception.message ?: "Error al actualizar perfil"
                }
                
            } catch (e: Exception) {
                Timber.e(e, "❌ PROFESSIONAL VIEWMODEL - Error updating profile")
                _errorMessage.value = e.message ?: "Error al actualizar perfil"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Realiza logout del usuario
     */
    fun logout() {
        viewModelScope.launch {
            try {
                Timber.d("🚪 PROFESSIONAL VIEWMODEL - Logging out user")
                
                authStateManager.clearAuthState()
                _currentUser.value = null
                _professionalProfile.value = null
                _balance.value = 0.0
                _logoutSuccess.value = true
                
                Timber.d("✅ PROFESSIONAL VIEWMODEL - Logout successful")
                
            } catch (e: Exception) {
                Timber.e(e, "❌ PROFESSIONAL VIEWMODEL - Error during logout")
                _errorMessage.value = e.message ?: "Error al cerrar sesión"
            }
        }
    }

    /**
     * Limpia los errores
     */
    fun clearError() {
        _errorMessage.value = null
    }

    /**
     * Resetea el estado de logout
     */
    fun resetLogoutSuccess() {
        _logoutSuccess.value = false
    }

    /**
     * Carga los datos editables desde el perfil y usuario actual
     */
    fun loadEditableData() {
        val user = _currentUser.value
        val profile = _professionalProfile.value
        
        _editableName.value = user?.name ?: ""
        _editableEmail.value = user?.email ?: ""
        _editableBio.value = profile?.bio ?: ""
        _editableHourlyRate.value = profile?.hourlyRate?.toString() ?: ""
        _editableCertifications.value = profile?.certifications ?: ""
        _editableResponseTime.value = profile?.responseTime?.toString() ?: ""
        _editableBankAccount.value = profile?.bankAccount ?: ""
        _editableTaxId.value = profile?.taxId ?: ""
        _editableSpecialties.value = profile?.specialties?.toString()?.trim('"') ?: ""
    }

    /**
     * Actualiza la información del usuario (nombre, email)
     */
    fun updateUserInfo(name: String, email: String) {
        viewModelScope.launch {
            _isUpdating.value = true
            _errorMessage.value = null
            
            try {
                val user = _currentUser.value
                if (user == null) {
                    _errorMessage.value = "Usuario no encontrado"
                    return@launch
                }

                // Validar email
                if (!isValidEmail(email)) {
                    _errorMessage.value = "Email no válido"
                    return@launch
                }

                Timber.d("💾 PROFESSIONAL VIEWMODEL - Updating user info")
                
                val result = supabaseDatabase.updateUser(user.id, name, email)
                
                result.onSuccess { updatedUser ->
                    _currentUser.value = updatedUser?.let { 
                        User(
                            id = it.id,
                            name = it.name ?: "",
                            email = it.email ?: "",
                            userType = UserType.valueOf(it.role)
                        )
                    }
                    _editableName.value = name
                    _editableEmail.value = email
                    _updateSuccess.value = true
                    Timber.d("✅ PROFESSIONAL VIEWMODEL - User info updated successfully")
                }.onFailure { exception ->
                    Timber.e(exception, "❌ PROFESSIONAL VIEWMODEL - Error updating user info")
                    _errorMessage.value = exception.message ?: "Error al actualizar información del usuario"
                }
                
            } catch (e: Exception) {
                Timber.e(e, "❌ PROFESSIONAL VIEWMODEL - Error updating user info")
                _errorMessage.value = e.message ?: "Error al actualizar información del usuario"
            } finally {
                _isUpdating.value = false
            }
        }
    }

    /**
     * Actualiza el perfil profesional con validaciones
     */
    fun updateProfessionalProfile(
        bio: String,
        hourlyRate: String,
        certifications: String,
        responseTime: String,
        bankAccount: String,
        taxId: String,
        specialties: String
    ) {
        viewModelScope.launch {
            _isUpdating.value = true
            _errorMessage.value = null
            
            try {
                val profile = _professionalProfile.value
                if (profile == null) {
                    _errorMessage.value = "Perfil profesional no encontrado"
                    return@launch
                }

                // Validaciones
                val hourlyRateDouble = hourlyRate.toDoubleOrNull()
                if (hourlyRateDouble != null && hourlyRateDouble <= 0) {
                    _errorMessage.value = "La tarifa por hora debe ser mayor a 0"
                    return@launch
                }

                val responseTimeInt = responseTime.toIntOrNull()
                if (responseTimeInt != null && responseTimeInt <= 0) {
                    _errorMessage.value = "El tiempo de respuesta debe ser mayor a 0"
                    return@launch
                }

                Timber.d("💾 PROFESSIONAL VIEWMODEL - Updating professional profile")
                
                val updatedProfile = profile.copy(
                    bio = bio.ifEmpty { null },
                    hourlyRate = hourlyRateDouble,
                    certifications = certifications.ifEmpty { null },
                    responseTime = responseTimeInt,
                    bankAccount = bankAccount.ifEmpty { null },
                    taxId = taxId.ifEmpty { null },
                    specialties = if (specialties.isNotEmpty()) {
                        kotlinx.serialization.json.Json.parseToJsonElement("\"$specialties\"")
                    } else null
                )
                
                val result = supabaseDatabase.update(
                    "ProfessionalProfile",
                    profile.id,
                    updatedProfile
                )
                
                result.onSuccess { updated ->
                    _professionalProfile.value = updated
                    _updateSuccess.value = true
                    Timber.d("✅ PROFESSIONAL VIEWMODEL - Professional profile updated successfully")
                }.onFailure { exception ->
                    Timber.e(exception, "❌ PROFESSIONAL VIEWMODEL - Error updating profile")
                    _errorMessage.value = exception.message ?: "Error al actualizar perfil profesional"
                }
                
            } catch (e: Exception) {
                Timber.e(e, "❌ PROFESSIONAL VIEWMODEL - Error updating profile")
                _errorMessage.value = e.message ?: "Error al actualizar perfil profesional"
            } finally {
                _isUpdating.value = false
            }
        }
    }

    /**
     * Actualiza un campo específico del perfil
     */
    fun updateField(field: String, value: String) {
        when (field) {
            "name" -> _editableName.value = value
            "email" -> _editableEmail.value = value
            "bio" -> _editableBio.value = value
            "hourlyRate" -> _editableHourlyRate.value = value
            "certifications" -> _editableCertifications.value = value
            "responseTime" -> _editableResponseTime.value = value
            "bankAccount" -> _editableBankAccount.value = value
            "taxId" -> _editableTaxId.value = value
            "specialties" -> _editableSpecialties.value = value
        }
    }

    /**
     * Guarda todos los cambios del perfil
     */
    fun saveProfileChanges() {
        val user = _currentUser.value
        if (user == null) {
            _errorMessage.value = "Usuario no encontrado"
            return
        }

        // Actualizar información del usuario
        updateUserInfo(_editableName.value, _editableEmail.value)
        
        // Actualizar perfil profesional
        updateProfessionalProfile(
            bio = _editableBio.value,
            hourlyRate = _editableHourlyRate.value,
            certifications = _editableCertifications.value,
            responseTime = _editableResponseTime.value,
            bankAccount = _editableBankAccount.value,
            taxId = _editableTaxId.value,
            specialties = _editableSpecialties.value
        )
    }

    /**
     * Valida si un email es válido
     */
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /**
     * Resetea el estado de éxito
     */
    fun resetUpdateSuccess() {
        _updateSuccess.value = false
    }

    /**
     * Carga la imagen de perfil del usuario
     */
    fun loadProfileImage(userId: String) {
        viewModelScope.launch {
            try {
                Timber.d("🖼️ PROFESSIONAL VIEWMODEL - Loading profile image for user: $userId")
                
                val imageUrl = supabaseStorage.getProfileImageUrl(userId)
                _profileImageUrl.value = imageUrl
                
                if (imageUrl != null) {
                    Timber.d("✅ PROFESSIONAL VIEWMODEL - Profile image loaded: $imageUrl")
                } else {
                    Timber.d("ℹ️ PROFESSIONAL VIEWMODEL - No profile image found")
                }
                
            } catch (e: Exception) {
                Timber.e(e, "❌ PROFESSIONAL VIEWMODEL - Error loading profile image")
                _errorMessage.value = "Error al cargar imagen de perfil"
            }
        }
    }

    /**
     * Sube una nueva imagen de perfil
     */
    fun uploadProfileImage(imageUri: Uri, context: Context) {
        viewModelScope.launch {
            _isUploadingImage.value = true
            _errorMessage.value = null
            
            try {
                val user = _currentUser.value
                if (user == null) {
                    _errorMessage.value = "Usuario no encontrado"
                    return@launch
                }

                Timber.d("📤 PROFESSIONAL VIEWMODEL - Uploading profile image")
                
                val result = supabaseStorage.uploadProfileImage(user.id, imageUri, context)
                
                result.onSuccess { imageUrl ->
                    _profileImageUrl.value = imageUrl
                    _updateSuccess.value = true
                    Timber.d("✅ PROFESSIONAL VIEWMODEL - Profile image uploaded successfully")
                }.onFailure { exception ->
                    Timber.e(exception, "❌ PROFESSIONAL VIEWMODEL - Error uploading profile image")
                    _errorMessage.value = exception.message ?: "Error al subir imagen de perfil"
                }
                
            } catch (e: Exception) {
                Timber.e(e, "❌ PROFESSIONAL VIEWMODEL - Error uploading profile image")
                _errorMessage.value = e.message ?: "Error al subir imagen de perfil"
            } finally {
                _isUploadingImage.value = false
            }
        }
    }

    /**
     * Elimina la imagen de perfil actual
     */
    fun removeProfileImage() {
        viewModelScope.launch {
            _isUploadingImage.value = true
            _errorMessage.value = null
            
            try {
                val currentImageUrl = _profileImageUrl.value
                if (currentImageUrl == null) {
                    _errorMessage.value = "No hay imagen para eliminar"
                    return@launch
                }

                Timber.d("🗑️ PROFESSIONAL VIEWMODEL - Removing profile image")
                
                val result = supabaseStorage.deleteProfileImage(currentImageUrl)
                
                result.onSuccess {
                    _profileImageUrl.value = null
                    _updateSuccess.value = true
                    Timber.d("✅ PROFESSIONAL VIEWMODEL - Profile image removed successfully")
                }.onFailure { exception ->
                    Timber.e(exception, "❌ PROFESSIONAL VIEWMODEL - Error removing profile image")
                    _errorMessage.value = exception.message ?: "Error al eliminar imagen de perfil"
                }
                
            } catch (e: Exception) {
                Timber.e(e, "❌ PROFESSIONAL VIEWMODEL - Error removing profile image")
                _errorMessage.value = e.message ?: "Error al eliminar imagen de perfil"
            } finally {
                _isUploadingImage.value = false
            }
        }
    }
}
