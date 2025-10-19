package com.thecodefather.untigrito.presentation.viewmodel

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecodefather.untigrito.domain.model.BankDetails
import com.thecodefather.untigrito.domain.model.PaymentParams
import com.thecodefather.untigrito.domain.usecase.PaymentFlowManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para la pantalla de pago unificada
 * Maneja los estados y la lógica de negocio para los 3 escenarios de pago
 */
@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val paymentFlowManager: PaymentFlowManager,
    private val savedStateHandle: SavedStateHandle,
    @ApplicationContext private val context: Context
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(PaymentUiState())
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()
    
    /**
     * Inicializa el proceso de pago
     */
    fun initializePayment(params: PaymentParams) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            paymentFlowManager.initiatePayment(params)
                .onSuccess { (paymentId, bankDetails) ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        paymentId = paymentId,
                        bankDetails = bankDetails,
                        amount = params.amount,
                        concept = params.concept,
                        paymentParams = params
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Error al iniciar el pago"
                    )
                }
        }
    }
    
    /**
     * Confirma el pago realizado por el usuario
     */
    fun confirmPayment(externalRef: String) {
        viewModelScope.launch {
            val state = _uiState.value
            if (state.paymentId == null || state.paymentParams == null) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Datos de pago no disponibles"
                )
                return@launch
            }
            
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            paymentFlowManager.confirmPayment(state.paymentId, externalRef, state.paymentParams)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        paymentCompleted = true
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Error al confirmar el pago"
                    )
                }
        }
    }
    
    /**
     * Copia texto al portapapeles
     */
    fun copyToClipboard(text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Bank Details", text)
        clipboard.setPrimaryClip(clip)
    }
    
    /**
     * Copia todos los datos bancarios al portapapeles
     */
    fun copyAllBankDetails() {
        val bankDetails = _uiState.value.bankDetails
        if (bankDetails != null) {
            val allDetails = "Teléfono: ${bankDetails.phone}\n" +
                    "RIF: ${bankDetails.rif}\n" +
                    "Banco: ${bankDetails.bankName}"
            copyToClipboard(allDetails)
        }
    }
    
    /**
     * Muestra el diálogo de confirmación
     */
    fun showConfirmationDialog() {
        _uiState.value = _uiState.value.copy(showConfirmationDialog = true)
    }
    
    /**
     * Oculta el diálogo de confirmación
     */
    fun hideConfirmationDialog() {
        _uiState.value = _uiState.value.copy(showConfirmationDialog = false)
    }
    
    /**
     * Limpia el mensaje de error
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

/**
 * Estado de la UI para la pantalla de pago
 */
data class PaymentUiState(
    val isLoading: Boolean = false,
    val paymentId: String? = null,
    val bankDetails: BankDetails? = null,
    val amount: Double = 0.0,
    val concept: String = "",
    val paymentParams: PaymentParams? = null,
    val showConfirmationDialog: Boolean = false,
    val paymentCompleted: Boolean = false,
    val errorMessage: String? = null
)