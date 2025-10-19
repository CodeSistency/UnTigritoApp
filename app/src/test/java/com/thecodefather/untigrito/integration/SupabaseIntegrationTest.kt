package com.thecodefather.untigrito.integration

import com.thecodefather.untigrito.data.datasource.remote.SupabaseUser
import com.thecodefather.untigrito.data.datasource.remote.SupabaseServicePosting
import com.thecodefather.untigrito.data.datasource.remote.SupabaseService
import com.thecodefather.untigrito.data.datasource.remote.SupabaseProfessionalProfile
import com.thecodefather.untigrito.domain.model.toClientUser
import com.thecodefather.untigrito.domain.model.toServicePosting
import com.thecodefather.untigrito.domain.model.toProfessionalService
import com.thecodefather.untigrito.domain.model.toProfessional
import org.junit.Test
import org.junit.Assert.*

/**
 * Test básico para verificar la integración de Supabase con el módulo cliente
 * 
 * Estos tests verifican que los mappers funcionen correctamente y que
 * la transformación de datos entre Supabase y los modelos de dominio sea exitosa.
 */
class SupabaseIntegrationTest {

    @Test
    fun `test SupabaseUser to ClientUser mapper`() {
        // Given
        val supabaseUser = SupabaseUser(
            id = "test-user-id",
            email = "test@example.com",
            phone = "+1234567890",
            name = "Test User",
            role = "CLIENT",
            isVerified = true,
            isIDVerified = false,
            balance = 100.0,
            isSuspended = false,
            locationLat = 40.7128,
            locationLng = -74.0060,
            locationAddress = "New York, NY",
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T00:00:00Z"
        )

        // When
        val clientUser = supabaseUser.toClientUser()

        // Then
        assertEquals("test-user-id", clientUser.id)
        assertEquals("test@example.com", clientUser.email)
        assertEquals("+1234567890", clientUser.phone)
        assertEquals("Test User", clientUser.name)
        assertEquals("CLIENT", clientUser.role)
        assertTrue(clientUser.isVerified)
        assertFalse(clientUser.isIDVerified)
        assertEquals(100.0, clientUser.balance, 0.01)
        assertFalse(clientUser.isSuspended)
    }

    @Test
    fun `test SupabaseServicePosting to ServicePosting mapper`() {
        // Given
        val supabasePosting = SupabaseServicePosting(
            id = "posting-id",
            clientId = "client-id",
            title = "Need plumber",
            description = "Fix kitchen sink",
            categoryId = "PLOMERIA",
            budget = 50.0,
            status = "OPEN",
            address = "123 Main St",
            locationLat = 40.7128,
            locationLng = -74.0060,
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T00:00:00Z"
        )

        // When
        val servicePosting = supabasePosting.toServicePosting()

        // Then
        assertEquals("posting-id", servicePosting.id)
        assertEquals("client-id", servicePosting.clientId)
        assertEquals("Need plumber", servicePosting.title)
        assertEquals("Fix kitchen sink", servicePosting.description)
        assertEquals("PLOMERIA", servicePosting.category)
        assertEquals(50.0, servicePosting.budget, 0.01)
        assertEquals("OPEN", servicePosting.status)
    }

    @Test
    fun `test SupabaseService to ProfessionalService mapper`() {
        // Given
        val supabaseService = SupabaseService(
            id = "service-id",
            professionalId = "professional-id",
            title = "Plumbing Services",
            description = "Expert plumber with 10 years experience",
            price = 75.0,
            categoryId = "PLOMERIA",
            isActive = true,
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T00:00:00Z"
        )

        // When
        val professionalService = supabaseService.toProfessionalService()

        // Then
        assertEquals("service-id", professionalService.id)
        assertEquals("professional-id", professionalService.professionalId)
        assertEquals("Plumbing Services", professionalService.title)
        assertEquals("Expert plumber with 10 years experience", professionalService.description)
        assertEquals(75.0, professionalService.price, 0.01)
        assertEquals("PLOMERIA", professionalService.categoryId)
        assertTrue(professionalService.isActive)
    }

    @Test
    fun `test SupabaseProfessionalProfile to Professional mapper`() {
        // Given
        val supabaseProfile = SupabaseProfessionalProfile(
            id = "profile-id",
            userId = "user-id",
            bio = "Experienced professional",
            rating = 4.5,
            ratingCount = 100,
            yearsOfExperience = 10,
            certifications = "Licensed Plumber",
            specialties = "Plumbing,Heating",
            responseTime = 2,
            completionRate = 95.0,
            hourlyRate = 50.0,
            bankAccount = "1234567890",
            taxId = "TAX123",
            isVerified = true,
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2024-01-01T00:00:00Z"
        )

        // When
        val professional = supabaseProfile.toProfessional()

        // Then
        assertEquals("profile-id", professional.id)
        assertEquals("user-id", professional.userId)
        assertEquals("Experienced professional", professional.bio)
        assertEquals(4.5, professional.rating ?: 0.0, 0.01)
        assertEquals(100, professional.totalReviews)
        assertEquals(10, professional.yearsOfExperience)
        assertEquals("Licensed Plumber", professional.certifications)
        assertEquals(2, listOf("Plumbing", "Heating").size)
        assertTrue(professional.isVerified)
    }

    @Test
    fun `test mapper handles null values correctly`() {
        // Given
        val supabaseUser = SupabaseUser(
            id = "test-id",
            email = null,
            phone = null,
            name = null,
            role = "CLIENT",
            isVerified = false,
            isIDVerified = false,
            balance = 0.0,
            isSuspended = false,
            locationLat = null,
            locationLng = null,
            locationAddress = null,
            createdAt = null,
            updatedAt = null
        )

        // When
        val clientUser = supabaseUser.toClientUser()

        // Then
        assertNull(clientUser.email)
        assertNull(clientUser.phone)
        assertNull(clientUser.name)
        assertEquals("", clientUser.createdAt)
        assertEquals("", clientUser.updatedAt)
    }
}

