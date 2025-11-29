package com.example.walletapp.wallet.presentation.ui.budjets

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.walletapp.wallet.domain.model.BudgetStatus
import androidx.navigation.NavController
import java.text.DecimalFormat

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BudgetsScreen(
    viewModel: BudgetViewModel = hiltViewModel(),
    navController: NavController
) {
    val budgetStatuses by viewModel.activeBudgetStatuses.collectAsState()
    var selectedBudget by remember { mutableStateOf<BudgetStatus?>(null) }
    if (budgetStatuses.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.onBackground),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                "Hozircha faol budjetlar yo'q.",
                color = MaterialTheme.colorScheme.onTertiary
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.onBackground),
            contentPadding = PaddingValues(horizontal = 0.dp, vertical = 0.dp),
            verticalArrangement = Arrangement.Top,
        ) {
            item {
                ThisMonthBudgetCard(
                    budgetStatuses = budgetStatuses,
                    onBudgetClick = { status -> selectedBudget = status }
                )
            }
        }
    }
    selectedBudget?.let { status ->
        BudgetDetailBottomSheet(
            budgetStatus = status,
            onDismiss = { selectedBudget = null },
            onUpdate = { /* ... */ },
            onDelete = { acc ->
                viewModel.deleteBudjet(acc.budget)
                selectedBudget = null
            }
        )
    }
}

@Composable
fun ThisMonthBudgetCard(
    budgetStatuses: List<BudgetStatus>,
    onBudgetClick: (BudgetStatus) -> Unit
) {
    val totalSpent = budgetStatuses.sumOf { it.spentAmount }
    val totalLimit = budgetStatuses.sumOf { it.budget.maxAmount }

    val formatter = remember { DecimalFormat("#,##0") }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp, vertical = 8.dp)
            .background(MaterialTheme.colorScheme.primaryContainer.copy(0.7f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "This month\n(so'm)",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onTertiary,
                    lineHeight = 18.sp
                )
                Text(
                    text = "Σ ${formatter.format(totalSpent)} / ${formatter.format(totalLimit)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onTertiary
                )
            }
            Spacer(Modifier.height(16.dp))
        }
        budgetStatuses.forEach { status ->
            SimpleBudgetRow(status = status, onClick = { onBudgetClick(status) })
        }
        if (budgetStatuses.size > 2) {
            Text(
                text = "SHOW MORE",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clickable { /* logic */ }
            )
        }
        Spacer(Modifier.height(8.dp))
    }
}


@Composable
fun SimpleBudgetRow(
    status: BudgetStatus,
    onClick: () -> Unit
) {
    val formatter = remember { DecimalFormat("#,##0") }
    val progress = (status.percentageUsed / 100).toFloat().coerceIn(0f, 1f)

    val progressFloat by animateFloatAsState(targetValue = progress, label = "progressAnim")
    val rowContentColor = MaterialTheme.colorScheme.onTertiary.copy(0.8f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = status.budget.category.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = rowContentColor
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${formatter.format(status.spentAmount)} / ${formatter.format(status.budget.maxAmount)}",
                    fontSize = 16.sp,
                    color = rowContentColor
                )
                Spacer(Modifier.width(8.dp))
                Icon(
                    Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = rowContentColor.copy(0.7f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = progressFloat,
            color = MaterialTheme.colorScheme.primary,
            trackColor = rowContentColor.copy(0.2f),
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
        )
    }
}