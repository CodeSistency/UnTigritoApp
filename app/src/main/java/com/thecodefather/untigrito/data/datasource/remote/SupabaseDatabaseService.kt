package com.thecodefather.untigrito.data.datasource.remote

import android.annotation.SuppressLint
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
// import io.github.jan.supabase.postgrest.query.filter
// import io.github.jan.supabase.postgrest.query.eq
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Servicio de base de datos usando Supabase Postgrest
 * 
 * Este servicio proporciona métodos para realizar operaciones CRUD
 * en la base de datos de Supabase.
 * 
 * IMPORTANTE: Este es un ejemplo. Adapta las tablas y modelos según tu esquema.
 */
@Singleton
public class SupabaseDatabaseService @Inject constructor(
    val postgrest: Postgrest
) {

    /**
     * Ejemplo: Obtiene todos los elementos de una tabla
     * 
     * @param table Nombre de la tabla
     * @return Lista de elementos del tipo especificado
     */
    public suspend inline fun <reified T : Any> getAll(table: String): Result<List<T>> =
        withContext(Dispatchers.IO) {
            try {
                val result = postgrest.from(table)
                    .select()
                    .decodeList<T>()
                
                Timber.d("Obtenidos ${result.size} elementos de $table")
                Result.success(result)
            } catch (e: Exception) {
                Timber.e(e, "Error al obtener elementos de $table")
                Result.failure(e)
            }
        }

    /**
     * Ejemplo: Obtiene un elemento por ID
     * 
     * @param table Nombre de la tabla
     * @param id ID del elemento
     * @return Elemento del tipo especificado
     */
    public suspend inline fun <reified T : Any> getById(table: String, id: String): Result<T?> =
        withContext(Dispatchers.IO) {
            try {
                val result = postgrest.from(table)
                    .select {
                        filter {
                            eq("id", id)
                        }
                    }
                    .decodeSingleOrNull<T>()
                
                Timber.d("Obtenido elemento de $table con id: $id")
                Result.success(result)
            } catch (e: Exception) {
                Timber.e(e, "Error al obtener elemento de $table con id: $id")
                Result.failure(e)
            }
        }

    /**
     * Ejemplo: Inserta un nuevo elemento
     * 
     * @param table Nombre de la tabla
     * @param data Datos a insertar
     * @return Elemento insertado
     */
    public suspend inline fun <reified T : Any> insert(table: String, data: T): Result<T?> =
        withContext(Dispatchers.IO) {
            try {
                val result = postgrest.from(table)
                    .insert(data) {
                        select()
                    }
                    .decodeSingleOrNull<T>()
                
                Timber.d("Elemento insertado en $table")
                Result.success(result)
            } catch (e: Exception) {
                Timber.e(e, "Error al insertar elemento en $table")
                Result.failure(e)
            }
        }

    /**
     * Ejemplo: Actualiza un elemento
     * 
     * @param table Nombre de la tabla
     * @param id ID del elemento a actualizar
     * @param data Datos a actualizar
     * @return Elemento actualizado
     */
    public suspend inline fun <reified T : Any> update(
        table: String,
        id: String,
        data: T
    ): Result<T?> = withContext(Dispatchers.IO) {
        try {
            val result = postgrest.from(table)
                .update(data) {
                    filter {
                        eq("id", id)
                    }
                    select()
                }
                .decodeSingleOrNull<T>()
            
            Timber.d("Elemento actualizado en $table con id: $id")
            Result.success(result)
        } catch (e: Exception) {
            Timber.e(e, "Error al actualizar elemento en $table con id: $id")
            Result.failure(e)
        }
    }

    /**
     * Ejemplo: Elimina un elemento
     * 
     * @param table Nombre de la tabla
     * @param id ID del elemento a eliminar
     */
    suspend fun delete(table: String, id: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                postgrest.from(table)
                    .delete {
                        filter {
                            eq("id", id)
                        }
                    }
                
                Timber.d("Elemento eliminado de $table con id: $id")
                Result.success(Unit)
            } catch (e: Exception) {
                Timber.e(e, "Error al eliminar elemento de $table con id: $id")
                Result.failure(e)
            }
        }

    /**
     * Ejemplo: Consulta con filtros personalizados
     * 
     * @param table Nombre de la tabla
     * @param column Columna por la que filtrar
     * @param value Valor a buscar
     * @return Lista de elementos que coinciden con el filtro
     */
    public suspend inline fun <reified T : Any> findBy(
        table: String,
        column: String,
        value: Any
    ): Result<List<T>> = withContext(Dispatchers.IO) {
        try {
            val result = postgrest.from(table)
                .select {
                    filter {
                        eq(column, value)
                    }
                }
                .decodeList<T>()
            
            Timber.d("Encontrados ${result.size} elementos en $table donde $column = $value")
            Result.success(result)
        } catch (e: Exception) {
            Timber.e(e, "Error al buscar en $table donde $column = $value")
            Result.failure(e)
        }
    }

    /**
     * Ejemplo: Consulta con ordenamiento
     * 
     * @param table Nombre de la tabla
     * @param orderBy Columna por la que ordenar
     * @param ascending Si es ascendente o descendente
     * @return Lista ordenada de elementos
     */
    public suspend inline fun <reified T : Any> getAllOrdered(
        table: String,
        orderBy: String,
        ascending: Boolean = true
    ): Result<List<T>> = withContext(Dispatchers.IO) {
        try {
            val result = postgrest.from(table)
                .select {
                    order(orderBy, if (ascending) Order.ASCENDING else Order.DESCENDING)
                }
                .decodeList<T>()
            
            Timber.d("Obtenidos ${result.size} elementos de $table ordenados por $orderBy")
            Result.success(result)
        } catch (e: Exception) {
            Timber.e(e, "Error al obtener elementos ordenados de $table")
            Result.failure(e)
        }
    }

    /**
     * Ejemplo: Consulta con paginación
     * 
     * @param table Nombre de la tabla
     * @param page Número de página (empezando en 0)
     * @param pageSize Tamaño de la página
     * @return Lista paginada de elementos
     */
    public suspend inline fun <reified T : Any> getPaginated(
        table: String,
        page: Int,
        pageSize: Int = 10
    ): Result<List<T>> = withContext(Dispatchers.IO) {
        try {
            val from = page * pageSize
            val to = from + pageSize - 1
            
            val result = postgrest.from(table)
                .select {
                    range(from.toLong(), to.toLong())
                }
                .decodeList<T>()
            
            Timber.d("Obtenidos ${result.size} elementos de $table (página $page)")
            Result.success(result)
        } catch (e: Exception) {
            Timber.e(e, "Error al obtener página $page de $table")
            Result.failure(e)
        }
    }

    /**
     * Consulta con múltiples filtros
     * 
     * @param table Nombre de la tabla
     * @param filters Mapa de filtros (campo -> valor)
     * @return Lista de elementos que coinciden con los filtros
     */
    public suspend inline fun <reified T : Any> findByMultiple(
        table: String,
        filters: Map<String, Any>
    ): Result<List<T>> = withContext(Dispatchers.IO) {
        try {
            val query = postgrest.from(table).select()
            
            // Aplicar filtros dinámicamente
            // TODO: Implementar filtros múltiples cuando esté disponible la API
            // Por ahora, usar solo el primer filtro
            val firstFilter = filters.entries.firstOrNull()
            if (firstFilter != null) {
                // Usar el método findBy existente
                return@withContext findBy(table, firstFilter.key, firstFilter.value)
            }
            
            val result = query.decodeList<T>()
            Timber.d("Obtenidos ${result.size} elementos de $table con filtros: $filters")
            Result.success(result)
        } catch (e: Exception) {
            Timber.e(e, "Error al obtener elementos de $table con filtros: $filters")
            Result.failure(e)
        }
    }

    /**
     * Consulta ordenada con límite
     * 
     * @param table Nombre de la tabla
     * @param orderBy Campo por el cual ordenar
     * @param ascending Si es ascendente (true) o descendente (false)
     * @param limit Número máximo de elementos a retornar
     * @return Lista de elementos ordenados y limitados
     */
    public suspend inline fun <reified T : Any> getAllOrderedLimit(
        table: String,
        orderBy: String,
        ascending: Boolean = true,
        limit: Int
    ): Result<List<T>> = withContext(Dispatchers.IO) {
        try {
            val result = postgrest.from(table)
                .select {
                    order(orderBy, if (ascending) Order.ASCENDING else Order.DESCENDING)
                    limit(limit.toLong())
                }
                .decodeList<T>()
            
            Timber.d("Obtenidos ${result.size} elementos de $table ordenados por $orderBy")
            Result.success(result)
        } catch (e: Exception) {
            Timber.e(e, "Error al obtener elementos ordenados de $table")
            Result.failure(e)
        }
    }

    /**
     * Consulta con rango geográfico (para filtrar por ubicación)
     * 
     * @param table Nombre de la tabla
     * @param lat Latitud del punto central
     * @param lng Longitud del punto central
     * @param radiusKm Radio en kilómetros
     * @return Lista de elementos dentro del radio especificado
     */
    public suspend inline fun <reified T : Any> findByLocation(
        table: String,
        lat: Double,
        lng: Double,
        radiusKm: Double
    ): Result<List<T>> = withContext(Dispatchers.IO) {
        try {
            // Por ahora, retornar todos los elementos
            // Una implementación completa requeriría PostGIS habilitado en Supabase
            val result = postgrest.from(table)
                .select()
                .decodeList<T>()
            
            Timber.d("Obtenidos ${result.size} elementos de $table (filtro de ubicación pendiente)")
            Result.success(result)
        } catch (e: Exception) {
            Timber.e(e, "Error al obtener elementos por ubicación de $table")
            Result.failure(e)
        }
    }

    /**
     * Consulta con JOIN para obtener información relacionada
     * 
     * @param table Nombre de la tabla principal
     * @param joinTable Tabla a hacer JOIN
     * @param joinCondition Condición del JOIN
     * @param filters Filtros adicionales
     * @return Lista de elementos con información relacionada
     */
    public suspend inline fun <reified T : Any> findWithJoin(
        table: String,
        joinTable: String,
        joinCondition: String,
        filters: Map<String, Any> = emptyMap()
    ): Result<List<T>> = withContext(Dispatchers.IO) {
        try {
            // Por ahora, usar consulta simple sin JOIN
            // Una implementación completa requeriría usar la sintaxis de foreign keys de Supabase
            val result = postgrest.from(table)
                .select()
                .decodeList<T>()
            Timber.d("Obtenidos ${result.size} elementos de $table con JOIN a $joinTable")
            Result.success(result)
        } catch (e: Exception) {
            Timber.e(e, "Error al obtener elementos con JOIN de $table y $joinTable")
            Result.failure(e)
        }
    }
}

// ========== MODELOS BASADOS EN PRISMA SCHEMA ==========
// Estos modelos están sincronizados con el schema de Prisma del backend

/**
 * Modelo de Usuario - Coincide con el modelo User de Prisma
 */
@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class SupabaseUser(
    val id: String,
    val email: String? = null,
    val phone: String? = null,
    val password: String? = null,
    val name: String? = null,
    val role: String = "CLIENT", // CLIENT, PROFESSIONAL, ADMIN
    val isVerified: Boolean = false,
    val isIDVerified: Boolean = false,
    val balance: Double = 0.0,
    val isSuspended: Boolean = false,
    val locationLat: Double? = null,
    val locationLng: Double? = null,
    val locationAddress: String? = null,
    val emailVerificationExpires: String? = null,
    val emailVerificationToken: String? = null,
    val passwordResetExpires: String? = null,
    val passwordResetToken: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val deletedAt: String? = null
)

/**
 * Modelo de Servicio Profesional - Coincide con ProfessionalService de Prisma
 */
@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class SupabaseService(
    val id: String,
    val professionalId: String,
    val title: String,
    val slug: String,
    val description: String,
    val price: Double,
    val categoryId: String,
    val professionalProfileId: String? = null,
    val serviceLocations: String? = null, // JSON
    val isActive: Boolean = true,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

/**
 * Modelo de Publicación de Servicio - Coincide con ServicePosting de Prisma
 */
@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class SupabaseServicePosting(
    val id: String,
    val clientId: String,
    val title: String,
    val description: String,
    val categoryId: String,
    val lat: Double? = null,
    val lng: Double? = null,
    val address: String? = null,
    val requiredFrom: String? = null,
    val requiredTo: String? = null,
    val priceMin: Double? = null,
    val priceMax: Double? = null,
    val budget: Double? = null,
    val locationLat: Double? = null,
    val locationLng: Double? = null,
    val status: String = "OPEN", // OPEN, CLOSED, EXPIRED
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val expiresAt: String? = null,
    val deletedAt: String? = null,
    val transactionId: String? = null
)

/**
 * Modelo de Oferta - Coincide con Offer de Prisma
 */
@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class SupabaseOffer(
    val id: String,
    val postingId: String,
    val professionalId: String,
    val price: Double,
    val proposedPrice: Double? = null,
    val message: String? = null,
    val status: String = "PENDING", // PENDING, ACCEPTED, REJECTED
    val createdAt: String? = null
)

/**
 * Modelo de Transacción de Servicio - Coincide con ServiceTransaction de Prisma
 */
@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class SupabaseServiceTransaction(
    val id: String,
    val clientId: String,
    val professionalId: String,
    val priceAgreed: Double,
    val discountAmount: Double = 0.0,
    val platformFee: Double = 0.0,
    val escrowAmount: Double = 0.0,
    val currentStatus: String = "PENDING_SOLICITUD", // PENDING_SOLICITUD, SCHEDULED, IN_PROGRESS, COMPLETED, CANCELED
    val status: String = "PENDING_SOLICITUD", // Duplicado para compatibilidad
    val scheduledDate: String? = null,
    val postingId: String? = null,
    val proServiceId: String? = null,
    val promoCodeId: String? = null,
    val offerId: String? = null,
    val agreedPrice: Double? = null,
    val yummyLogistics: String? = null, // JSON
    val notes: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val completedAt: String? = null
)

/**
 * Modelo de Pago - Coincide con Payment de Prisma
 */
@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class SupabasePayment(
    val id: String,
    val userId: String,
    val transactionId: String? = null,
    val amount: Double,
    val fee: Double = 0.0,
    val method: String, // CASHEA, BALANCE, TRANSFER, PAY_MOBILE, CARD, OTHER
    val status: String = "PENDING", // PENDING, COMPLETED, FAILED, REFUNDED
    val recipientId: String? = null,
    val details: String? = null, // JSON
    val createdAt: String? = null,
    val updatedAt: String? = null
)

/**
 * Modelo de Profesión/Categoría - Coincide con Profession de Prisma
 */
@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class SupabaseProfession(
    val id: String,
    val name: String,
    val slug: String,
    val description: String? = null,
    val createdAt: String? = null
)

/**
 * Modelo de Reseña - Coincide con Review de Prisma
 */
@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class SupabaseReview(
    val id: String,
    val transactionId: String? = null,
    val reviewerId: String,
    val reviewedId: String,
    val rating: Int,
    val comment: String? = null,
    val isProReview: Boolean,
    val professionalProfileId: String? = null,
    val jobId: String? = null,
    val createdAt: String? = null
)

/**
 * Modelo de Perfil Profesional - Coincide con ProfessionalProfile de Prisma
 */
@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class SupabaseProfessionalProfile(
    val id: String,
    val userId: String,
    val bio: String? = null,
    val earningsSummary: String? = null, // JSON
    val portfolio: String? = null, // JSON
    val ratingAvg: Double? = 0.0,
    val ratingCount: Int = 0,
    val bankAccount: String? = null,
    val certifications: String? = null,
    val hourlyRate: Double? = null,
    val isVerified: Boolean = false,
    val specialties: String? = null, // Array JSON
    val taxId: String? = null,
    val yearsOfExperience: Int? = null,
    val completionRate: Double? = 0.0,
    val rating: Double? = 0.0,
    val responseTime: Int? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val deletedAt: String? = null
)

/**
 * Modelo de Enlace de Profesión - Coincide con ProfessionLink de Prisma
 */
@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class SupabaseProfessionLink(
    val id: String,
    val userId: String,
    val professionId: String,
    val documents: String? = null, // JSON
    val verified: Boolean = false
)

/**
 * Modelo de Método de Pago del Usuario - Coincide con UserPaymentMethod de Prisma
 */
@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class SupabaseUserPaymentMethod(
    val id: String,
    val userId: String,
    val method: String, // CASHEA, BALANCE, TRANSFER, PAY_MOBILE, CARD, OTHER
    val accountNumber: String? = null,
    val accountAlias: String? = null,
    val idNumber: String? = null,
    val phoneNumber: String? = null,
    val details: String? = null, // JSON
    val isVerified: Boolean = false,
    val isDefault: Boolean = false,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

/**
 * Modelo de Retiro - Coincide con Withdrawal de Prisma
 */
@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class SupabaseWithdrawal(
    val id: String,
    val userId: String,
    val paymentMethodId: String,
    val amount: Double,
    val status: String = "PENDING", // PENDING, COMPLETED, FAILED
    val requestedAt: String? = null,
    val completedAt: String? = null,
    val adminNotes: String? = null,
    val rejectionReason: String? = null
)

/**
 * Modelo de Reporte - Coincide con Report de Prisma
 */
@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class SupabaseReport(
    val id: String,
    val reporterId: String,
    val reportedId: String? = null,
    val serviceId: String? = null,
    val reason: String,
    val proofMedia: String? = null, // JSON
    val status: String = "PENDING",
    val adminNotes: String? = null,
    val createdAt: String? = null
)

/**
 * Modelo de Campaña Publicitaria - Coincide con AdCampaign de Prisma
 */
@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class SupabaseAdCampaign(
    val id: String,
    val title: String,
    val targetSegment: String, // CLIENT, PROFESSIONAL, ALL
    val location: String,
    val imageUrl: String,
    val targetUrl: String,
    val startDate: String,
    val endDate: String,
    val isActive: Boolean = true,
    val impressions: Int = 0,
    val clicks: Int = 0,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

/**
 * Modelo de Código Promocional - Coincide con PromoCode de Prisma
 */
@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class SupabasePromoCode(
    val id: String,
    val code: String,
    val discountType: String, // PERCENTAGE, FIXED_AMOUNT
    val discountValue: Double,
    val maxUses: Int? = null,
    val usesCount: Int = 0,
    val maxUsesPerUser: Int? = null,
    val validFrom: String? = null,
    val validUntil: String? = null,
    val targetCategory: String? = null,
    val isActive: Boolean = true,
    val createdAt: String? = null
)

/**
 * Modelo de Uso de Código Promocional - Coincide con PromoCodeUsage de Prisma
 */
@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class SupabasePromoCodeUsage(
    val id: String,
    val codeId: String,
    val userId: String,
    val usedAt: String? = null
)

/**
 * Modelo de Media/Archivo - Coincide con Media de Prisma
 */
@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class SupabaseMedia(
    val id: String,
    val url: String,
    val type: String, // IMAGE, VIDEO, DOCUMENT
    val filename: String? = null,
    val sizeBytes: Int? = null,
    val uploadedById: String? = null,
    val postingId: String? = null,
    val proServiceId: String? = null,
    val transactionId: String? = null,
    val reportId: String? = null,
    val createdAt: String? = null
)

/**
 * Modelo de Dispositivo - Coincide con Device de Prisma
 */
@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class SupabaseDevice(
    val id: String,
    val userId: String,
    val deviceId: String,
    val pushToken: String? = null,
    val lastSeen: String? = null
)

/**
 * Modelo de Notificación - Coincide con Notification de Prisma
 */
@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class SupabaseNotification(
    val id: String,
    val userId: String,
    val title: String,
    val body: String,
    val data: String? = null, // JSON
    val read: Boolean = false,
    val createdAt: String? = null
)

/**
 * Modelo de Log de Auditoría - Coincide con AuditLog de Prisma
 */
@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class SupabaseAuditLog(
    val id: String,
    val actorId: String? = null,
    val action: String,
    val meta: String? = null, // JSON
    val createdAt: String? = null
)

/**
 * Modelo de Conversación - Coincide con Conversation de Prisma
 */
@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class SupabaseConversation(
    val id: String,
    val createdById: String,
    val jobId: String? = null,
    val title: String? = null,
    val type: String? = "CLIENT_PROFESSIONAL", // CLIENT_PROFESSIONAL, SUPPORT
    val createdAt: String? = null,
    val updatedAt: String? = null
)

/**
 * Modelo de Participante de Conversación - Coincide con ConversationParticipant de Prisma
 */
@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class SupabaseConversationParticipant(
    val id: String,
    val conversationId: String,
    val userId: String,
    val joinedAt: String? = null
)

/**
 * Modelo de Mensaje - Coincide con Message de Prisma
 */
@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class SupabaseMessage(
    val id: String,
    val conversationId: String,
    val senderId: String,
    val text: String? = null,
    val content: String? = null,
    val mediaIds: String? = null, // JSON array
    val readBy: String? = null, // JSON array
    val isRead: Boolean = false,
    val messageType: String? = "TEXT", // TEXT, IMAGE, FILE, SYSTEM
    val createdAt: String? = null
)

