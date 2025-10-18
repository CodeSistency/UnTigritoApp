package com.thecodefather.untigrito.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity for ClientRequest
 * Represents an offer on a service posting
 */
@Entity(
    tableName = "client_requests",
    foreignKeys = [
        ForeignKey(
            entity = ClientUserEntity::class,
            parentColumns = ["id"],
            childColumns = ["clientId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ServicePostingEntity::class,
            parentColumns = ["id"],
            childColumns = ["servicePostingId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("clientId"),
        Index("servicePostingId"),
        Index("status"),
        Index("createdAt")
    ]
)
data class ClientRequestEntity(
    @PrimaryKey
    val id: String,
    val clientId: String,
    val servicePostingId: String,
    val professionalId: String? = null,
    val status: String = "PENDING",
    val proposedPrice: Double,
    val description: String,
    val estimatedDuration: Int = 0,
    val createdAt: String = "",
    val updatedAt: String = ""
)
