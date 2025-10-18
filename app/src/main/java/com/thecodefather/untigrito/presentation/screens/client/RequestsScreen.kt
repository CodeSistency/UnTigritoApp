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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.thecodefather.untigrito.domain.model.ClientRequest
import com.thecodefather.untigrito.presentation.components.ClientBottomNavBar
import com.thecodefather.untigrito.presentation.navigation.Routes
import com.thecodefather.untigrito.presentation.viewmodel.RequestsViewModel
import com.thecodefather.untigrito.presentation.screens.client.components.HomeHeader
import com.thecodefather.untigrito.presentation.screens.client.components.OrangeUntigrito
import com.thecodefather.untigrito.presentation.screens.client.components.PrimaryTextColor
import com.thecodefather.untigrito.presentation.screens.client.components.SecondaryTextColor

/**
 * Requests Screen
 * Shows client requests filtered by status: Pending, Active, Completed
 */
@Composable
fun RequestsScreen(
    navController: NavController,
    viewModel: RequestsViewModel = hiltViewModel()
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    val pendingRequests by viewModel.pendingRequests.collectAsState(initial = emptyList())
    val activeRequests by viewModel.activeRequests.collectAsState(initial = emptyList())
    val completedRequests by viewModel.completedRequests.collectAsState(initial = emptyList())
    val loading by viewModel.loading.collectAsState()

    val tabs = listOf("Pendientes", "Activas", "Historial")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        HomeHeader(userName = "Juan Pérez")
        // Tab Row
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = OrangeUntigrito,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    height = 3.dp,
                    color = OrangeUntigrito
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            title,
                            fontSize = 14.sp,
                            color = if (selectedTab == index) PrimaryTextColor else SecondaryTextColor,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        // Content based on selected tab
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (selectedTab) {
                0 -> items(pendingRequests) { request ->
                    RequestCard(request, onStatusChange = { newStatus ->
                        viewModel.updateRequestStatus(request.id, newStatus)
                    })
                }
                1 -> items(activeRequests) { request ->
                    RequestCard(request, onStatusChange = { newStatus ->
                        viewModel.updateRequestStatus(request.id, newStatus)
                    })
                }
                2 -> items(completedRequests) { request ->
                    RequestCard(request, onStatusChange = { newStatus ->
                        viewModel.updateRequestStatus(request.id, newStatus)
                    })
                }
            }

            // Empty state
            if ((selectedTab == 0 && pendingRequests.isEmpty()) ||
                (selectedTab == 1 && activeRequests.isEmpty()) ||
                (selectedTab == 2 && completedRequests.isEmpty())
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay solicitudes en esta categoría",
                            fontSize = 16.sp,
                            color = SecondaryTextColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RequestCard(
    request: ClientRequest,
    onStatusChange: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Solicitud #${request.id.take(8)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryTextColor
                )

                Box(
                    modifier = Modifier
                        .background(
                            color = OrangeUntigrito.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = request.status,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = OrangeUntigrito
                    )
                }
            }

            Text(
                text = "Presupuesto propuesto: $${ "%.2f".format(request.proposedPrice)}",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = OrangeUntigrito
            )

            Text(
                text = request.description.take(80) + "...",
                fontSize = 13.sp,
                color = SecondaryTextColor
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp) // Aumentar espacio entre botones
            ) {
                if (request.status == ClientRequest.STATUS_PENDING) {
                    Text(
                        text = "Aceptar",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier
                            .background(OrangeUntigrito, RoundedCornerShape(8.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp) // Relleno más grande
                    )
                    Text(
                        text = "Rechazar",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = OrangeUntigrito,
                        modifier = Modifier
                            .background(OrangeUntigrito.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp) // Relleno más grande
                    )
                }
            }
        }
    }
}
