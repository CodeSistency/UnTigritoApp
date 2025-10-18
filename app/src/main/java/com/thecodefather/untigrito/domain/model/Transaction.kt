package com.thecodefather.untigrito.domain.model

/**
 * Transaction model
 * Represents payment and transaction history
 */
data class Transaction(
    val id: String,
    val userId: String,
    val type: String, // RECHARGE, WITHDRAWAL, PAYMENT
    val amount: Double,
    val description: String,
    val status: String = "PENDING", // PENDING, COMPLETED, FAILED
    val createdAt: String = ""
) {
    companion object {
        const val TYPE_RECHARGE = "RECHARGE"
        const val TYPE_WITHDRAWAL = "WITHDRAWAL"
        const val TYPE_PAYMENT = "PAYMENT"

        const val STATUS_PENDING = "PENDING"
        const val STATUS_COMPLETED = "COMPLETED"
        const val STATUS_FAILED = "FAILED"
    }
}
