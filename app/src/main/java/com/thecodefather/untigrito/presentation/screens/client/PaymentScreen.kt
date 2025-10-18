package com.thecodefather.untigrito.presentation.screens.client

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import com.thecodefather.untigrito.domain.model.Transaction
import com.thecodefather.untigrito.presentation.components.ClientBottomNavBar
import com.thecodefather.untigrito.presentation.viewmodel.PaymentViewModel

/**
 * Payment Screen
 * Manages balance, transactions, and payment methods
 */
@Composable
fun PaymentScreen(
    navController: NavController,
    viewModel: PaymentViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsState(initial = null)
    val transactions by viewModel.transactions.collectAsState(initial = emptyList())
    val totalRecharged by viewModel.totalRecharged.collectAsState()
    val totalWithdrawn by viewModel.totalWithdrawn.collectAsState()
    val loading by viewModel.loading.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 56.dp),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Pagos y Transacciones",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            // Balance Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE67822)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Balance Disponible",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.9f)
                        )

                        Text(
                            text = "$${"%.2f".format(user?.balance ?: 0.0)}",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = { viewModel.addTransaction(100.0, Transaction.TYPE_RECHARGE, "Recarga") },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    "Recargar",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE67822)
                                )
                            }

                            Button(
                                onClick = { viewModel.addTransaction(50.0, Transaction.TYPE_WITHDRAWAL, "Retiro") },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White.copy(alpha = 0.2f)
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    "Retirar",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // Statistics
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(80.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("Recargado", fontSize = 11.sp, color = Color.Gray)
                            Text(
                                "$${"%.2f".format(totalRecharged)}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Green
                            )
                        }
                    }

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(80.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("Retirado", fontSize = 11.sp, color = Color.Gray)
                            Text(
                                "$${"%.2f".format(totalWithdrawn)}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Red
                            )
                        }
                    }
                }
            }

            // Transactions Header
            item {
                Text(
                    text = "Historial de Transacciones",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            // Transactions List
            items(transactions) { transaction ->
                TransactionCard(transaction)
            }

            // Empty State
            if (transactions.isEmpty() && !loading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay transacciones",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }

        ClientBottomNavBar(
            currentRoute = "payment",
            onNavigate = { route ->
                if (route != "payment") {
                    navController.navigate(route)
                }
            }
        )
    }
}

@Composable
private fun TransactionCard(transaction: Transaction) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.description,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Text(
                    text = transaction.type,
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${"+ ".takeIf { transaction.type == Transaction.TYPE_RECHARGE } ?: "- "}${"%.2f".format(transaction.amount)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = when (transaction.type) {
                        Transaction.TYPE_RECHARGE -> Color.Green
                        Transaction.TYPE_PAYMENT -> Color.Red
                        else -> Color(0xFFE67822)
                    }
                )

                Box(
                    modifier = Modifier
                        .background(
                            color = when (transaction.status) {
                                Transaction.STATUS_COMPLETED -> Color.Green.copy(alpha = 0.1f)
                                Transaction.STATUS_PENDING -> Color(0xFFE67822).copy(alpha = 0.1f)
                                else -> Color.Red.copy(alpha = 0.1f)
                            },
                            shape = RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = transaction.status,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (transaction.status) {
                            Transaction.STATUS_COMPLETED -> Color.Green
                            Transaction.STATUS_PENDING -> Color(0xFFE67822)
                            else -> Color.Red
                        }
                    )
                }
            }
        }
    }
}
