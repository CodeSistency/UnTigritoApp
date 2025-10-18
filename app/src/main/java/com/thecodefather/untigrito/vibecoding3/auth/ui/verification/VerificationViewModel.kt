package com.example.vibecoding3.auth.ui.verification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vibecoding3.auth.data.repository.VerificationRepositoryImpl
import com.example.vibecoding3.auth.domain.model.VerificationData
import com.example.vibecoding3.core.validation.CedulaValidator
import com.example.vibecoding3.core.validation.OTPValidator
import com.example.vibecoding3.core.validation.PhoneValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para el flujo de verificación de identidad y teléfono
 */
@HiltViewModel
class VerificationViewModel @Inject constructor(
    private val verificationRepository: VerificationRepositoryImpl
) : ViewModel() {
    
    private val _verificationData = MutableStateFlow(VerificationData())
    val verificationData: StateFlow<VerificationData> = _verificationData.asStateFlow()
    
    private val _cedulaError = MutableStateFlow<String?>(null)
    val cedulaError: StateFlow<String?> = _cedulaError.asStateFlow()
    
    private val _phoneError = MutableStateFlow<String?>(null)
    val phoneError: StateFlow<String?> = _phoneError.asStateFlow()
    
    private val _otpError = MutableStateFlow<String?>(null)
    val otpError: StateFlow<String?> = _otpError.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _verificationSuccess = MutableStateFlow(false)
    val verificationSuccess: StateFlow<Boolean> = _verificationSuccess.asStateFlow()
    
    /**
     * Actualiza el número de cédula
     */
    fun updateCedula(cedula: String) {
        val cleanCedula = cedula.replace(Regex("[^0-9]"), "")
        _verificationData.value = _verificationData.value.copy(cedula = cleanCedula)
        validateCedula(cleanCedula)
    }
    
    /**
     * Actualiza el número de teléfono
     */
    fun updatePhone(phone: String) {
        val cleanPhone = phone.replace(Regex("[^0-9]"), "")
        _verificationData.value = _verificationData.value.copy(phoneNumber = cleanPhone)
        validatePhone(cleanPhone)
    }
    
    /**
     * Actualiza el código OTP
     */
    fun updateOTP(otp: String) {
        val cleanOtp = otp.replace(Regex("[^0-9]"), "").take(5)
        _verificationData.value = _verificationData.value.copy(otpCode = cleanOtp)
        if (cleanOtp.length == 5) {
            validateOTP(cleanOtp)
        }
    }
    
    /**
     * Verifica la cédula
     */
    fun verifyCedula() {
        val cedula = _verificationData.value.cedula
        if (!CedulaValidator.isValid(cedula)) {
            _cedulaError.value = "Cédula inválida (8 dígitos)"
            return
        }
        
        _isLoading.value = true
        _cedulaError.value = null
        
        viewModelScope.launch {
            val result = verificationRepository.verifyCedula(cedula)
            
            result.onSuccess {
                _verificationData.value = _verificationData.value.copy(isCedulaVerified = true)
            }.onFailure { exception ->
                _cedulaError.value = exception.message ?: "Error en verificación"
            }
            
            _isLoading.value = false
        }
    }
    
    /**
     * Verifica el teléfono
     */
    fun verifyPhone() {
        val phone = _verificationData.value.phoneNumber
        if (!PhoneValidator.isValidVenezuelanPhone(phone)) {
            _phoneError.value = "Teléfono inválido (11 dígitos, comienza con 04)"
            return
        }
        
        _isLoading.value = true
        _phoneError.value = null
        
        viewModelScope.launch {
            val result = verificationRepository.verifyPhone(phone)
            
            result.onSuccess {
                // No marcar como verificado hasta que se valide OTP
            }.onFailure { exception ->
                _phoneError.value = exception.message ?: "Error en verificación"
            }
            
            _isLoading.value = false
        }
    }
    
    /**
     * Valida el código OTP
     */
    fun validateOTP(otp: String = _verificationData.value.otpCode) {
        if (!OTPValidator.isValid(otp)) {
            _otpError.value = "Código inválido (5 dígitos)"
            return
        }
        
        _isLoading.value = true
        _otpError.value = null
        
        viewModelScope.launch {
            val result = verificationRepository.validateOTP(otp)
            
            result.onSuccess {
                _verificationData.value = _verificationData.value.copy(
                    isPhoneVerified = true,
                    otpCode = otp
                )
                _verificationSuccess.value = true
            }.onFailure { exception ->
                _otpError.value = exception.message ?: "Error en validación"
            }
            
            _isLoading.value = false
        }
    }
    
    /**
     * Salta el proceso de verificación
     */
    fun skipVerification() {
        _verificationData.value = _verificationData.value.copy(
            verificationSkipped = true,
            isPhoneVerified = false,
            isCedulaVerified = false
        )
    }
    
    /**
     * Reinicia el flujo de verificación
     */
    fun resetVerification() {
        _verificationData.value = VerificationData()
        _cedulaError.value = null
        _phoneError.value = null
        _otpError.value = null
        _verificationSuccess.value = false
    }
    
    /**
     * Valida cédula en tiempo real
     */
    private fun validateCedula(cedula: String) {
        _cedulaError.value = if (cedula.isEmpty()) null 
            else if (!CedulaValidator.isValid(cedula)) "Cédula inválida (8 dígitos)"
            else null
    }
    
    /**
     * Valida teléfono en tiempo real
     */
    private fun validatePhone(phone: String) {
        _phoneError.value = if (phone.isEmpty()) null
            else if (!PhoneValidator.isValidVenezuelanPhone(phone)) "Teléfono inválido"
            else null
    }
}
