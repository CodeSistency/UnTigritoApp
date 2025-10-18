package com.thecodefather.untigrito.presentation.viewmodel

/**
 * Eventos que emite ProposalsListViewModel
 */
sealed class ProposalsListEvent {
    data class NavigateToDetail(val proposalId: String) : ProposalsListEvent()
    data class ShowError(val message: String) : ProposalsListEvent()
    data class ProposalCancelled(val proposalId: String) : ProposalsListEvent()
}
