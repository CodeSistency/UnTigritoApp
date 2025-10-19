package com.thecodefather.untigrito.data.datasource.remote

import android.content.Context
import android.net.Uri
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Servicio para gestión de archivos en Supabase Storage
 */
@Singleton
class SupabaseStorageService @Inject constructor(
    private val storage: Storage
) {

    companion object {
        private const val PROFILE_IMAGES_BUCKET = "profile-images"
        private const val IDENTITY_DOCUMENTS_BUCKET = "tigritoBucket"
        private const val MAX_IMAGE_SIZE_BYTES = 5 * 1024 * 1024 // 5MB
    }

    /**
     * Sube una imagen de perfil y retorna la URL pública
     * 
     * @param userId ID del usuario
     * @param imageUri URI de la imagen seleccionada
     * @return URL pública de la imagen subida
     */
    suspend fun uploadProfileImage(userId: String, imageUri: Uri, context: Context): Result<String> =
        withContext(Dispatchers.IO) {
            try {
                Timber.d("📤 STORAGE - Uploading profile image for user: $userId")
                
                // Generar nombre único para el archivo
                val fileName = "profile_${userId}_${System.currentTimeMillis()}.jpg"
                val filePath = "users/$userId/$fileName"
                
                // Leer el archivo desde URI
                val inputStream = context.contentResolver.openInputStream(imageUri)
                    ?: return@withContext Result.failure(Exception("No se pudo leer la imagen"))
                
                // Verificar tamaño del archivo
                val fileSize = inputStream.available()
                if (fileSize > MAX_IMAGE_SIZE_BYTES) {
                    return@withContext Result.failure(
                        Exception("La imagen es demasiado grande. Máximo 5MB")
                    )
                }
                
                // Convertir InputStream a ByteArray
                val byteArray = inputStream.readBytes()
                inputStream.close()
                
                // Subir archivo al bucket
                storage.from(PROFILE_IMAGES_BUCKET)
                    .upload(filePath, byteArray, upsert = true)
                
                // Obtener URL pública
                val publicUrl = storage.from(PROFILE_IMAGES_BUCKET)
                    .createSignedUrl(filePath, kotlin.time.Duration.parse("P365D")) // 1 año
                
                Timber.d("✅ STORAGE - Profile image uploaded successfully: $publicUrl")
                Result.success(publicUrl)
                
            } catch (e: Exception) {
                Timber.e(e, "❌ STORAGE - Error uploading profile image")
                Result.failure(e)
        }
    }

    /**
     * Sube un documento de identidad (cédula) y retorna la URL pública
     * 
     * @param userId ID del usuario
     * @param imageUri URI de la imagen seleccionada
     * @param context Contexto de Android
     * @return URL pública de la imagen subida
     */
    suspend fun uploadIdentityDocument(
        userId: String, 
        imageUri: Uri, 
        context: Context
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            Timber.d("📤 STORAGE - Uploading identity document for user: $userId")
            
            val fileName = "cedula_${userId}_${System.currentTimeMillis()}.jpg"
            val filePath = "identity-documents/$userId/$fileName"
            
            val inputStream = context.contentResolver.openInputStream(imageUri)
                ?: return@withContext Result.failure(Exception("No se pudo leer la imagen"))
            
            val fileSize = inputStream.available()
            if (fileSize > MAX_IMAGE_SIZE_BYTES) {
                return@withContext Result.failure(
                    Exception("La imagen es demasiado grande. Máximo 5MB")
                )
            }
            
            val byteArray = inputStream.readBytes()
            inputStream.close()
            
            // Subir a bucket tigritoBucket
            storage.from(IDENTITY_DOCUMENTS_BUCKET)
                .upload(filePath, byteArray, upsert = true)
            
            val publicUrl = storage.from(IDENTITY_DOCUMENTS_BUCKET)
                .createSignedUrl(filePath, kotlin.time.Duration.parse("P365D"))
            
            Timber.d("✅ STORAGE - Identity document uploaded: $publicUrl")
            Result.success(publicUrl)
            
        } catch (e: Exception) {
            Timber.e(e, "❌ STORAGE - Error uploading identity document")
            Result.failure(e)
        }
    }

    /**
     * Elimina una imagen de perfil
     * 
     * @param imageUrl URL de la imagen a eliminar
     * @return Resultado de la operación
     */
    suspend fun deleteProfileImage(imageUrl: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                Timber.d("🗑️ STORAGE - Deleting profile image: $imageUrl")
                
                // Extraer el path del archivo desde la URL
                val filePath = extractFilePathFromUrl(imageUrl)
                    ?: return@withContext Result.failure(Exception("URL de imagen no válida"))
                
                // Eliminar archivo del bucket
                storage.from(PROFILE_IMAGES_BUCKET)
                    .delete(listOf(filePath))
                
                Timber.d("✅ STORAGE - Profile image deleted successfully")
                Result.success(Unit)
                
            } catch (e: Exception) {
                Timber.e(e, "❌ STORAGE - Error deleting profile image")
                Result.failure(e)
            }
        }

    /**
     * Obtiene la URL de la imagen de perfil de un usuario
     * 
     * @param userId ID del usuario
     * @return URL de la imagen de perfil o null si no existe
     */
    suspend fun getProfileImageUrl(userId: String): String? =
        withContext(Dispatchers.IO) {
            try {
                Timber.d("🔍 STORAGE - Getting profile image for user: $userId")
                
                // Listar archivos en el directorio del usuario
                val files = storage.from(PROFILE_IMAGES_BUCKET)
                    .list("users/$userId")
                
                // Buscar la imagen más reciente
                val profileImages = files.filter { 
                    it.name.startsWith("profile_${userId}_") && it.name.endsWith(".jpg")
                }
                
                if (profileImages.isNotEmpty()) {
                    // Ordenar por fecha de modificación (más reciente primero)
                    val latestImage = profileImages.maxByOrNull { it.updatedAt ?: kotlinx.datetime.Clock.System.now() }
                    val filePath = "users/$userId/${latestImage?.name}"
                    
                    // Crear URL firmada
                    val publicUrl = storage.from(PROFILE_IMAGES_BUCKET)
                        .createSignedUrl(filePath, kotlin.time.Duration.parse("P365D")) // 1 año
                    
                    Timber.d("✅ STORAGE - Profile image found: $publicUrl")
                    publicUrl
                } else {
                    Timber.d("ℹ️ STORAGE - No profile image found for user: $userId")
                    null
                }
                
            } catch (e: Exception) {
                Timber.e(e, "❌ STORAGE - Error getting profile image")
                null
            }
        }

    /**
     * Extrae el path del archivo desde una URL de Supabase Storage
     */
    private fun extractFilePathFromUrl(url: String): String? {
        return try {
            // La URL de Supabase Storage tiene el formato:
            // https://[project].supabase.co/storage/v1/object/public/[bucket]/[path]
            val parts = url.split("/storage/v1/object/public/")
            if (parts.size == 2) {
                val bucketAndPath = parts[1]
                val pathParts = bucketAndPath.split("/", limit = 2)
                if (pathParts.size == 2) {
                    pathParts[1] // Retorna solo el path sin el bucket
                } else null
            } else null
        } catch (e: Exception) {
            Timber.e(e, "Error extracting file path from URL: $url")
            null
        }
    }

    /**
     * Verifica si una URL es válida para Supabase Storage
     */
    fun isValidStorageUrl(url: String): Boolean {
        return url.contains("supabase.co/storage/v1/object/public/")
    }
}
