package com.thecodefather.untigrito.domain.usecase

import com.thecodefather.untigrito.domain.model.Proposal
import com.thecodefather.untigrito.domain.repository.ProposalsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Caso de uso para crear una nueva propuesta
 */
class CreateProposalUseCase @Inject constructor(
    private val repository: ProposalsRepository
) {
    suspend operator fun invoke(
        jobId: String,
        description: String,
        proposedPrice: Double,
        estimatedDuration: Int = 1,
        includesMaterials: Boolean = false,
        offersWarranty: Boolean = false,
        termsAndConditions: String? = null
    ): Result<Proposal> {
        return try {
            repository.createProposal(
                jobId = jobId,
                proposedPrice = proposedPrice,
                description = description,
                estimatedDuration = estimatedDuration,
                includesMaterials = includesMaterials,
                offersWarranty = offersWarranty,
                termsAndConditions = termsAndConditions
            ).first()
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
