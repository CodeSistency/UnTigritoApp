package com.thecodefather.untigrito.domain.repository

import com.thecodefather.untigrito.domain.model.Service
import com.thecodefather.untigrito.domain.model.ServiceFilter
import kotlinx.coroutines.flow.Flow

interface ServicesRepository {
    suspend fun getServices(filter: ServiceFilter): Flow<List<Service>>
    suspend fun getServiceById(serviceId: String): Flow<Service?>
    suspend fun createService(
        title: String,
        description: String,
        category: String,
        minPrice: Double,
        maxPrice: Double,
        serviceArea: String,
        images: List<String> = emptyList()
    ): Flow<Result<Service>>
    suspend fun updateService(service: Service): Flow<Result<Service>>
    suspend fun deleteService(serviceId: String): Flow<Result<Boolean>>
    suspend fun toggleServiceStatus(serviceId: String): Flow<Result<Boolean>>
}