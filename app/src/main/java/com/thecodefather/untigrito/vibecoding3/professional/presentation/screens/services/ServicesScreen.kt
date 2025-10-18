package com.thecodefather.untigrito.vibecoding3.professional.presentation.screens.services

import androidx.compose.runtime.Composable

@Composable
fun MyServicesListScreen(
    onServiceClick: (String) -> Unit = {},
    onCreateServiceClick: () -> Unit = {}
) {
    // TODO: Listado de mis servicios
}

@Composable
fun ServiceDetailScreen(
    serviceId: String = "",
    onBackClick: () -> Unit = {},
    onEditClick: () -> Unit = {}
) {
    // TODO: Detalles del servicio
}

@Composable
fun CreateEditServiceScreen(
    serviceId: String? = null,
    onBackClick: () -> Unit = {},
    onServiceSaved: () -> Unit = {}
) {
    // TODO: Formulario crear/editar servicio
}
