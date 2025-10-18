package com.thecodefather.untigrito.domain.model

/**
 * Professional model
 * Represents a professional profile with ratings and specialties
 */
data class Professional(
    val id: String,
    val userId: String,
    val bio: String? = null,
    val rating: Double? = null, // 0-5 scale
    val totalReviews: Int? = null,
    val yearsOfExperience: Int? = null,
    val certifications: String? = null,
    val specialties: List<String> = emptyList(),
    val responseTime: Int? = null, // in hours
    val completionRate: Double? = null, // 0-100
    val hourlyRate: Double? = null,
    val bankAccount: String? = null,
    val taxId: String? = null,
    val isVerified: Boolean = false,
    val createdAt: String = "",
    val updatedAt: String = ""
)
