package com.thecodefather.untigrito.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "job_favorites",
    indices = [Index(value = ["userId", "jobId"], unique = true)]
)
data class JobFavoriteEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val jobId: String,
    val createdAt: Long = System.currentTimeMillis()
)
