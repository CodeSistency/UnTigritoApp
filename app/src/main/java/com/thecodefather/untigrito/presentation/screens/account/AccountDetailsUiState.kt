package com.thecodefather.untigrito.presentation.screens.account

import com.thecodefather.untigrito.presentation.screens.account.data.Transaction

/**
 * UI State for Account Details Screen
 * 
 * Represents all possible states of the account details screen
 * including loading, data, and error states
 */
data class AccountDetailsUiState(
    val isLoading: Boolean = false,
    val balance: Double = 0.0,
    val transactions: List<Transaction> = emptyList(),
    val errorMessage: String? = null
)

