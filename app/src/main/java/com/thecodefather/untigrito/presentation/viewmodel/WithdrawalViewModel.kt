package com.thecodefather.untigrito.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecodefather.untigrito.auth.domain.usecase.AuthStateManager
import com.thecodefather.untigrito.data.datasource.remote.SupabaseDatabaseService
import com.thecodefather.untigrito.data.datasource.remote.SupabaseUser
import com.thecodefather.untigrito.data.datasource.remote.SupabaseUserPaymentMethod
import com.thecodefather.untigrito.data.datasource.remote.SupabaseWithdrawal
import com.thecodefather.untigrito.presentation.screens.account.WithdrawalUiState
import com.thecodefather.untigrito.presentation.screens.account.PaymentMethod
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for Withdrawal operations
 * 
 * Uses direct Supabase queries following the pattern from LoginViewModel
 */
@HiltViewModel
class WithdrawalViewModel @Inject constructor(
    private val supabaseDatabase: SupabaseDatabaseService,
    private val postgrest: Postgrest,
    private val authStateManager: AuthStateManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(WithdrawalUiState())
    val uiState: StateFlow<WithdrawalUiState> = _uiState.asStateFlow()

    init {
        loadPaymentMethods()
    }

    /**
     * Load user payment methods
     */
    fun loadPaymentMethods() {
        Timber.d("💳 WITHDRAWAL VIEWMODEL LOAD_PAYMENT_METHODS_START")
        Log.e("TAG", "💳 WITHDRAWAL VIEWMODEL LOAD_PAYMENT_METHODS_START")
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                val currentUserId = authStateManager.getCurrentUserId()
                
                if (currentUserId == null) {
                    Timber.w("⚠️ WITHDRAWAL - No authenticated user found")
                    Log.e("TAG", "⚠️ WITHDRAWAL - No authenticated user found")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Usuario no autenticado"
                    )
                    return@launch
                }
                
                // Cargar métodos de pago del usuario
                val paymentMethods = postgrest.from("UserPaymentMethod")
                    .select {
                        filter {
                            eq("userId", currentUserId)
                        }
                    }
                    .decodeList<SupabaseUserPaymentMethod>()
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    paymentMethods = paymentMethods.map { supabaseMethod ->
                        PaymentMethod(
                            id = supabaseMethod.id,
                            method = supabaseMethod.method,
                            accountNumber = supabaseMethod.accountNumber,
                            accountAlias = supabaseMethod.accountAlias,
                            phoneNumber = supabaseMethod.phoneNumber,
                            isVerified = supabaseMethod.isVerified,
                            isDefault = supabaseMethod.isDefault
                        )
                    },
                    errorMessage = null
                )
                
                Timber.d("✅ WITHDRAWAL - Payment methods loaded: ${paymentMethods.size}")
                Log.e("TAG", "✅ WITHDRAWAL - Payment methods loaded: ${paymentMethods.size}")
                
            } catch (e: Exception) {
                Timber.e(e, "❌ WITHDRAWAL - Error loading payment methods")
                Log.e("TAG", "❌ WITHDRAWAL - Error loading payment methods: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error cargando métodos de pago: ${e.message}"
                )
            }
        }
    }

    /**
     * Process withdrawal request
     */
    fun processWithdrawal(
        amount: Double,
        method: String,
        phone: String,
        cedula: String,
        bank: String? = null
    ) {
        Timber.d("💰 WITHDRAWAL VIEWMODEL PROCESS_WITHDRAWAL_START - Amount: $amount")
        Log.e("TAG", "💰 WITHDRAWAL VIEWMODEL PROCESS_WITHDRAWAL_START - Amount: $amount")
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                val currentUserId = authStateManager.getCurrentUserId()
                
                if (currentUserId == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Usuario no autenticado"
                    )
                    return@launch
                }
                
                // 1. Verificar balance suficiente
                val userResult = supabaseDatabase.getById<SupabaseUser>("User", currentUserId)
                
                userResult.onSuccess { user ->
                    if (user != null && user.balance >= amount) {
                        // 2. Crear registro de retiro
                        val withdrawal = SupabaseWithdrawal(
                            id = "", // Se genera automáticamente
                            userId = currentUserId,
                            paymentMethodId = method,
                            amount = amount,
                            status = "PENDING",
                            requestedAt = java.time.Instant.now().toString()
                        )
                        
                        val withdrawalResult = supabaseDatabase.insert<SupabaseWithdrawal>("Withdrawal", withdrawal)
                        
                        withdrawalResult.onSuccess { insertedWithdrawal ->
                            if (insertedWithdrawal != null) {
                                // 3. Actualizar balance del usuario
                                val updatedUser = user.copy(balance = user.balance - amount)
                                val updateResult = supabaseDatabase.update("User", currentUserId, updatedUser)
                                
                                updateResult.onSuccess {
                                    _uiState.value = _uiState.value.copy(
                                        isLoading = false,
                                        withdrawalSuccess = true,
                                        errorMessage = null
                                    )
                                    
                                    Timber.d("✅ WITHDRAWAL - Withdrawal processed successfully")
                                    Log.e("TAG", "✅ WITHDRAWAL - Withdrawal processed successfully")
                                }.onFailure { exception ->
                                    _uiState.value = _uiState.value.copy(
                                        isLoading = false,
                                        errorMessage = "Error actualizando balance: ${exception.message}"
                                    )
                                }
                            } else {
                                _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    errorMessage = "Error procesando retiro"
                                )
                            }
                        }.onFailure { exception ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                errorMessage = "Error creando retiro: ${exception.message}"
                            )
                        }
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Balance insuficiente"
                        )
                    }
                }.onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Error verificando balance: ${exception.message}"
                    )
                }
                
            } catch (e: Exception) {
                Timber.e(e, "❌ WITHDRAWAL - Error processing withdrawal")
                Log.e("TAG", "❌ WITHDRAWAL - Error processing withdrawal: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error procesando retiro: ${e.message}"
                )
            }
        }
    }

    /**
     * Reset withdrawal success state
     */
    fun resetWithdrawalSuccess() {
        _uiState.value = _uiState.value.copy(withdrawalSuccess = false)
    }
}

/**
 * Extension function to convert SupabaseUserPaymentMethod to PaymentMethod
 */
private fun SupabaseUserPaymentMethod.toPaymentMethod(): PaymentMethod {
    return PaymentMethod(
        id = this.id,
        method = this.method,
        accountNumber = this.accountNumber,
        accountAlias = this.accountAlias,
        phoneNumber = this.phoneNumber,
        isVerified = this.isVerified,
        isDefault = this.isDefault
    )
}

/**
 * PaymentMethod data class
 */
data class PaymentMethod(
    val id: String,
    val method: String,
    val accountNumber: String?,
    val accountAlias: String?,
    val phoneNumber: String?,
    val isVerified: Boolean,
    val isDefault: Boolean
)
