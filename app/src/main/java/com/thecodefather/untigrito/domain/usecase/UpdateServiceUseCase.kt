package com.thecodefather.untigrito.domain.usecase

import com.thecodefather.untigrito.domain.model.Service
import com.thecodefather.untigrito.domain.repository.ServicesRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Caso de uso para actualizar un servicio existente
 */
class UpdateServiceUseCase @Inject constructor(
    private val repository: ServicesRepository
) {
    suspend operator fun invoke(service: Service): Result<Service> {
        return try {
            repository.updateService(service).first()
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
