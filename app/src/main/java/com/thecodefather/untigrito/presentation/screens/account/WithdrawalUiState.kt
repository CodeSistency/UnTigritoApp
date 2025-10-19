package com.thecodefather.untigrito.presentation.screens.account

/**
 * UI State for Withdrawal operations
 * 
 * Represents all possible states of the withdrawal flow
 * including loading, payment methods, and success states
 */
data class WithdrawalUiState(
    val isLoading: Boolean = false,
    val paymentMethods: List<PaymentMethod> = emptyList(),
    val errorMessage: String? = null,
    val withdrawalSuccess: Boolean = false
)

/**
 * PaymentMethod data class for UI
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
