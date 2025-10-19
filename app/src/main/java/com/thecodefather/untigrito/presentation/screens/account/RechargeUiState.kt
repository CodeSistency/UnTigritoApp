package com.thecodefather.untigrito.presentation.screens.account

/**
 * UI State for Recharge operations
 * 
 * Represents all possible states of the recharge flow
 * including loading and success states
 */
data class RechargeUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val rechargeSuccess: Boolean = false
)
