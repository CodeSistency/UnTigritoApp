package com.thecodefather.untigrito.domain.usecase

import com.thecodefather.untigrito.domain.model.Service
import com.thecodefather.untigrito.domain.repository.ServicesRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Caso de uso para crear un nuevo servicio
 */
class CreateServiceUseCase @Inject constructor(
    private val repository: ServicesRepository
) {
    suspend operator fun invoke(
        title: String,
        description: String,
        minPrice: Double,
        maxPrice: Double,
        serviceArea: String,
        category: String
    ): Result<Service> {
        return try {
            repository.createService(
                title = title,
                description = description,
                minPrice = minPrice,
                maxPrice = maxPrice,
                serviceArea = serviceArea,
                category = category
            ).first()
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
