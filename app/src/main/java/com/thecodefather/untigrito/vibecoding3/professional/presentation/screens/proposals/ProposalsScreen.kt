package com.thecodefather.untigrito.vibecoding3.professional.presentation.screens.proposals

import androidx.compose.runtime.Composable

@Composable
fun ProposalsListScreen(
    onProposalClick: (String) -> Unit = {},
    onNavigateToJobs: () -> Unit = {}
) {
    // TODO: Listar propuestas
}

@Composable
fun ProposalDetailScreen(
    proposalId: String = "",
    onBackClick: () -> Unit = {},
    onChatClick: (String) -> Unit = {}
) {
    // TODO: Detalles de propuesta
}
