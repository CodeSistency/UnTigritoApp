package com.thecodefather.untigrito.presentation.screens.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecodefather.untigrito.auth.domain.model.AuthState
import com.thecodefather.untigrito.domain.model.User
import com.thecodefather.untigrito.auth.domain.repository.IAuthRepository
import com.thecodefather.untigrito.auth.domain.usecase.AuthStateManager
import com.thecodefather.untigrito.core.validation.EmailValidator
import com.thecodefather.untigrito.core.validation.PasswordValidator
import com.thecodefather.untigrito.core.validation.PhoneValidator
import com.thecodefather.untigrito.data.datasource.local.GoogleSignInData
import com.thecodefather.untigrito.data.datasource.local.GoogleSignInHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for authentication flows (login, register, forgot password, OTP, etc.)
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: IAuthRepository,
    private val googleSignInHelper: GoogleSignInHelper,
    private val authStateManager: AuthStateManager
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _emailError = MutableStateFlow<String?>(null)
    val emailError: StateFlow<String?> = _emailError.asStateFlow()

    private val _phoneError = MutableStateFlow<String?>(null)
    val phoneError: StateFlow<String?> = _phoneError.asStateFlow()

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError: StateFlow<String?> = _passwordError.asStateFlow()

    private val _otpError = MutableStateFlow<String?>(null)
    val otpError: StateFlow<String?> = _otpError.asStateFlow()

    private val _cedulaError = MutableStateFlow<String?>(null)
    val cedulaError: StateFlow<String?> = _cedulaError.asStateFlow()

    // ========== Authentication Methods ==========

    /**
     * Performs login with email/phone and password
     */
    fun login(identifier: String, password: String) {
        Timber.d("🔐 AUTH VIEWMODEL LOGIN_START - Identifier: $identifier")

        // Determine if identifier is email or phone
        val email = if (EmailValidator.isValid(identifier)) identifier else null
        val phone = if (email == null && PhoneValidator.isValidVenezuelanPhone(identifier)) identifier else null

        // Validate data
        if (!validateLoginData(identifier, password)) {
            Timber.w("⚠️ AUTH VIEWMODEL LOGIN_VALIDATION_FAILED")
            return
        }

        _authState.value = AuthState.Loading

        viewModelScope.launch {
            val result = authRepository.login(email, phone, password)

            result.onSuccess { user ->
                Timber.d("✅ AUTH VIEWMODEL LOGIN_SUCCESS - User: ${user.id}")
                _authState.value = AuthState.Authenticated(user)
                // Persist authentication state
                viewModelScope.launch {
                    authStateManager.updateAuthState(AuthState.Authenticated(user), user)
                }
            }.onFailure { exception ->
                Timber.w("⚠️ AUTH VIEWMODEL LOGIN_FAILED - ${exception.message}")
                _authState.value = AuthState.Error(exception.message ?: "Login failed")
            }
        }
    }

    /**
     * Performs user registration
     */
    fun register(
        name: String,
        identifier: String, // Can be email or phone
        password: String,
        confirmPassword: String
    ) {
        Timber.d("🔐 AUTH VIEWMODEL REGISTER_START - Name: $name, Identifier: $identifier")

        // Determine if identifier is email or phone
        val email = if (EmailValidator.isValid(identifier)) identifier else null
        val phone = if (email == null && PhoneValidator.isValidVenezuelanPhone(identifier)) identifier else null

        // Validate data
        if (!validateRegisterData(name, identifier, password, confirmPassword)) {
            Timber.w("⚠️ AUTH VIEWMODEL REGISTER_VALIDATION_FAILED")
            return
        }

        _authState.value = AuthState.Loading

        viewModelScope.launch {
            val result = authRepository.register(name, email, phone, password)

            result.onSuccess { user ->
                Timber.d("✅ AUTH VIEWMODEL REGISTER_SUCCESS - User: ${user.id}")
                _authState.value = AuthState.Authenticated(user)
            }.onFailure { exception ->
                Timber.w("⚠️ AUTH VIEWMODEL REGISTER_FAILED - ${exception.message}")
                _authState.value = AuthState.Error(exception.message ?: "Registration failed")
            }
        }
    }

    /**
     * Requests password reset
     */
    fun forgotPassword(email: String) {
        Timber.d("🔐 AUTH VIEWMODEL FORGOT_PASSWORD_START - Email: $email")

        if (!EmailValidator.isValid(email)) {
            _emailError.value = "Invalid email format"
            Timber.w("⚠️ AUTH VIEWMODEL FORGOT_PASSWORD_INVALID_EMAIL")
            return
        }

        _authState.value = AuthState.Loading

        viewModelScope.launch {
            val result = authRepository.forgotPassword(email)

            result.onSuccess {
                Timber.d("✅ AUTH VIEWMODEL FORGOT_PASSWORD_SUCCESS")
                _authState.value = AuthState.Unauthenticated
            }.onFailure { exception ->
                Timber.w("⚠️ AUTH VIEWMODEL FORGOT_PASSWORD_FAILED - ${exception.message}")
                _authState.value = AuthState.Error(exception.message ?: "Forgot password failed")
            }
        }
    }

    /**
     * Resets password with token
     */
    fun resetPassword(token: String, newPassword: String) {
        Timber.d("🔐 AUTH VIEWMODEL RESET_PASSWORD_START")

        if (!PasswordValidator.isValid(newPassword)) {
            _passwordError.value = "Password must be at least 6 characters"
            return
        }

        _authState.value = AuthState.Loading

        viewModelScope.launch {
            val result = authRepository.resetPassword(token, newPassword)

            result.onSuccess {
                Timber.d("✅ AUTH VIEWMODEL RESET_PASSWORD_SUCCESS")
                _authState.value = AuthState.Unauthenticated
            }.onFailure { exception ->
                Timber.w("⚠️ AUTH VIEWMODEL RESET_PASSWORD_FAILED - ${exception.message}")
                _authState.value = AuthState.Error(exception.message ?: "Reset password failed")
            }
        }
    }

    /**
     * Verifies email with token
     */
    fun verifyEmail(token: String) {
        Timber.d("🔐 AUTH VIEWMODEL VERIFY_EMAIL_START")

        _authState.value = AuthState.Loading

        viewModelScope.launch {
            val result = authRepository.verifyEmail(token)

            result.onSuccess {
                Timber.d("✅ AUTH VIEWMODEL VERIFY_EMAIL_SUCCESS")
                _authState.value = AuthState.Unauthenticated
            }.onFailure { exception ->
                Timber.w("⚠️ AUTH VIEWMODEL VERIFY_EMAIL_FAILED - ${exception.message}")
                _authState.value = AuthState.Error(exception.message ?: "Email verification failed")
            }
        }
    }

    /**
     * Sends OTP to phone number
     */
    fun sendOtp(phoneNumber: String) {
        Timber.d("🔐 AUTH VIEWMODEL SEND_OTP_START - Phone: $phoneNumber")

        if (!PhoneValidator.isValidVenezuelanPhone(phoneNumber)) {
            _phoneError.value = "Invalid phone number format"
            Timber.w("⚠️ AUTH VIEWMODEL SEND_OTP_INVALID_PHONE")
            return
        }

        _authState.value = AuthState.Loading

        viewModelScope.launch {
            val result = authRepository.sendOtp(phoneNumber)

            result.onSuccess { message ->
                Timber.d("✅ AUTH VIEWMODEL SEND_OTP_SUCCESS")
                _authState.value = AuthState.OtpSent(message)
            }.onFailure { exception ->
                Timber.w("⚠️ AUTH VIEWMODEL SEND_OTP_FAILED - ${exception.message}")
                _authState.value = AuthState.Error(exception.message ?: "Send OTP failed")
            }
        }
    }

    /**
     * Verifies OTP code
     */
    fun verifyOtp(phoneNumber: String, otpCode: String) {
        Timber.d("🔐 AUTH VIEWMODEL VERIFY_OTP_START - Phone: $phoneNumber")

        if (otpCode.length != 5) {
            _otpError.value = "OTP must be 5 digits"
            return
        }

        _authState.value = AuthState.Loading

        viewModelScope.launch {
            val result = authRepository.verifyOtp(phoneNumber, otpCode)

            result.onSuccess { verified ->
                if (verified) {
                    Timber.d("✅ AUTH VIEWMODEL VERIFY_OTP_SUCCESS")
                    _authState.value = AuthState.OtpVerified
                } else {
                    Timber.w("⚠️ AUTH VIEWMODEL VERIFY_OTP_FAILED - Not verified")
                    _authState.value = AuthState.Error("OTP verification failed")
                }
            }.onFailure { exception ->
                Timber.w("⚠️ AUTH VIEWMODEL VERIFY_OTP_ERROR - ${exception.message}")
                _authState.value = AuthState.Error(exception.message ?: "OTP verification error")
            }
        }
    }

    /**
     * Verifies ID with cedula and images
     */
    fun verifyId(cedula: String, cedulaImage: String, faceScanData: String) {
        Timber.d("🔐 AUTH VIEWMODEL VERIFY_ID_START - Cedula: $cedula")

        // Validate cedula format (7-8 digits)
        if (!cedula.matches(Regex("^\\d{7,8}$"))) {
            _cedulaError.value = "Cedula must be 7-8 digits"
            return
        }

        _authState.value = AuthState.Loading

        viewModelScope.launch {
            val result = authRepository.verifyId(cedula, cedulaImage, faceScanData)

            result.onSuccess { verified ->
                if (verified) {
                    Timber.d("✅ AUTH VIEWMODEL VERIFY_ID_SUCCESS")
                    _authState.value = AuthState.IdVerified
                } else {
                    Timber.w("⚠️ AUTH VIEWMODEL VERIFY_ID_FAILED - Not verified")
                    _authState.value = AuthState.Error("ID verification failed")
                }
            }.onFailure { exception ->
                Timber.w("⚠️ AUTH VIEWMODEL VERIFY_ID_ERROR - ${exception.message}")
                _authState.value = AuthState.Error(exception.message ?: "ID verification error")
            }
        }
    }

    /**
     * Performs Google authentication
     */
    fun googleAuth(token: String, idToken: String? = null) {
        Timber.d("🔐 AUTH VIEWMODEL GOOGLE_AUTH_START")

        _authState.value = AuthState.Loading

        viewModelScope.launch {
            val result = authRepository.googleAuth(token, idToken)

            result.onSuccess { user ->
                Timber.d("✅ AUTH VIEWMODEL GOOGLE_AUTH_SUCCESS - User: ${user.id}")
                _authState.value = AuthState.Authenticated(user)
            }.onFailure { exception ->
                Timber.w("⚠️ AUTH VIEWMODEL GOOGLE_AUTH_FAILED - ${exception.message}")
                _authState.value = AuthState.Error(exception.message ?: "Google authentication failed")
            }
        }
    }

    /**
     * Refreshes authentication token
     */
    fun refreshToken() {
        Timber.d("🔐 AUTH VIEWMODEL REFRESH_TOKEN_START")

        viewModelScope.launch {
            val result = authRepository.refreshToken()

            result.onSuccess { newToken ->
                Timber.d("✅ AUTH VIEWMODEL REFRESH_TOKEN_SUCCESS")
                // Token refresh successful, auth state should remain authenticated
            }.onFailure { exception ->
                Timber.w("⚠️ AUTH VIEWMODEL REFRESH_TOKEN_FAILED - ${exception.message}")
                _authState.value = AuthState.Unauthenticated
            }
        }
    }

    /**
     * Performs logout
     */
    fun logout() {
        Timber.d("🔐 AUTH VIEWMODEL LOGOUT")
        authRepository.logout()
        _authState.value = AuthState.Unauthenticated
        clearErrors()
    }

    /**
     * Checks if user is authenticated
     */
    fun isAuthenticated(): Boolean {
        return authRepository.isAuthenticated()
    }

    /**
     * Checks if token should be refreshed
     */
    fun shouldRefreshToken(): Boolean {
        return authRepository.shouldRefreshToken()
    }

    // ========== Validation Methods ==========

    /**
     * Validates login data
     */
    private fun validateLoginData(identifier: String, password: String): Boolean {
        var isValid = true

        // Validate identifier (email or phone)
        if (!EmailValidator.isValid(identifier) && !PhoneValidator.isValidVenezuelanPhone(identifier)) {
            _emailError.value = "Enter a valid email or phone number"
            _phoneError.value = "Enter a valid email or phone number"
            isValid = false
        } else {
            _emailError.value = null
            _phoneError.value = null
        }

        // Validate password
        if (!PasswordValidator.isValid(password)) {
            _passwordError.value = "Password must be at least 6 characters"
            isValid = false
        } else {
            _passwordError.value = null
        }

        return isValid
    }

    /**
     * Validates registration data
     */
    private fun validateRegisterData(
        name: String,
        identifier: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        var isValid = true

        // Validate name
        if (name.isBlank()) {
            isValid = false
        }

        // Validate identifier (email or phone)
        if (!EmailValidator.isValid(identifier) && !PhoneValidator.isValidVenezuelanPhone(identifier)) {
            _emailError.value = "Enter a valid email or phone number"
            _phoneError.value = "Enter a valid email or phone number"
            isValid = false
        } else {
            _emailError.value = null
            _phoneError.value = null
        }

        // Validate passwords
        if (!PasswordValidator.doPasswordsMatch(password, confirmPassword)) {
            _passwordError.value = "Passwords do not match"
            isValid = false
        } else if (!PasswordValidator.isValid(password)) {
            _passwordError.value = "Password must be at least 6 characters"
            isValid = false
        } else {
            _passwordError.value = null
        }

        return isValid
    }

    /**
     * Clears all errors
     */
    fun clearErrors() {
        _emailError.value = null
        _phoneError.value = null
        _passwordError.value = null
        _otpError.value = null
        _cedulaError.value = null
    }

    // ========== Real-time Validation Methods ==========

    /**
     * Validates email in real-time
     */
    fun validateEmail(email: String) {
        _emailError.value = if (EmailValidator.isValid(email)) null else "Invalid email format"
    }

    /**
     * Validates phone in real-time
     */
    fun validatePhone(phone: String) {
        _phoneError.value = if (PhoneValidator.isValidVenezuelanPhone(phone)) null else "Invalid phone format (0412345678)"
    }

    /**
     * Validates password in real-time
     */
    fun validatePassword(password: String) {
        _passwordError.value = if (PasswordValidator.isValid(password)) null else "At least 6 characters required"
    }

    /**
     * Validates OTP in real-time
     */
    fun validateOtp(otp: String) {
        _otpError.value = if (otp.length == 5 && otp.all { it.isDigit() }) null else "OTP must be 5 digits"
    }

    /**
     * Validates cedula in real-time
     */
    fun validateCedula(cedula: String) {
        _cedulaError.value = if (cedula.matches(Regex("^\\d{7,8}$"))) null else "Cedula must be 7-8 digits"
    }

    // ========== Google Sign-In Methods ==========

    /**
     * Get Google Sign-In intent for starting OAuth flow
     */
    fun getGoogleSignInIntent() = googleSignInHelper.getSignInIntent()

    /**
     * Handle Google Sign-In result
     */
    fun handleGoogleSignInResult(data: android.content.Intent?) {
        Timber.d("🔐 AUTH VIEWMODEL GOOGLE SIGN-IN RESULT HANDLING")

        val result = googleSignInHelper.handleSignInResult(data)

        result.onSuccess { googleData ->
            // Perform authentication with backend using Google tokens
            performGoogleAuth(googleData)
        }.onFailure { exception ->
            Timber.w("⚠️ AUTH VIEWMODEL GOOGLE SIGN-IN FAILED - ${exception.message}")
            _authState.value = AuthState.Error(exception.message ?: "Google Sign-In failed")
        }
    }

    /**
     * Perform authentication with backend using Google data
     */
    private fun performGoogleAuth(googleData: GoogleSignInData) {
        Timber.d("🔐 AUTH VIEWMODEL GOOGLE AUTH START")

        _authState.value = AuthState.Loading

        viewModelScope.launch {
            val result = authRepository.googleAuth(
                token = googleData.idToken ?: "",
                idToken = googleData.idToken
            )

            result.onSuccess { user ->
                Timber.d("✅ AUTH VIEWMODEL GOOGLE AUTH SUCCESS - User: ${user.id}")
                _authState.value = AuthState.Authenticated(user)
            }.onFailure { exception ->
                Timber.w("⚠️ AUTH VIEWMODEL GOOGLE AUTH FAILED - ${exception.message}")
                _authState.value = AuthState.Error(exception.message ?: "Google authentication failed")
            }
        }
    }

    /**
     * Sign out from Google
     */
    fun googleSignOut() {
        googleSignInHelper.signOut()
    }
}
