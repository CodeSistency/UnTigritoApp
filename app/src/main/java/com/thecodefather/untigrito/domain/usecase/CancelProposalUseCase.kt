package com.thecodefather.untigrito.domain.usecase

import com.thecodefather.untigrito.domain.repository.ProposalsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Caso de uso para cancelar una propuesta
 */
class CancelProposalUseCase @Inject constructor(
    private val repository: ProposalsRepository
) {
    suspend operator fun invoke(proposalId: String): Result<Boolean> {
        return repository.cancelProposal(proposalId)
    }
}
