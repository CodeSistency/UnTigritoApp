package com.thecodefather.untigrito.presentation.screens.client

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecodefather.untigrito.auth.domain.repository.IAuthRepository
import com.thecodefather.untigrito.data.datasource.remote.SupabaseDatabaseService
import com.thecodefather.untigrito.data.datasource.remote.SupabaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for Client Home Screen
 * 
 * Uses direct Supabase queries to get user balance
 * Following the same pattern as AccountDetailsViewModel
 */
@HiltViewModel
class ClientHomeViewModel @Inject constructor(
    private val supabaseDatabase: SupabaseDatabaseService,
    private val authRepository: IAuthRepository
) : ViewModel() {
    
    private val _balance = MutableStateFlow(0.0)
    val balance: StateFlow<Double> = _balance.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    init {
        loadUserBalance()
    }
    
    /**
     * Load user balance from Supabase
     * Gets the current authenticated user and queries their balance
     */
    private fun loadUserBalance() {
        Timber.d("💰 HOME VIEWMODEL LOAD_BALANCE_START")
        Log.e("TAG", "💰 HOME VIEWMODEL LOAD_BALANCE_START")
        
        viewModelScope.launch {
            _isLoading.value = true
            
            try {
                val currentUser = authRepository.getCurrentUser()
                
                if (currentUser == null) {
                    Timber.w("⚠️ HOME - No authenticated user found")
                    Log.e("TAG", "⚠️ HOME - No authenticated user found")
                    _balance.value = 0.0
                    _isLoading.value = false
                    return@launch
                }
                
                Timber.d("📊 HOME - Loading balance for user: ${currentUser.id}")
                Log.e("TAG", "📊 HOME - Loading balance for user: ${currentUser.id}")
                
                // Consulta directa a Supabase para obtener el balance
                supabaseDatabase.getById<SupabaseUser>("User", currentUser.id)
                    .onSuccess { user ->
                        val userBalance = user?.balance ?: 0.0
                        _balance.value = userBalance
                        
                        Timber.d("✅ HOME - Balance loaded: $userBalance")
                        Log.e("TAG", "✅ HOME - Balance loaded: $userBalance")
                    }
                    .onFailure { error ->
                        Timber.e(error, "❌ HOME - Error loading balance")
                        Log.e("TAG", "❌ HOME - Error loading balance: ${error.message}", error)
                        _balance.value = 0.0
                    }
                
            } catch (e: Exception) {
                Timber.e(e, "❌ HOME - Unexpected error loading balance")
                Log.e("TAG", "❌ HOME - Unexpected error: ${e.message}", e)
                _balance.value = 0.0
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Refresh user balance
     * Can be called when user returns to home screen
     */
    fun refreshBalance() {
        Timber.d("🔄 HOME - Refreshing balance")
        Log.e("TAG", "🔄 HOME - Refreshing balance")
        loadUserBalance()
    }
}

