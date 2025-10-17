package com.thecodefather.untigrito.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * State for the Home Screen
 */
data class HomeUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val data: String = "Welcome to UnTigritoApp"
)

/**
 * ViewModel for the Home Screen
 *
 * Manages the state and business logic for the home screen.
 * Uses Hilt for dependency injection.
 *
 * @param getUserUseCase Use case for fetching user data (to be injected)
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    // private val getUserUseCase: GetUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        // TODO: Implement data loading using use cases
        // For now, just use the default state
    }

    fun retry() {
        loadData()
    }
}
