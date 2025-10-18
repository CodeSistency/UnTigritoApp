package com.thecodefather.untigrito.presentation.screens.account.data

data class Transaction(
    val id: String,
    val type: String,
    val description: String,
    val date: String,
    val amount: Double
)
