package com.thecodefather.untigrito.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thecodefather.untigrito.data.repository.ClientRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.thecodefather.untigrito.domain.model.ClientUser
import com.thecodefather.untigrito.domain.repository.ClientRepository

@HiltViewModel
class ClientProfileViewModel @Inject constructor(
    private val repository: ClientRepositoryImpl
) : ViewModel() {

    private val _user = MutableStateFlow<ClientUser?>(null)
    val user = _user.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        _loading.value = true
        viewModelScope.launch {
            try {
                repository.getUserById("current_user_id").collect { user ->
                    _user.value = user
                }
            } catch (e: Exception) {
                _error.value = "Error loading profile: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun logout() {
        // TODO: Call auth repository to logout
    }

    fun updateProfile(user: ClientUser) {
        viewModelScope.launch {
            try {
                repository.saveUser(user)
                _user.value = user
            } catch (e: Exception) {
                _error.value = "Error updating profile: ${e.message}"
            }
        }
    }

    fun requestVerification() {
        // TODO: Implement verification request
    }
}
