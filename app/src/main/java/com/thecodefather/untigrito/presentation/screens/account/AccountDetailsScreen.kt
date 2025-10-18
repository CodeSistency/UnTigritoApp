package com.thecodefather.untigrito.presentation.screens.account

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.thecodefather.untigrito.ui.theme.UnTigritoTheme
import com.thecodefather.untigrito.presentation.screens.account.data.Transaction
import androidx.lifecycle.viewmodel.compose.viewModel
import com.thecodefather.untigrito.presentation.navigation.ClientRoutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountDetailsScreen(
    navController: NavController,
    viewModel: AccountDetailsViewModel = viewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles de tu cuenta") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 0.dp)
        ) {
            AccountSummaryCard(
                balance = viewModel.balance,
                onRechargeClick = { navController.navigate(ClientRoutes.RECHARGE) },
                onWithdrawClick = { navController.navigate(ClientRoutes.WITHDRAW) }
            )
            TransactionHistoryList(
                transactions = viewModel.transactions,
                onRefresh = { viewModel.loadAccountDetails() },
                isRefreshing = viewModel.isRefreshing
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AccountDetailsScreenPreview() {
    UnTigritoTheme {
        AccountDetailsScreen(rememberNavController())
    }
}

