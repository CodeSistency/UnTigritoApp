package com.thecodefather.untigrito.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity for ServicePosting
 * Represents a service request stored in local database
 */
@Entity(
    tableName = "service_postings",
    foreignKeys = [
        ForeignKey(
            entity = ClientUserEntity::class,
            parentColumns = ["id"],
            childColumns = ["clientId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("clientId"),
        Index("status"),
        Index("category"),
        Index("createdAt")
    ]
)
data class ServicePostingEntity(
    @PrimaryKey
    val id: String,
    val clientId: String,
    val title: String,
    val description: String,
    val category: String,
    val budget: Double,
    val deadline: String? = null,
    val status: String = "OPEN",
    val location: String? = null,
    val locationLat: Double? = null,
    val locationLng: Double? = null,
    val createdAt: String = "",
    val updatedAt: String = ""
)
