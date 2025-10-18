package com.thecodefather.untigrito.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.thecodefather.untigrito.data.database.dao.AuthStateDao
import com.thecodefather.untigrito.data.database.dao.ClientRequestDao
import com.thecodefather.untigrito.data.database.dao.ClientUserDao
import com.thecodefather.untigrito.data.database.dao.ServicePostingDao
import com.thecodefather.untigrito.data.database.dao.TransactionDao
import com.thecodefather.untigrito.data.database.entity.AuthStateEntity
import com.thecodefather.untigrito.data.database.entity.ClientRequestEntity
import com.thecodefather.untigrito.data.database.entity.ClientUserEntity
import com.thecodefather.untigrito.data.database.entity.ServicePostingEntity
import com.thecodefather.untigrito.data.database.entity.TransactionEntity

@Database(
    entities = [
        ClientUserEntity::class,
        ClientRequestEntity::class,
        ServicePostingEntity::class,
        TransactionEntity::class,
        AuthStateEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun clientUserDao(): ClientUserDao
    abstract fun clientRequestDao(): ClientRequestDao
    abstract fun servicePostingDao(): ServicePostingDao
    abstract fun transactionDao(): TransactionDao
    abstract fun authStateDao(): AuthStateDao
}
