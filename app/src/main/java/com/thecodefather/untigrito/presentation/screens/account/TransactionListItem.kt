package com.thecodefather.untigrito.presentation.screens.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thecodefather.untigrito.presentation.screens.account.data.Transaction
import com.thecodefather.untigrito.ui.theme.UnTigritoTheme

@Composable
fun TransactionListItem(
    transaction: Transaction
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = transaction.type,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = transaction.date,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray // O un color de tema para texto secundario
            )
        }
        Text(
            text = "${String.format("%,.2f", transaction.amount)}",
            style = MaterialTheme.typography.bodyLarge,
            color = if (transaction.amount >= 0) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.error // Rojo para montos negativos
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TransactionListItemPreview() {
    UnTigritoTheme {
        Column {
            TransactionListItem(Transaction("1", "Recarga pago móvil", "", "15/10/2025", 11000.00))
            TransactionListItem(Transaction("2", "Retiro pago móvil", "", "15/10/2025", -8000.00))
            TransactionListItem(Transaction("3", "Recarga pago móvil", "", "15/10/2025", 2000.00))
        }
    }
}
