package com.thecodefather.untigrito.domain.usecase

import com.thecodefather.untigrito.domain.model.Service
import com.thecodefather.untigrito.domain.model.ServiceFilter
import com.thecodefather.untigrito.domain.repository.ServicesRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Caso de uso para obtener la lista de servicios
 */
class GetServicesUseCase @Inject constructor(
    private val repository: ServicesRepository
) {
    suspend operator fun invoke(
        filter: ServiceFilter = ServiceFilter.ALL
    ): Result<List<Service>> {
        return try {
            Result.success(repository.getServices(filter).first())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
