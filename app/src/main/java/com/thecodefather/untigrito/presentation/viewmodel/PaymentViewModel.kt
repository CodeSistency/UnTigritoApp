package com.thecodefather.untigrito.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecodefather.untigrito.auth.domain.usecase.AuthStateManager
import com.thecodefather.untigrito.data.datasource.remote.SupabaseDatabaseService
import com.thecodefather.untigrito.data.datasource.remote.SupabasePayment
import com.thecodefather.untigrito.data.datasource.remote.SupabaseUser
import com.thecodefather.untigrito.data.repository.ClientRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.thecodefather.untigrito.domain.model.ClientUser
import com.thecodefather.untigrito.domain.model.Transaction
import com.thecodefather.untigrito.domain.model.toTransaction
import timber.log.Timber
import java.time.Instant
import java.util.Date
import java.util.UUID

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val repository: ClientRepositoryImpl,
    private val supabaseDatabaseService: SupabaseDatabaseService,
    private val authStateManager: AuthStateManager
) : ViewModel() {

    private val _user = MutableStateFlow<ClientUser?>(null)
    val user = _user.asStateFlow()

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions = _transactions.asStateFlow()

    private val _totalRecharged = MutableStateFlow(0.0)
    val totalRecharged = _totalRecharged.asStateFlow()

    private val _totalWithdrawn = MutableStateFlow(0.0)
    val totalWithdrawn = _totalWithdrawn.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    init {
        loadPaymentData()
    }

    private fun loadPaymentData() {
        _loading.value = true
        viewModelScope.launch {
            try {
                val currentUserId = authStateManager.getCurrentUserId()
                if (currentUserId == null) {
                    _error.value = "Usuario no autenticado"
                    _loading.value = false
                    return@launch
                }
                
                // Load user from Supabase
                loadUserFromSupabase(currentUserId)

                // Load transactions from Supabase
                loadTransactionsFromSupabase(currentUserId)

                // Load totals from Supabase
                loadTotalsFromSupabase(currentUserId)
            } catch (e: Exception) {
                Timber.e(e, "Error loading payment data")
                _error.value = "Error loading payment data: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    // Cargar usuario desde Supabase
    private suspend fun loadUserFromSupabase(userId: String) {
        try {
            val result = supabaseDatabaseService.getById<SupabaseUser>("User", userId)
            
            result.onSuccess { supabaseUser ->
                if (supabaseUser != null) {
                    _user.value = supabaseUser.toClientUser()
                }
            }.onFailure { exception ->
                Timber.e(exception, "Error loading user from Supabase")
                _error.value = "Error loading user: ${exception.message}"
            }
        } catch (e: Exception) {
            Timber.e(e, "Exception loading user")
            _error.value = "Error loading user: ${e.message}"
        }
    }

    // Cargar transacciones desde Supabase
    private suspend fun loadTransactionsFromSupabase(userId: String) {
        try {
            val result = supabaseDatabaseService.findBy<SupabasePayment>("Payment", "userId", userId)
            
            result.onSuccess { payments ->
                val transactions = payments.map { payment ->
                    payment.toTransaction()
                }.sortedByDescending { it.createdAt }
                
                _transactions.value = transactions
            }.onFailure { exception ->
                Timber.e(exception, "Error loading transactions from Supabase")
                _error.value = "Error loading transactions: ${exception.message}"
            }
        } catch (e: Exception) {
            Timber.e(e, "Exception loading transactions")
            _error.value = "Error loading transactions: ${e.message}"
        }
    }

    // Cargar totales desde Supabase
    private suspend fun loadTotalsFromSupabase(userId: String) {
        try {
            val result = supabaseDatabaseService.findBy<SupabasePayment>("Payment", "userId", userId)
            
            result.onSuccess { payments ->
                val totalRecharged = payments
                    .filter { it.method == "BALANCE" && it.status == "COMPLETED" }
                    .sumOf { it.amount }
                
                val totalWithdrawn = payments
                    .filter { it.method == "TRANSFER" && it.status == "COMPLETED" }
                    .sumOf { it.amount }
                
                _totalRecharged.value = totalRecharged
                _totalWithdrawn.value = totalWithdrawn
            }.onFailure { exception ->
                Timber.e(exception, "Error loading totals from Supabase")
                _error.value = "Error loading totals: ${exception.message}"
            }
        } catch (e: Exception) {
            Timber.e(e, "Exception loading totals")
            _error.value = "Error loading totals: ${e.message}"
        }
    }

    fun addTransaction(amount: Double, type: String, description: String) {
        viewModelScope.launch {
            try {
                val currentUserId = authStateManager.getCurrentUserId()
                if (currentUserId == null) {
                    _error.value = "Usuario no autenticado"
                    return@launch
                }
                
                val newId = UUID.randomUUID().toString()
                val currentTimestamp = Instant.now().toString()
                
                // Create payment in Supabase
                val supabasePayment = SupabasePayment(
                    id = newId,
                    userId = currentUserId,
                    amount = amount,
                    method = type,
                    status = "PENDING",
                    details = description,
                    createdAt = currentTimestamp
                )
                
                supabaseDatabaseService.insert("payments", supabasePayment)
                    .onSuccess {
                        Timber.d("Payment created successfully with id: $newId")
                        loadPaymentData()
                    }
                    .onFailure { exception ->
                        Timber.e(exception, "Error creating payment")
                        _error.value = "Error adding transaction: ${exception.message}"
                    }
            } catch (e: Exception) {
                Timber.e(e, "Exception adding transaction")
                _error.value = "Error adding transaction: ${e.message}"
            }
        }
    }

    fun getBalance(): Double {
        return _user.value?.balance ?: 0.0
    }
}

// Extension functions para convertir modelos de Supabase a modelos de dominio
private fun SupabaseUser.toClientUser(): ClientUser {
    return ClientUser(
        id = this.id.orEmpty(),
        name = this.name ?: "",
        email = this.email ?: "",
        phone = this.phone ?: "",
        balance = this.balance,
        isVerified = this.isVerified,
        isIDVerified = this.isIDVerified,
        locationLat = this.locationLat,
        locationLng = this.locationLng,
        locationAddress = this.locationAddress
    )
}

private fun SupabasePayment.toTransaction(): Transaction {
    return Transaction(
        id = this.id,
        userId = this.userId,
        amount = this.amount,
        type = when (this.method) {
            "BALANCE" -> "RECHARGE"
            "TRANSFER" -> "WITHDRAWAL"
            "CARD" -> "CARD_PAYMENT"
            else -> "OTHER"
        },
        description = this.details ?: "Transacción",
        status = when (this.status) {
            "COMPLETED" -> "COMPLETED"
            "PENDING" -> "PENDING"
            "FAILED" -> "FAILED"
            "REFUNDED" -> "REFUNDED"
            else -> "PENDING"
        },
        createdAt = this.createdAt ?: ""
    )
}
