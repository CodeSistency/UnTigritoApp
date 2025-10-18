package com.thecodefather.untigrito.presentation.screens.client

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.thecodefather.untigrito.domain.model.ClientRequest
import com.thecodefather.untigrito.presentation.viewmodel.ServiceDetailViewModel

/**
 * Service Detail Screen
 * Displays full service details and offers received from professionals
 */
@Composable
fun ServiceDetailScreen(
    serviceId: String,
    navController: NavController,
    viewModel: ServiceDetailViewModel = hiltViewModel()
) {
    val service by viewModel.service.collectAsState(initial = null)
    val offers by viewModel.offers.collectAsState(initial = emptyList())
    val loading by viewModel.loading.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp),
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Service Details Card
        item {
            service?.let { serv ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = serv.title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Text(
                            text = "Categoría: ${serv.category}",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )

                        Text(
                            text = serv.description,
                            fontSize = 13.sp,
                            color = Color.DarkGray
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Presupuesto", fontSize = 11.sp, color = Color.Gray)
                                Text(
                                    "$${"%.2f".format(serv.budget)}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE67822)
                                )
                            }

                            Column {
                                Text("Estado", fontSize = 11.sp, color = Color.Gray)
                                Text(
                                    serv.status,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE67822)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Offers Section Header
        item {
            Text(
                text = "Ofertas Recibidas (${offers.size})",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Offers List
        items(offers) { offer ->
            OfferCard(
                offer = offer,
                onAccept = { viewModel.acceptOffer(offer.id) },
                onReject = { viewModel.rejectOffer(offer.id) }
            )
        }

        // Empty Offers State
        if (offers.isEmpty() && !loading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aún no hay ofertas para este servicio",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
private fun OfferCard(
    offer: ClientRequest,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Profesional ID: ${offer.professionalId?.take(8) ?: "N/A"}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                Box(
                    modifier = Modifier
                        .background(
                            color = when (offer.status) {
                                ClientRequest.STATUS_PENDING -> Color(0xFFE67822).copy(alpha = 0.1f)
                                ClientRequest.STATUS_ACCEPTED -> Color.Green.copy(alpha = 0.1f)
                                else -> Color.Red.copy(alpha = 0.1f)
                            },
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = offer.status,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (offer.status) {
                            ClientRequest.STATUS_PENDING -> Color(0xFFE67822)
                            ClientRequest.STATUS_ACCEPTED -> Color.Green
                            else -> Color.Red
                        }
                    )
                }
            }

            Text(
                text = offer.description,
                fontSize = 12.sp,
                color = Color.DarkGray
            )

            Text(
                text = "Presupuesto Propuesto: $${"%.2f".format(offer.proposedPrice)}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE67822)
            )

            // Action Buttons
            if (offer.status == ClientRequest.STATUS_PENDING) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onAccept,
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE67822)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Aceptar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onReject,
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE67822).copy(alpha = 0.2f)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Rechazar", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE67822))
                    }
                }
            }
        }
    }
}
