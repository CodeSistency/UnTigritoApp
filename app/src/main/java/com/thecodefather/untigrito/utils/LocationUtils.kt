package com.thecodefather.untigrito.utils

import kotlin.math.*

object LocationUtils {
    
    /**
     * Calcula la distancia entre dos puntos geográficos usando la fórmula de Haversine
     * @param lat1 Latitud del primer punto
     * @param lng1 Longitud del primer punto
     * @param lat2 Latitud del segundo punto
     * @param lng2 Longitud del segundo punto
     * @return Distancia en kilómetros como string
     */
    fun calculateDistance(
        lat1: Double?, lng1: Double?,
        lat2: Double?, lng2: Double?
    ): String {
        if (lat1 == null || lng1 == null || lat2 == null || lng2 == null) {
            return "N/A"
        }
        
        val earthRadius = 6371.0 // Radio de la Tierra en kilómetros
        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)
        
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLng / 2) * sin(dLng / 2)
        
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        val distance = earthRadius * c
        
        return "${distance.roundToInt()} km"
    }
    
    /**
     * Calcula la distancia entre dos puntos y retorna un valor numérico
     * @param lat1 Latitud del primer punto
     * @param lng1 Longitud del primer punto
     * @param lat2 Latitud del segundo punto
     * @param lng2 Longitud del segundo punto
     * @return Distancia en kilómetros
     */
    fun calculateDistanceInKm(
        lat1: Double?, lng1: Double?,
        lat2: Double?, lng2: Double?
    ): Double {
        if (lat1 == null || lng1 == null || lat2 == null || lng2 == null) {
            return Double.MAX_VALUE
        }
        
        val earthRadius = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)
        
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLng / 2) * sin(dLng / 2)
        
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadius * c
    }
    
    /**
     * Formatea la distancia de manera legible
     * @param distanceKm Distancia en kilómetros
     * @return String formateado
     */
    fun formatDistance(distanceKm: Double): String {
        return when {
            distanceKm < 1.0 -> "${(distanceKm * 1000).roundToInt()} m"
            distanceKm < 10.0 -> "${distanceKm.roundToInt()} km"
            else -> "${distanceKm.roundToInt()} km"
        }
    }
}
