package com.thecodefather.untigrito.domain.usecase

import com.thecodefather.untigrito.data.datasource.remote.SupabaseDatabaseService
import com.thecodefather.untigrito.domain.model.BankDetails
import com.thecodefather.untigrito.domain.model.PaymentParams
import com.thecodefather.untigrito.data.datasource.remote.SupabasePayment
import com.thecodefather.untigrito.data.datasource.remote.SupabaseUser
import com.thecodefather.untigrito.data.datasource.remote.SupabaseProfessionalProfile
import com.thecodefather.untigrito.data.datasource.remote.SupabaseServiceTransaction
import com.thecodefather.untigrito.auth.domain.usecase.AuthStateManager
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager para el flujo de pagos unificado
 * Maneja los 3 escenarios: recarga, pago por problema resuelto, pago por servicio
 */
@Singleton
class PaymentFlowManager @Inject constructor(
    private val supabaseDatabase: SupabaseDatabaseService,
    private val authStateManager: AuthStateManager
) {
    
    // Datos bancarios de la plataforma (hardcoded)
    private val platformBankDetails = BankDetails(
        phone = "0424-3865670",
        rif = "J-269873020",
        bankName = "Banco nacional de crédito"
    )
    
    private val json = Json { ignoreUnknownKeys = true }
    
    /**
     * Inicia el proceso de pago
     * @param params Parámetros del pago según el escenario
     * @return Result con el ID del pago y los datos bancarios del receptor
     */
    suspend fun initiatePayment(params: PaymentParams): Result<Pair<String, BankDetails>> {
        return try {
            val bankDetails = when (params) {
                is PaymentParams.Recharge -> platformBankDetails
                is PaymentParams.ProblemSolved -> getProfessionalBankDetails(params.professionalId)
                is PaymentParams.ServicePayment -> getProfessionalBankDetails(params.professionalId)
            }
            
            val payment = SupabasePayment(
                id = UUID.randomUUID().toString(),
                userId = params.clientId,
                recipientId = getRecipientId(params),
                amount = params.amount,
                method = "TRANSFER",
                status = "PENDING",
                details = createDetailsJson(params, bankDetails),
                transactionId = getTransactionId(params),
                createdAt = System.currentTimeMillis().toString(),
                updatedAt = System.currentTimeMillis().toString()
            )
            
            supabaseDatabase.insert("Payment", payment)
            Result.success(Pair(payment.id, bankDetails))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Confirma el pago realizado por el usuario
     * @param paymentId ID del pago
     * @param externalRef Referencia bancaria externa
     * @param params Parámetros originales del pago
     * @return Result indicando éxito o fallo
     */
    suspend fun confirmPayment(
        paymentId: String,
        externalRef: String,
        params: PaymentParams
    ): Result<Unit> {
        return try {
            // Actualizar Payment a COMPLETED
            val updateData = mapOf(
                "status" to "COMPLETED",
                "details" to createUpdatedDetailsJson(externalRef),
                "updatedAt" to System.currentTimeMillis().toString()
            )
            supabaseDatabase.update("Payment", paymentId, updateData)
            
            // Ejecutar acciones según el tipo de pago
            when (params) {
                is PaymentParams.Recharge -> updateUserBalance(params.clientId, params.amount)
                is PaymentParams.ProblemSolved -> updateServiceTransaction(params.transactionId)
                is PaymentParams.ServicePayment -> createServiceTransaction(params)
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Obtiene los datos bancarios del profesional
     */
    private suspend fun getProfessionalBankDetails(professionalId: String): BankDetails {
        val professional = supabaseDatabase.getById<SupabaseProfessionalProfile>(
            "ProfessionalProfile", professionalId
        ).getOrNull()
        
        return professional?.let {
            // Obtener el teléfono del usuario asociado
            val user = supabaseDatabase.getById<SupabaseUser>("User", it.userId).getOrNull()
            BankDetails(
                phone = user?.phone ?: "",
                rif = it.taxId ?: "",
                bankName = it.bankAccount ?: ""
            )
        } ?: throw Exception("Profesional no encontrado")
    }
    
    /**
     * Obtiene el ID del receptor según el tipo de pago
     */
    private fun getRecipientId(params: PaymentParams): String? {
        return when (params) {
            is PaymentParams.Recharge -> null // Plataforma recibe
            is PaymentParams.ProblemSolved -> params.professionalId
            is PaymentParams.ServicePayment -> params.professionalId
        }
    }
    
    /**
     * Obtiene el ID de transacción según el tipo de pago
     */
    private fun getTransactionId(params: PaymentParams): String? {
        return when (params) {
            is PaymentParams.Recharge -> null
            is PaymentParams.ProblemSolved -> params.transactionId
            is PaymentParams.ServicePayment -> null // Se creará al confirmar
        }
    }
    
    /**
     * Crea JSON con detalles del pago y datos bancarios
     */
    private fun createDetailsJson(params: PaymentParams, bankDetails: BankDetails): String {
        val details = mapOf(
            "concept" to params.concept,
            "bankDetails" to mapOf(
                "phone" to bankDetails.phone,
                "rif" to bankDetails.rif,
                "bankName" to bankDetails.bankName
            ),
            "paymentType" to when (params) {
                is PaymentParams.Recharge -> "RECHARGE"
                is PaymentParams.ProblemSolved -> "PROBLEM_SOLVED"
                is PaymentParams.ServicePayment -> "SERVICE_PAYMENT"
            }
        )
        return json.encodeToString(details)
    }
    
    /**
     * Crea JSON actualizado con referencia externa
     */
    private fun createUpdatedDetailsJson(externalRef: String): String {
        val details = mapOf(
            "externalRef" to externalRef,
            "confirmedAt" to System.currentTimeMillis().toString()
        )
        return json.encodeToString(details)
    }
    
    /**
     * Actualiza el saldo del usuario (para recargas)
     */
    private suspend fun updateUserBalance(userId: String, amount: Double): Result<Unit> {
        return try {
            val user = supabaseDatabase.getById<SupabaseUser>("User", userId).getOrNull()
            user?.let {
                val newBalance = it.balance + amount
                val updateData = mapOf(
                    "balance" to newBalance,
                    "updatedAt" to System.currentTimeMillis().toString()
                )
                supabaseDatabase.update("User", userId, updateData)
                Result.success(Unit)
            } ?: Result.failure(Exception("Usuario no encontrado"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Actualiza el estado de la transacción de servicio (para problemas resueltos)
     */
    private suspend fun updateServiceTransaction(transactionId: String): Result<Unit> {
        return try {
            val updateData = mapOf(
                "status" to "COMPLETED",
                "updatedAt" to System.currentTimeMillis().toString()
            )
            supabaseDatabase.update("ServiceTransaction", transactionId, updateData)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Crea una nueva transacción de servicio (para pagos de servicios específicos)
     */
    private suspend fun createServiceTransaction(params: PaymentParams.ServicePayment): Result<Unit> {
        return try {
            val serviceTransaction = SupabaseServiceTransaction(
                id = UUID.randomUUID().toString(),
                clientId = params.clientId,
                professionalId = params.professionalId,
                priceAgreed = params.amount,
                status = "COMPLETED",
                postingId = null,
                proServiceId = params.serviceId,
                createdAt = System.currentTimeMillis().toString(),
                updatedAt = System.currentTimeMillis().toString()
            )
            supabaseDatabase.insert("ServiceTransaction", serviceTransaction)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
