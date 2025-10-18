package com.thecodefather.untigrito.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity for Transaction
 * Represents payment and transaction history
 */
@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = ClientUserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("userId"),
        Index("type"),
        Index("status"),
        Index("createdAt")
    ]
)
data class TransactionEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val type: String,
    val amount: Double,
    val description: String,
    val status: String = "PENDING",
    val createdAt: String = ""
)
