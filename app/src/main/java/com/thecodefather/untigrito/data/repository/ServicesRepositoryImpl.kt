package com.thecodefather.untigrito.data.repository

import com.thecodefather.untigrito.domain.model.*
import com.thecodefather.untigrito.domain.repository.ServicesRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServicesRepositoryImpl @Inject constructor() : ServicesRepository {

    private val dummyServices = listOf(
        Service(
            id = "service1",
            title = "Reparación de Plomería",
            description = "Servicio completo de reparación de plomería en hogares y oficinas. Incluye grifos, tuberías, sanitarios y más.",
            category = "Plomería",
            minPrice = 50.0,
            maxPrice = 200.0,
            serviceArea = "Caracas y Miranda",
            status = ServiceStatus.ACTIVE,
            images = listOf("plomeria1.jpg", "plomeria2.jpg"),
            createdAt = Date(System.currentTimeMillis() - 30 * 24 * 60 * 60 * 1000L),
            updatedAt = Date(System.currentTimeMillis() - 5 * 24 * 60 * 60 * 1000L),
            isActive = true,
            rating = 4.8,
            reviewCount = 25,
            completedJobs = 18
        ),
        Service(
            id = "service2",
            title = "Instalación de Aires Acondicionados",
            description = "Instalación profesional de sistemas de aire acondicionado split y central. Incluye mantenimiento.",
            category = "Refrigeración",
            minPrice = 200.0,
            maxPrice = 500.0,
            serviceArea = "Caracas, Miranda, Aragua",
            status = ServiceStatus.ACTIVE,
            images = listOf("aire1.jpg", "aire2.jpg"),
            createdAt = Date(System.currentTimeMillis() - 20 * 24 * 60 * 60 * 1000L),
            updatedAt = Date(System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000L),
            isActive = true,
            rating = 4.9,
            reviewCount = 15,
            completedJobs = 12
        ),
        Service(
            id = "service3",
            title = "Pintura Residencial",
            description = "Servicio de pintura para hogares, apartamentos y oficinas. Incluye preparación de superficies.",
            category = "Pintura",
            minPrice = 300.0,
            maxPrice = 800.0,
            serviceArea = "Caracas y Miranda",
            status = ServiceStatus.ACTIVE,
            images = listOf("pintura1.jpg", "pintura2.jpg"),
            createdAt = Date(System.currentTimeMillis() - 15 * 24 * 60 * 60 * 1000L),
            updatedAt = Date(System.currentTimeMillis() - 1 * 24 * 60 * 60 * 1000L),
            isActive = true,
            rating = 4.7,
            reviewCount = 8,
            completedJobs = 6
        ),
        Service(
            id = "service4",
            title = "Reparación de Electrodomésticos",
            description = "Reparación especializada de neveras, lavadoras, secadoras y otros electrodomésticos.",
            category = "Electrodomésticos",
            minPrice = 80.0,
            maxPrice = 300.0,
            serviceArea = "Caracas",
            status = ServiceStatus.INACTIVE,
            images = listOf("electro1.jpg"),
            createdAt = Date(System.currentTimeMillis() - 10 * 24 * 60 * 60 * 1000L),
            updatedAt = Date(System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000L),
            isActive = false,
            rating = 4.5,
            reviewCount = 5,
            completedJobs = 4
        ),
        Service(
            id = "service5",
            title = "Instalación de Cerraduras",
            description = "Instalación y reparación de cerraduras tradicionales y digitales. Incluye programación.",
            category = "Cerrajería",
            minPrice = 60.0,
            maxPrice = 150.0,
            serviceArea = "Caracas y Miranda",
            status = ServiceStatus.ACTIVE,
            images = listOf("cerradura1.jpg"),
            createdAt = Date(System.currentTimeMillis() - 5 * 24 * 60 * 60 * 1000L),
            updatedAt = Date(System.currentTimeMillis() - 1 * 24 * 60 * 60 * 1000L),
            isActive = true,
            rating = 4.6,
            reviewCount = 3,
            completedJobs = 2
        )
    )

    override suspend fun getServices(filter: ServiceFilter): Flow<List<Service>> = flow {
        delay(800)
        
        val filteredServices = when (filter) {
            ServiceFilter.ALL -> dummyServices
            ServiceFilter.ACTIVE -> dummyServices.filter { it.status == ServiceStatus.ACTIVE }
            ServiceFilter.INACTIVE -> dummyServices.filter { it.status == ServiceStatus.INACTIVE }
        }
        
        emit(filteredServices.sortedByDescending { it.updatedAt })
    }

    override suspend fun getServiceById(serviceId: String): Flow<Service?> = flow {
        delay(500)
        emit(dummyServices.find { it.id == serviceId })
    }

    override suspend fun createService(
        title: String,
        description: String,
        category: String,
        minPrice: Double,
        maxPrice: Double,
        serviceArea: String,
        images: List<String>
    ): Flow<Result<Service>> = flow {
        delay(1000)
        
        val newService = Service(
            id = "service_${System.currentTimeMillis()}",
            title = title,
            description = description,
            category = category,
            minPrice = minPrice,
            maxPrice = maxPrice,
            serviceArea = serviceArea,
            status = ServiceStatus.ACTIVE,
            images = images,
            createdAt = Date(),
            updatedAt = Date(),
            isActive = true
        )
        
        emit(Result.success(newService))
    }

    override suspend fun updateService(service: Service): Flow<Result<Service>> = flow {
        delay(800)
        val updatedService = service.copy(updatedAt = Date())
        emit(Result.success(updatedService))
    }

    override suspend fun deleteService(serviceId: String): Flow<Result<Boolean>> = flow {
        delay(500)
        emit(Result.success(true))
    }

    override suspend fun toggleServiceStatus(serviceId: String): Flow<Result<Boolean>> = flow {
        delay(500)
        emit(Result.success(true))
    }
}
