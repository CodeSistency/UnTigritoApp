package com.thecodefather.untigrito.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecodefather.untigrito.auth.domain.usecase.AuthStateManager
import com.thecodefather.untigrito.data.datasource.remote.SupabaseDatabaseService
import com.thecodefather.untigrito.data.datasource.remote.SupabasePayment
import com.thecodefather.untigrito.data.datasource.remote.SupabaseUser
import com.thecodefather.untigrito.presentation.screens.account.RechargeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for Recharge operations
 * 
 * Uses direct Supabase queries following the pattern from LoginViewModel
 */
@HiltViewModel
class RechargeViewModel @Inject constructor(
    private val supabaseDatabase: SupabaseDatabaseService,
    private val postgrest: Postgrest,
    private val authStateManager: AuthStateManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(RechargeUiState())
    val uiState: StateFlow<RechargeUiState> = _uiState.asStateFlow()

    /**
     * Process recharge request
     */
    fun processRecharge(
        amount: Double,
        method: String,
        phone: String? = null,
        cedula: String? = null,
        bank: String? = null
    ) {
        Timber.d("💰 RECHARGE VIEWMODEL PROCESS_RECHARGE_START - Amount: $amount")
        Log.e("TAG", "💰 RECHARGE VIEWMODEL PROCESS_RECHARGE_START - Amount: $amount")
        
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
                
                // 1. Obtener usuario actual
                val userResult = supabaseDatabase.getById<SupabaseUser>("User", currentUserId)
                
                userResult.onSuccess { user ->
                    if (user != null) {
                        // 2. Crear registro de pago (recarga)
                        val payment = SupabasePayment(
                            id = "", // Se genera automáticamente
                            userId = currentUserId,
                            amount = amount,
                            fee = 0.0,
                            method = method,
                            status = "COMPLETED", // Asumimos que la recarga es exitosa
                            details = buildPaymentDetails(phone, cedula, bank),
                            createdAt = java.time.Instant.now().toString()
                        )
                        
                        val paymentResult = supabaseDatabase.insert<SupabasePayment>("Payment", payment)
                        
                        paymentResult.onSuccess { insertedPayment ->
                            if (insertedPayment != null) {
                                // 3. Actualizar balance del usuario
                                val updatedUser = user.copy(balance = user.balance + amount)
                                val updateResult = supabaseDatabase.update("User", currentUserId, updatedUser)
                                
                                updateResult.onSuccess {
                                    _uiState.value = _uiState.value.copy(
                                        isLoading = false,
                                        rechargeSuccess = true,
                                        errorMessage = null
                                    )
                                    
                                    Timber.d("✅ RECHARGE - Recharge processed successfully")
                                    Log.e("TAG", "✅ RECHARGE - Recharge processed successfully")
                                }.onFailure { exception ->
                                    _uiState.value = _uiState.value.copy(
                                        isLoading = false,
                                        errorMessage = "Error actualizando balance: ${exception.message}"
                                    )
                                }
                            } else {
                                _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    errorMessage = "Error procesando recarga"
                                )
                            }
                        }.onFailure { exception ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                errorMessage = "Error creando recarga: ${exception.message}"
                            )
                        }
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Usuario no encontrado"
                        )
                    }
                }.onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Error verificando usuario: ${exception.message}"
                    )
                }
                
            } catch (e: Exception) {
                Timber.e(e, "❌ RECHARGE - Error processing recharge")
                Log.e("TAG", "❌ RECHARGE - Error processing recharge: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error procesando recarga: ${e.message}"
                )
            }
        }
    }

    /**
     * Build payment details JSON string
     */
    private fun buildPaymentDetails(phone: String?, cedula: String?, bank: String?): String {
        val details = mutableMapOf<String, String?>()
        phone?.let { details["phone"] = it }
        cedula?.let { details["cedula"] = it }
        bank?.let { details["bank"] = it }
        
        return details.entries.joinToString(",") { "${it.key}=${it.value}" }
    }

    /**
     * Reset recharge success state
     */
    fun resetRechargeSuccess() {
        _uiState.value = _uiState.value.copy(rechargeSuccess = false)
    }
}
