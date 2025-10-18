package com.thecodefather.untigrito.domain.usecase

import com.thecodefather.untigrito.domain.model.Proposal
import com.thecodefather.untigrito.domain.model.ProposalFilter
import com.thecodefather.untigrito.domain.repository.ProposalsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Caso de uso para obtener la lista de propuestas
 */
class GetProposalsUseCase @Inject constructor(
    private val repository: ProposalsRepository
) {
    suspend operator fun invoke(
        filter: ProposalFilter = ProposalFilter.OPEN
    ): Result<List<Proposal>> {
        return try {
            Result.success(repository.getProposals(filter).first())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
