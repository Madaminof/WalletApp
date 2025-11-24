package com.example.walletapp.wallet.presentation.ui.budjets

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.walletapp.wallet.domain.model.BudgetStatus
import androidx.navigation.NavController

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
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.onBackground),
            contentAlignment = Alignment.Center,
        ) {
            Text("Hozircha faol budjetlar yo'q.", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onTertiary)
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.onBackground),
        ){
            LazyColumn(
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                items(budgetStatuses) { status ->
                    BudgetStatusCard(
                        status = status,
                        onClick = {
                            selectedBudget = status
                        }
                    )
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

    }
}

@Composable
fun BudgetStatusCard(
    status: BudgetStatus,
    onClick: () -> Unit
) {
    val progressColor = when {
        status.isOverBudget -> MaterialTheme.colorScheme.error
        status.percentageUsed > 80.0 -> Color(0xFFFF9800)
        else -> MaterialTheme.colorScheme.primary
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 4.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onBackground,
            contentColor = MaterialTheme.colorScheme.onTertiary
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = status.budget.category.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiary
                )
                Text(
                    text = "${status.percentageUsed.toInt()}%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = progressColor
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Limit: ${status.budget.maxAmount} | Sarf: ${status.spentAmount}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onTertiary
            )

            Spacer(Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = (status.percentageUsed / 100).toFloat().coerceIn(0f, 1f),
                color = progressColor,
                trackColor = MaterialTheme.colorScheme.onTertiary.copy(0.3f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
            )

            Spacer(Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Qoldi: ${status.remainingAmount}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = if (status.isOverBudget) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Qolgan kunlar: ${status.daysRemaining}",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onTertiary.copy(0.5f)
                )
            }
        }
    }
}