package com.thecodefather.untigrito.data.repository

import com.thecodefather.untigrito.domain.model.*
import com.thecodefather.untigrito.domain.repository.JobsRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JobsRepositoryImpl @Inject constructor() : JobsRepository {

    private val dummyJobs = listOf(
        Job(
            id = "1",
            title = "Reparación de grifo en cocina",
            description = "Necesito reparar un grifo que gotea en la cocina. El problema parece ser en la llave de paso.",
            category = "Plomería",
            budget = 150.0,
            clientId = "client1",
            clientName = "María González",
            clientAvatar = null,
            status = JobStatus.OPEN,
            location = Location("Av. Principal 123", "Caracas", "Distrito Capital"),
            deadline = Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000L),
            createdAt = Date(System.currentTimeMillis() - 2 * 60 * 60 * 1000L),
            isRecommended = true,
            isFavorite = false,
            urgency = UrgencyLevel.NORMAL,
            estimatedDuration = "2-3 horas",
            requiredSkills = listOf("Plomería", "Herramientas básicas")
        ),
        Job(
            id = "2",
            title = "Instalación de aire acondicionado",
            description = "Instalar un aire acondicionado split en habitación de 15m². Ya tengo el equipo comprado.",
            category = "Electricidad",
            budget = 300.0,
            clientId = "client2",
            clientName = "Carlos Rodríguez",
            clientAvatar = null,
            status = JobStatus.OPEN,
            location = Location("Calle 5 de Julio 456", "Valencia", "Carabobo"),
            deadline = Date(System.currentTimeMillis() + 3 * 24 * 60 * 60 * 1000L),
            createdAt = Date(System.currentTimeMillis() - 1 * 60 * 60 * 1000L),
            isRecommended = false,
            isFavorite = true,
            urgency = UrgencyLevel.HIGH,
            estimatedDuration = "4-6 horas",
            requiredSkills = listOf("Electricidad", "Refrigeración", "Instalaciones")
        ),
        Job(
            id = "3",
            title = "Pintura de apartamento",
            description = "Pintar apartamento de 2 habitaciones, sala y cocina. Incluye preparación de paredes.",
            category = "Pintura",
            budget = 500.0,
            clientId = "client3",
            clientName = "Ana Martínez",
            clientAvatar = null,
            status = JobStatus.OPEN,
            location = Location("Urbanización Los Palos Grandes", "Caracas", "Distrito Capital"),
            deadline = Date(System.currentTimeMillis() + 14 * 24 * 60 * 60 * 1000L),
            createdAt = Date(System.currentTimeMillis() - 30 * 60 * 1000L),
            isRecommended = true,
            isFavorite = false,
            urgency = UrgencyLevel.LOW,
            estimatedDuration = "3-4 días",
            requiredSkills = listOf("Pintura", "Preparación de superficies")
        ),
        Job(
            id = "4",
            title = "Reparación de nevera",
            description = "Mi nevera no está enfriando bien. Necesito que la revisen y reparen.",
            category = "Refrigeración",
            budget = 200.0,
            clientId = "client4",
            clientName = "Roberto Silva",
            clientAvatar = null,
            status = JobStatus.OPEN,
            location = Location("Barrio San José", "Maracay", "Aragua"),
            deadline = Date(System.currentTimeMillis() + 5 * 24 * 60 * 60 * 1000L),
            createdAt = Date(System.currentTimeMillis() - 45 * 60 * 1000L),
            isRecommended = false,
            isFavorite = false,
            urgency = UrgencyLevel.URGENT,
            estimatedDuration = "2-3 horas",
            requiredSkills = listOf("Refrigeración", "Electrodomésticos")
        ),
        Job(
            id = "5",
            title = "Instalación de cerradura digital",
            description = "Instalar cerradura digital en puerta principal. Ya tengo la cerradura.",
            category = "Cerrajería",
            budget = 100.0,
            clientId = "client5",
            clientName = "Isabel Torres",
            clientAvatar = null,
            status = JobStatus.OPEN,
            location = Location("Residencias El Paraíso", "Caracas", "Distrito Capital"),
            deadline = Date(System.currentTimeMillis() + 2 * 24 * 60 * 60 * 1000L),
            createdAt = Date(System.currentTimeMillis() - 15 * 60 * 1000L),
            isRecommended = false,
            isFavorite = true,
            urgency = UrgencyLevel.NORMAL,
            estimatedDuration = "1-2 horas",
            requiredSkills = listOf("Cerrajería", "Instalaciones")
        )
    )

    override suspend fun getJobs(filter: JobFilter, searchQuery: String): Flow<List<Job>> = flow {
        delay(1000) // Simular llamada a API
        
        val filteredJobs = when (filter) {
            JobFilter.RECENT -> dummyJobs.sortedByDescending { it.createdAt }
            JobFilter.RECOMMENDED -> dummyJobs.filter { it.isRecommended }
            JobFilter.FAVORITES -> dummyJobs.filter { it.isFavorite }
        }
        
        val searchFiltered = if (searchQuery.isNotEmpty()) {
            filteredJobs.filter { 
                it.title.contains(searchQuery, ignoreCase = true) ||
                it.description.contains(searchQuery, ignoreCase = true) ||
                it.category.contains(searchQuery, ignoreCase = true)
            }
        } else {
            filteredJobs
        }
        
        emit(searchFiltered)
    }

    override suspend fun searchJobs(query: String, page: Int, perPage: Int): Flow<List<Job>> = flow {
        delay(800) // Simular búsqueda

        val searchResults = if (query.isNotBlank()) {
            dummyJobs.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.description.contains(query, ignoreCase = true) ||
                it.category.contains(query, ignoreCase = true)
            }
        } else {
            dummyJobs
        }

        // Simular paginación
        val startIndex = (page - 1) * perPage
        val endIndex = minOf(startIndex + perPage, searchResults.size)
        val paginatedResults = if (startIndex < searchResults.size) {
            searchResults.subList(startIndex, endIndex)
        } else {
            emptyList()
        }

        emit(paginatedResults)
    }

    override suspend fun getJobById(jobId: String): Flow<Job?> = flow {
        delay(500)
        emit(dummyJobs.find { it.id == jobId })
    }

    override suspend fun toggleFavorite(jobId: String): Flow<Boolean> = flow {
        delay(300)
        // Simular toggle de favorito
        emit(true)
    }

    override suspend fun getFavoriteJobs(): Flow<List<Job>> = flow {
        delay(500)
        emit(dummyJobs.filter { it.isFavorite })
    }

    override suspend fun getRecommendedJobs(): Flow<List<Job>> = flow {
        delay(500)
        emit(dummyJobs.filter { it.isRecommended })
    }
}
