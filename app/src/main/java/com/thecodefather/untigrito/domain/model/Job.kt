package com.thecodefather.untigrito.domain.model

import java.util.Date

data class Job(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val budget: Double,
    val clientId: String,
    val clientName: String,
    val clientAvatar: String?,
    val status: JobStatus,
    val location: Location?,
    val deadline: Date?,
    val createdAt: Date,
    val isRecommended: Boolean = false,
    val isFavorite: Boolean = false,
    val urgency: UrgencyLevel = UrgencyLevel.NORMAL,
    val estimatedDuration: String? = null,
    val requiredSkills: List<String> = emptyList(),
    val attachments: List<String> = emptyList()
)

enum class JobStatus {
    OPEN,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
}

enum class UrgencyLevel {
    LOW,
    NORMAL,
    HIGH,
    URGENT
}

data class Location(
    val address: String,
    val city: String,
    val state: String,
    val coordinates: Coordinates? = null
)

data class Coordinates(
    val latitude: Double,
    val longitude: Double
)