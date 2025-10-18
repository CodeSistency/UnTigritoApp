package com.thecodefather.untigrito.domain.model

import java.util.Date

data class Service(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val minPrice: Double,
    val maxPrice: Double,
    val serviceArea: String,
    val status: ServiceStatus,
    val images: List<String> = emptyList(),
    val createdAt: Date,
    val updatedAt: Date,
    val isActive: Boolean = true,
    val rating: Double = 0.0,
    val reviewCount: Int = 0,
    val completedJobs: Int = 0
)

enum class ServiceStatus {
    ACTIVE,
    INACTIVE,
    DRAFT
}