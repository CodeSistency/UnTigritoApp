package com.example.vibecoding3.account.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vibecoding3.ui.theme.Vibecoding3Theme

data class AccountDetails(
    val accountName: String,
    val availableBalance: String,
    val currency: String,
    val gradientColors: List<Color>
)

data class TransactionItem(
    val description: String,
    val date: String,
    val amount: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen() {
    val accountDetails = AccountDetails(
        accountName = "Cuenta",
        availableBalance = "15.000,00",
        currency = "Bs",
        gradientColors = listOf(Color(0xFFFFA726), Color(0xFFFF7043)) // Colores del gradiente de la imagen
    )

    val transactions = listOf(
        TransactionItem("Recarga pago movil", "15/10/2025", "11.000,00"),
        TransactionItem("Retiro pago movil", "15/10/2025", "-8.000,00"),
        TransactionItem("Recarga pago movil", "15/10/2025", "2.000,00"),
        TransactionItem("Recarga pago movil", "15/10/2025", "10.000,00")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles de tu cuenta") },
                navigationIcon = {
                    IconButton(onClick = { /* TODO: Handle back */ }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AccountCardWithGradient(accountDetails = accountDetails)
            Spacer(modifier = Modifier.height(16.dp))
            AccountItemList(transactions = transactions)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAccountScreen() {
    Vibecoding3Theme {
        AccountScreen()
    }
}

@Composable
fun AccountCardWithGradient(accountDetails: AccountDetails) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.horizontalGradient(accountDetails.gradientColors)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Icono de cuenta (puedes reemplazarlo con un icono real)
                        Icon(
                            imageVector = Icons.Default.AccountCircle, // Placeholder
                            contentDescription = "Cuenta",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = accountDetails.accountName,
                            color = Color.White,
                            fontSize = 18.sp
                        )
                    }
                    // Logo (puedes reemplazarlo con una imagen real)
                    Text(
                        text = "UnTigrito", // Placeholder
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Disponible (${accountDetails.currency})",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                    Text(
                        text = accountDetails.availableBalance,
                        color = Color.White,
                        fontSize = 30.sp,
                        //fontWeight = FontWeight.Bold // Añade esto si quieres negrita
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { /* TODO: Handle recharge */ },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF9A825)),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ArrowDownward, contentDescription = "Recargar", tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Recargar", color = Color.White)
                    }
                    Button(
                        onClick = { /* TODO: Handle withdraw */ },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF9A825)),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ArrowUpward, contentDescription = "Retirar", tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Retirar", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun DefaultAccountListItem(transaction: TransactionItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = transaction.description, fontSize = 16.sp, color = Color.Black)
                Text(text = transaction.date, fontSize = 12.sp, color = Color.Gray)
            }
            Text(
                text = transaction.amount,
                fontSize = 16.sp,
                color = if (transaction.amount.startsWith("-")) Color.Red else Color.Black
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Divider(color = Color.LightGray, thickness = 0.5.dp)
    }
}

@Composable
fun AccountItemList(transactions: List<TransactionItem>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(transactions) {
            DefaultAccountListItem(transaction = it)
        }
    }
}
