package com.thecodefather.untigrito.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecodefather.untigrito.data.repository.ClientRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.thecodefather.untigrito.domain.model.ClientUser
import com.thecodefather.untigrito.domain.model.Transaction
import com.thecodefather.untigrito.domain.repository.ClientRepository

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val repository: ClientRepositoryImpl
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

    private val _currentUserId = "current_user_id"

    init {
        loadPaymentData()
    }

    private fun loadPaymentData() {
        _loading.value = true
        viewModelScope.launch {
            try {
                // Load user
                repository.getUserById(_currentUserId).collect { user ->
                    _user.value = user
                }

                // Load transactions
                repository.getTransactionsByUser(_currentUserId).collect { transactions ->
                    _transactions.value = transactions
                }

                // Load totals
                repository.getTotalRecharged(_currentUserId).collect { total ->
                    _totalRecharged.value = total
                }

                repository.getTotalWithdrawn(_currentUserId).collect { total ->
                    _totalWithdrawn.value = total
                }
            } catch (e: Exception) {
                _error.value = "Error loading payment data: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun addTransaction(amount: Double, type: String, description: String) {
        viewModelScope.launch {
            try {
                val transaction = Transaction(
                    id = "",
                    userId = _currentUserId,
                    type = type,
                    amount = amount,
                    description = description,
                    status = Transaction.STATUS_PENDING
                )
                repository.saveTransaction(transaction)
                loadPaymentData()
            } catch (e: Exception) {
                _error.value = "Error adding transaction: ${e.message}"
            }
        }
    }

    fun getBalance(): Double {
        return _user.value?.balance ?: 0.0
    }
}
