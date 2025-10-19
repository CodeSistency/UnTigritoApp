package com.thecodefather.untigrito.domain.model

import kotlinx.serialization.Serializable

/**
 * Parámetros unificados para iniciar pago
 * Soporta 3 escenarios: recarga de saldo, pago por problema resuelto, pago por servicio
 */
@Serializable
sealed class PaymentParams {
    abstract val clientId: String
    abstract val amount: Double
    abstract val concept: String
    
    @Serializable
    data class Recharge(
        override val clientId: String,
        override val amount: Double,
        override val concept: String = "Recarga de saldo"
    ) : PaymentParams()
    
    @Serializable
    data class ProblemSolved(
        override val clientId: String,
        val professionalId: String,
        val transactionId: String,
        override val amount: Double,
        override val concept: String = "Pago problema resuelto"
    ) : PaymentParams()
    
    @Serializable
    data class ServicePayment(
        override val clientId: String,
        val professionalId: String,
        val serviceId: String,
        override val amount: Double,
        override val concept: String = "Pago servicio profesional"
    ) : PaymentParams()
}

/**
 * Datos bancarios del receptor del pago
 */
@Serializable
data class BankDetails(
    val phone: String,
    val rif: String,
    val bankName: String
)

/**
 * Estados de pago
 */
enum class PaymentStatus {
    PENDING, COMPLETED, FAILED
}

/**
 * Métodos de pago
 */
enum class PaymentMethod {
    TRANSFER, CARD, CASH
}
