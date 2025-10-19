package com.thecodefather.untigrito.utils

import com.thecodefather.untigrito.data.datasource.remote.ServiceWithProfessional
import com.thecodefather.untigrito.domain.model.Service

/**
 * Helper class for service-related operations
 */
object ServiceHelper {
    
    /**
     * Extracts professional name from ServiceWithProfessional
     */
    fun getProfessionalName(serviceWithProf: ServiceWithProfessional?): String {
        return serviceWithProf?.professional?.name ?: "Profesional"
    }
    
    /**
     * Calculates distance between user and service location
     */
    fun calculateServiceDistance(
        userLat: Double?,
        userLng: Double?,
        serviceWithProf: ServiceWithProfessional?
    ): String {
        val serviceLat = serviceWithProf?.professional?.locationLat
        val serviceLng = serviceWithProf?.professional?.locationLng
        
        return LocationUtils.calculateDistance(userLat, userLng, serviceLat, serviceLng)
    }
    
    /**
     * Gets service rating from service data
     */
    fun getServiceRating(service: Service): Double {
        return service.rating
    }
    
    /**
     * Gets service review count
     */
    fun getServiceReviewCount(service: Service): Int {
        return service.reviewCount
    }
    
    /**
     * Formats price for display
     */
    fun formatPrice(price: Double): String {
        return "$${price.toInt()}"
    }
    
    /**
     * Gets category display name
     */
    fun getCategoryDisplayName(categoryId: String): String {
        return when (categoryId.uppercase()) {
            "PLOMERIA" -> "Plomería"
            "ELECTRICIDAD" -> "Electricidad"
            "ALBANILERIA" -> "Albañilería"
            "LIMPIEZA" -> "Limpieza"
            "MUDANZA" -> "Mudanza"
            else -> categoryId
        }
    }
}
