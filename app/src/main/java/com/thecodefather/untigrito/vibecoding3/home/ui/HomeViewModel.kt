package com.example.vibecoding3.home.ui

import androidx.lifecycle.ViewModel
import com.example.vibecoding3.auth.domain.repository.IAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * ViewModel para la pantalla de home
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: IAuthRepository
) : ViewModel() {
    
    val currentUser = flow {
        val user = authRepository.getCurrentUser()
        emit(user)
    }
}
