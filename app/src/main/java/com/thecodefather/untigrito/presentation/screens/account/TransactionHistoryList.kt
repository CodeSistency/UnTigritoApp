package com.thecodefather.untigrito.presentation.screens.account

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thecodefather.untigrito.presentation.screens.account.data.Transaction
import com.thecodefather.untigrito.ui.theme.UnTigritoTheme
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TransactionHistoryList(
    transactions: List<Transaction>,
    onRefresh: () -> Unit,
    isRefreshing: Boolean
) {
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshing)

    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = onRefresh,
        indicator = { state, trigger ->
            SwipeRefreshIndicator(
                state = state,
                refreshTriggerDistance = trigger,
                scale = true,
                contentColor = MaterialTheme.colorScheme.primary // Utiliza el color primario del tema
            )
        }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "Historial de Transacciones",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.titleMedium
            )
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp) // Espacio entre elementos
            ) {
                items(transactions, key = { it.id }) {
                    TransactionListItem(transaction = it)
                    if (transactions.last() != it) {
                        Divider(modifier = Modifier.padding(horizontal = 16.dp))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TransactionHistoryListPreview() {
    UnTigritoTheme {
        val sampleTransactions = listOf(
            Transaction("1", "Recarga pago móvil", "", "15/10/2025", 11000.00),
            Transaction("2", "Retiro pago móvil", "", "15/10/2025", -8000.00),
            Transaction("3", "Recarga pago móvil", "", "15/10/2025", 2000.00),
            Transaction("4", "Recarga pago móvil", "", "15/10/2025", 10000.00),
        )
        TransactionHistoryList(transactions = sampleTransactions, onRefresh = {}, isRefreshing = false)
    }
}
