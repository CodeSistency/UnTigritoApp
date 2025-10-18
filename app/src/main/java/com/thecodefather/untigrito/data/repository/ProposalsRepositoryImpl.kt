package com.thecodefather.untigrito.data.repository

import com.thecodefather.untigrito.domain.model.*
import com.thecodefather.untigrito.domain.repository.ProposalsRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProposalsRepositoryImpl @Inject constructor() : ProposalsRepository {

    private val dummyProposals = listOf(
        Proposal(
            id = "prop1",
            jobId = "1",
            jobTitle = "Reparación de grifo en cocina",
            clientId = "client1",
            clientName = "María González",
            clientAvatar = null,
            proposedPrice = 120.0,
            description = "Tengo experiencia en reparaciones de plomería. Puedo solucionar el problema del grifo y revisar otras conexiones.",
            estimatedDuration = 2,
            includesMaterials = true,
            offersWarranty = true,
            termsAndConditions = "Garantía de 30 días en la reparación. Materiales incluidos.",
            status = ProposalStatus.PENDING,
            createdAt = Date(System.currentTimeMillis() - 2 * 60 * 60 * 1000L),
            updatedAt = Date(System.currentTimeMillis() - 2 * 60 * 60 * 1000L)
        ),
        Proposal(
            id = "prop2",
            jobId = "2",
            jobTitle = "Instalación de aire acondicionado",
            clientId = "client2",
            clientName = "Carlos Rodríguez",
            clientAvatar = null,
            proposedPrice = 280.0,
            description = "Especialista en instalaciones de aire acondicionado. Trabajo limpio y profesional.",
            estimatedDuration = 4,
            includesMaterials = false,
            offersWarranty = true,
            termsAndConditions = "Garantía de 6 meses en la instalación. Certificado de instalación incluido.",
            status = ProposalStatus.ACCEPTED,
            createdAt = Date(System.currentTimeMillis() - 1 * 24 * 60 * 60 * 1000L),
            updatedAt = Date(System.currentTimeMillis() - 12 * 60 * 60 * 1000L),
            responseMessage = "¡Excelente! Aceptamos tu propuesta. Te contactaremos pronto.",
            responseDate = Date(System.currentTimeMillis() - 12 * 60 * 60 * 1000L)
        ),
        Proposal(
            id = "prop3",
            jobId = "3",
            jobTitle = "Pintura de apartamento",
            clientId = "client3",
            clientName = "Ana Martínez",
            clientAvatar = null,
            proposedPrice = 450.0,
            description = "Pintor profesional con 10 años de experiencia. Trabajo de calidad garantizada.",
            estimatedDuration = 3,
            includesMaterials = false,
            offersWarranty = true,
            termsAndConditions = "Garantía de 1 año en el trabajo de pintura. Limpieza incluida.",
            status = ProposalStatus.REJECTED,
            createdAt = Date(System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000L),
            updatedAt = Date(System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000L),
            responseMessage = "Gracias por tu propuesta, pero hemos elegido otra opción.",
            responseDate = Date(System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000L)
        ),
        Proposal(
            id = "prop4",
            jobId = "4",
            jobTitle = "Reparación de nevera",
            clientId = "client4",
            clientName = "Roberto Silva",
            clientAvatar = null,
            proposedPrice = 180.0,
            description = "Técnico especializado en refrigeración. Diagnóstico gratuito incluido.",
            estimatedDuration = 2,
            includesMaterials = true,
            offersWarranty = true,
            termsAndConditions = "Garantía de 90 días en la reparación. Repuestos originales.",
            status = ProposalStatus.IN_PROGRESS,
            createdAt = Date(System.currentTimeMillis() - 5 * 24 * 60 * 60 * 1000L),
            updatedAt = Date(System.currentTimeMillis() - 1 * 24 * 60 * 60 * 1000L)
        ),
        Proposal(
            id = "prop5",
            jobId = "5",
            jobTitle = "Instalación de cerradura digital",
            clientId = "client5",
            clientName = "Isabel Torres",
            clientAvatar = null,
            proposedPrice = 80.0,
            description = "Instalación profesional de cerraduras digitales. Configuración incluida.",
            estimatedDuration = 1,
            includesMaterials = false,
            offersWarranty = true,
            termsAndConditions = "Garantía de 1 año en la instalación. Capacitación en el uso incluida.",
            status = ProposalStatus.COMPLETED,
            createdAt = Date(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000L),
            updatedAt = Date(System.currentTimeMillis() - 1 * 24 * 60 * 60 * 1000L),
            responseMessage = "Trabajo completado exitosamente. ¡Gracias!",
            responseDate = Date(System.currentTimeMillis() - 1 * 24 * 60 * 60 * 1000L)
        )
    )

    override suspend fun getProposals(filter: ProposalFilter): Flow<List<Proposal>> = flow {
        delay(800)
        
        val filteredProposals = when (filter) {
            ProposalFilter.OPEN -> dummyProposals.filter { it.status == ProposalStatus.PENDING }
            ProposalFilter.IN_PROGRESS -> dummyProposals.filter { 
                it.status == ProposalStatus.ACCEPTED || it.status == ProposalStatus.IN_PROGRESS 
            }
            ProposalFilter.COMPLETED -> dummyProposals.filter { it.status == ProposalStatus.COMPLETED }
            ProposalFilter.REJECTED -> dummyProposals.filter { it.status == ProposalStatus.REJECTED }
            ProposalFilter.HISTORY -> dummyProposals.filter { 
                it.status == ProposalStatus.COMPLETED || it.status == ProposalStatus.REJECTED 
            }
        }
        
        emit(filteredProposals.sortedByDescending { it.createdAt })
    }

    override suspend fun getProposalById(proposalId: String): Flow<Proposal?> = flow {
        delay(500)
        emit(dummyProposals.find { it.id == proposalId })
    }

    override suspend fun createProposal(
        jobId: String,
        proposedPrice: Double,
        description: String,
        estimatedDuration: Int,
        includesMaterials: Boolean,
        offersWarranty: Boolean,
        termsAndConditions: String?
    ): Flow<Result<Proposal>> = flow {
        delay(1000)
        
        val newProposal = Proposal(
            id = "prop_${System.currentTimeMillis()}",
            jobId = jobId,
            jobTitle = "Trabajo #$jobId",
            clientId = "client_$jobId",
            clientName = "Cliente",
            clientAvatar = null,
            proposedPrice = proposedPrice,
            description = description,
            estimatedDuration = estimatedDuration,
            includesMaterials = includesMaterials,
            offersWarranty = offersWarranty,
            termsAndConditions = termsAndConditions,
            status = ProposalStatus.PENDING,
            createdAt = Date(),
            updatedAt = Date()
        )
        
        emit(Result.success(newProposal))
    }

    override suspend fun withdrawProposal(proposalId: String): Flow<Result<Boolean>> = flow {
        delay(500)
        emit(Result.success(true))
    }

    override fun cancelProposal(proposalId: String): Result<Boolean> {
        return try {
            // Simular cancelación de propuesta
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
