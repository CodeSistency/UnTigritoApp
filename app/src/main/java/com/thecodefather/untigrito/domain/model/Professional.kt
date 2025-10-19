package com.thecodefather.untigrito.domain.model

import com.thecodefather.untigrito.data.datasource.remote.SupabaseProfessionalProfile

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
    val imageUrl: String? = null, // Añadir esta propiedad para la imagen del profesional
    val createdAt: String = "",
    val updatedAt: String = ""
)

// ========== Extension Functions for Supabase Integration ==========

/**
 * Converts SupabaseProfessionalProfile to Professional domain model
 */
fun SupabaseProfessionalProfile.toProfessional(): Professional {
    return Professional(
        id = this.id,
        userId = this.userId,
        bio = this.bio,
        rating = this.rating,
        totalReviews = this.ratingCount,
        yearsOfExperience = this.yearsOfExperience,
        certifications = this.certifications,
        specialties = this.specialties?.split(",")?.map { it.trim() } ?: emptyList(),
        responseTime = this.responseTime,
        completionRate = this.completionRate,
        hourlyRate = this.hourlyRate,
        bankAccount = this.bankAccount,
        taxId = this.taxId,
        isVerified = this.isVerified,
        imageUrl = null,
        createdAt = this.createdAt ?: "",
        updatedAt = this.updatedAt ?: ""
    )
}
