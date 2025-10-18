package com.thecodefather.untigrito.domain.repository

import com.thecodefather.untigrito.domain.model.Proposal
import com.thecodefather.untigrito.domain.model.ProposalFilter
import kotlinx.coroutines.flow.Flow

interface ProposalsRepository {
    suspend fun getProposals(filter: ProposalFilter): Flow<List<Proposal>>
    suspend fun getProposalById(proposalId: String): Flow<Proposal?>
    suspend fun createProposal(
        jobId: String,
        proposedPrice: Double,
        description: String,
        estimatedDuration: Int,
        includesMaterials: Boolean,
        offersWarranty: Boolean,
        termsAndConditions: String?
    ): Flow<Result<Proposal>>
    suspend fun withdrawProposal(proposalId: String): Flow<Result<Boolean>>
    fun cancelProposal(proposalId: String): Result<Boolean>
}