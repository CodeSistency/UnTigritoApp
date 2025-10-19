package com.thecodefather.untigrito.presentation.screens.account

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecodefather.untigrito.auth.domain.repository.IAuthRepository
import com.thecodefather.untigrito.data.datasource.remote.SupabaseDatabaseService
import com.thecodefather.untigrito.data.datasource.remote.SupabasePayment
import com.thecodefather.untigrito.data.datasource.remote.SupabaseUser
import com.thecodefather.untigrito.presentation.screens.account.data.Transaction
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for Account Details Screen
 * 
 * Uses direct Supabase queries following the pattern from LoginViewModel
 * No repository layer - direct ViewModel → Supabase communication
 */
@HiltViewModel
class AccountDetailsViewModel @Inject constructor(
    private val supabaseDatabase: SupabaseDatabaseService,
    private val postgrest: Postgrest,
    private val authRepository: IAuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AccountDetailsUiState())
    val uiState: StateFlow<AccountDetailsUiState> = _uiState.asStateFlow()

    init {
        loadAccountDetails()
    }

    /**
     * Load account details including balance and transaction history
     * Performs direct Supabase queries
     */
    fun loadAccountDetails() {
        Timber.d("💰 ACCOUNT VIEWMODEL LOAD_DETAILS_START")
        Log.e("TAG", "💰 ACCOUNT VIEWMODEL LOAD_DETAILS_START")
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                val currentUser = authRepository.getCurrentUser()
                
                if (currentUser == null) {
                    Timber.w("⚠️ ACCOUNT - No authenticated user found")
                    Log.e("TAG", "⚠️ ACCOUNT - No authenticated user found")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Usuario no autenticado"
                    )
                    return@launch
                }
                
                Timber.d("📊 ACCOUNT - Loading data for user: ${currentUser.id}")
                Log.e("TAG", "📊 ACCOUNT - Loading data for user: ${currentUser.id}")
                
                // 1. Obtener balance del usuario directamente desde Supabase
                val userResult = supabaseDatabase.getById<SupabaseUser>("User", currentUser.id)
                
                userResult.onSuccess { supabaseUser ->
                    if (supabaseUser != null) {
                        val balance = supabaseUser.balance
                        Timber.d("✅ ACCOUNT - Balance loaded: $balance")
                        Log.e("TAG", "✅ ACCOUNT - Balance loaded: $balance")
                        
                        _uiState.value = _uiState.value.copy(
                            balance = balance,
                            isLoading = false
                        )
                        
                        // 2. Cargar historial de transacciones
                        loadTransactionHistory(currentUser.id)
                    } else {
                        Timber.w("⚠️ ACCOUNT - User not found in database")
                        Log.e("TAG", "⚠️ ACCOUNT - User not found in database")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Usuario no encontrado en la base de datos"
                        )
                    }
                }.onFailure { error ->
                    Timber.e(error, "❌ ACCOUNT - Error loading balance")
                    Log.e("TAG", "❌ ACCOUNT - Error loading balance: ${error.message}", error)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Error al cargar el balance"
                    )
                }
                
            } catch (exception: Exception) {
                Timber.e(exception, "❌ ACCOUNT - Unexpected error")
                Log.e("TAG", "❌ ACCOUNT - Unexpected error: ${exception.message}", exception)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = exception.message ?: "Error inesperado al cargar datos"
                )
            }
        }
    }

    /**
     * Load transaction history from Supabase Payment table
     * Uses direct Postgrest query with filters and ordering
     */
    private fun loadTransactionHistory(userId: String) {
        Timber.d("📜 ACCOUNT - Loading transaction history for user: $userId")
        Log.e("TAG", "📜 ACCOUNT - Loading transaction history for user: $userId")
        
        viewModelScope.launch {
            try {
                // Consulta directa a la tabla Payment con filtros
                val payments = postgrest.from("Payment")
                    .select {
                        filter { eq("userId", userId) }
                        order("createdAt", Order.DESCENDING)
                    }
                    .decodeList<SupabasePayment>()
                
                Timber.d("✅ ACCOUNT - Transactions loaded: ${payments.size}")
                Log.e("TAG", "✅ ACCOUNT - Transactions loaded: ${payments.size}")
                
                // Convertir SupabasePayment a Transaction del dominio
                val transactions = payments.map { payment ->
                    Transaction(
                        id = payment.id,
                        type = mapPaymentMethodToType(payment.method),
                        description = payment.details ?: "",
                        date = formatDate(payment.createdAt),
                        amount = if (payment.status == "COMPLETED") payment.amount else -payment.amount
                    )
                }
                
                _uiState.value = _uiState.value.copy(
                    transactions = transactions
                )
                
            } catch (exception: Exception) {
                Timber.e(exception, "❌ ACCOUNT - Error loading transactions")
                Log.e("TAG", "❌ ACCOUNT - Error loading transactions: ${exception.message}", exception)
                _uiState.value = _uiState.value.copy(
                    errorMessage = exception.message ?: "Error al cargar el historial de transacciones"
                )
            }
        }
    }

    /**
     * Refresh account details
     * Called when user pulls to refresh
     */
    fun refreshAccountDetails() {
        Timber.d("🔄 ACCOUNT - Refreshing account details")
        Log.e("TAG", "🔄 ACCOUNT - Refreshing account details")
        loadAccountDetails()
    }

    /**
     * Map payment method to readable transaction type
     */
    private fun mapPaymentMethodToType(method: String): String {
        return when (method) {
            "CASHEA" -> "Recarga Cashea"
            "BALANCE" -> "Pago con Balance"
            "TRANSFER" -> "Transferencia"
            "PAY_MOBILE" -> "Pago Móvil"
            "CARD" -> "Tarjeta"
            "OTHER" -> "Otro"
            else -> method
        }
    }

    /**
     * Format date string from ISO format to readable format
     */
    private fun formatDate(isoDate: String?): String {
        if (isoDate == null) return ""
        
        // Simple format - extract date part from ISO format
        // Example: "2025-10-19T12:30:00.000Z" -> "19/10/2025"
        return try {
            val datePart = isoDate.substringBefore('T')
            val parts = datePart.split('-')
            if (parts.size == 3) {
                "${parts[2]}/${parts[1]}/${parts[0]}"
            } else {
                isoDate
            }
        } catch (e: Exception) {
            isoDate
        }
    }
}
