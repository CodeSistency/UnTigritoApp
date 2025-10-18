package com.thecodefather.untigrito.domain.usecase

import com.thecodefather.untigrito.domain.model.Service
import com.thecodefather.untigrito.domain.repository.ServicesRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Caso de uso para obtener los detalles de un servicio específico
 */
class GetServiceDetailsUseCase @Inject constructor(
    private val repository: ServicesRepository
) {
    suspend operator fun invoke(serviceId: String): Result<Service?> {
        return try {
            Result.success(repository.getServiceById(serviceId).first())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
