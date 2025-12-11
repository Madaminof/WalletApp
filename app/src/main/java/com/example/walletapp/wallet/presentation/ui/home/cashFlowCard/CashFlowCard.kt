package com.example.walletapp.wallet.presentation.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.walletapp.ui.theme.expenseColor
import com.example.walletapp.ui.theme.incomeColor
import com.example.walletapp.wallet.presentation.ui.home.cashFlowCard.CashFlowFilterDialog
import com.example.walletapp.wallet.presentation.ui.home.totalBalanceCard.CircularIconButton
import com.example.walletapp.wallet.presentation.ui.home.totalBalanceCard.primaryAccent
import com.example.walletapp.wallet.presentation.ui.otherScreens.settings.items.currency.CurrencyManager
import com.example.walletapp.wallet.presentation.utils.FormatAmount
import com.example.walletapp.wallet.presentation.utils.formatAmountWithCurrency
import com.example.walletapp.wallet.presentation.viewmodel.CashFlowViewModel
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.absoluteValue

val activeCurrency by CurrencyManager.currentCurrency

@Composable
fun CashFlowItem(
    icon: ImageVector,
    label: String,
    amount: Double,
    itemColor: Color,
    isTotal: Boolean = false
) {
    val displayColor = if (isTotal) {
        if (amount < 0) expenseColor else incomeColor
    } else {
        itemColor
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = if (isTotal) 8.dp else 2.dp, horizontal = 0.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = displayColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(8.dp))

            Text(
                text = "$label:",
                fontSize = if (isTotal) 14.sp else 13.sp,
                fontWeight = if (isTotal) FontWeight.SemiBold else FontWeight.Normal,
                color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
            )
        }
        Text(
            text = FormatAmount(amount),
            fontSize = if (isTotal) 14.sp else 13.sp,
            fontWeight = if (isTotal) FontWeight.SemiBold else FontWeight.Normal,
            color = displayColor,
        )
    }
}

@Composable
fun CashFlowCard(
    viewModel: CashFlowViewModel = hiltViewModel()
) {
    val state by viewModel.cardState.collectAsState()

    if (state.isLoading) {
        Box( /* Loading UI */ ) { /* ... */ }
    } else {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimaryContainer),
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().height(32.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Cash flow",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.8f)
                    )
                    CircularIconButton(
                        onClick = viewModel::onFilterClick,
                        icon = Icons.Default.FilterList,
                        contentDescription = "Filter",
                        tint = primaryAccent,
                        backgroundColor = primaryAccent.copy(alpha = 0.1f),
                        size = 32.dp
                    )
                }
                Text(text = state.periodLabel, fontSize = 10.sp, color = MaterialTheme.colorScheme.onTertiary.copy(0.7f))
                Spacer(Modifier.height(4.dp))

                CashFlowItem(Icons.Default.ArrowUpward, "Income", state.income, incomeColor)
                CashFlowItem(Icons.Default.ArrowDownward, "Expenses", state.expenses, expenseColor)

                Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

                CashFlowItem(
                    icon = Icons.Default.Balance,
                    label = "Total",
                    amount = state.total,
                    itemColor = if (state.total < 0) expenseColor else incomeColor,
                    isTotal = true
                )
            }
        }
    }

    if (state.isFilterDialogOpen) {
        CashFlowFilterDialog(
            initialSelectedFilter = viewModel.cardState.collectAsState().value.periodLabel,
            onFilterChange = viewModel::onFilterChange,
            onDismiss = viewModel::onFilterDismiss
        )
    }
}