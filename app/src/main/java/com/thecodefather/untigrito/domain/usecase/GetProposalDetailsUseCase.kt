package com.thecodefather.untigrito.domain.usecase

import com.thecodefather.untigrito.domain.model.Proposal
import com.thecodefather.untigrito.domain.repository.ProposalsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Caso de uso para obtener los detalles de una propuesta específica
 */
class GetProposalDetailsUseCase @Inject constructor(
    private val repository: ProposalsRepository
) {
    suspend operator fun invoke(proposalId: String): Result<Proposal?> {
        return try {
            Result.success(repository.getProposalById(proposalId).first())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
