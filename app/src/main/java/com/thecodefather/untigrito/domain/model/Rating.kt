package com.thecodefather.untigrito.domain.model

import java.util.Date

/**
 * Entidad de dominio para Calificación
 */
data class Rating(
    val id: String,
    val professionalId: String,
    val clientId: String,
    val proposalId: String,
    val score: Int, // 1-5
    val review: String? = null,
    val createdAt: Date,
    val photos: List<String> = emptyList()
)
