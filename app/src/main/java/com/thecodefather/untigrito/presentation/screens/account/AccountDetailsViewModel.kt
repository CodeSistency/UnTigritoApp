package com.thecodefather.untigrito.presentation.screens.account

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecodefather.untigrito.presentation.screens.account.data.Transaction
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AccountDetailsViewModel : ViewModel() {

    var balance by mutableStateOf("0,00")
        private set

    var transactions by mutableStateOf(emptyList<Transaction>())
        private set

    var isRefreshing by mutableStateOf(false)
        private set

    init {
        loadAccountDetails()
    }

    fun loadAccountDetails() {
        viewModelScope.launch {
            isRefreshing = true
            delay(2000) // Simula una llamada a la red
            balance = "15.000,00"
            transactions = listOf(
                Transaction("1", "Recarga pago móvil", "", "15/10/2025", 11000.00),
                Transaction("2", "Retiro pago móvil", "", "15/10/2025", -8000.00),
                Transaction("3", "Recarga pago móvil", "", "15/10/2025", 2000.00),
                Transaction("4", "Recarga pago móvil", "", "15/10/2025", 10000.00),
            )
            isRefreshing = false
        }
    }
}
