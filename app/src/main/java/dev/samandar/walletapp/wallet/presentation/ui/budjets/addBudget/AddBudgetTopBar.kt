package dev.samandar.walletapp.wallet.presentation.ui.budjets.addBudget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.domain.model.BudgetPeriod
import dev.samandar.walletapp.wallet.domain.model.Category
import dev.samandar.walletapp.wallet.presentation.ui.budjets.BudgetViewModel
import kotlinx.coroutines.CoroutineScope


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBudgetTopBar(
    navController: NavController,
    selectedCategory: Category?,
    maxAmountInput: String,
    selectedPeriod: BudgetPeriod,
    endDateMillis: Long?,
    startDateMillis: Long,
    viewModel: BudgetViewModel,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope
) {
    TopAppBar(
        title = { Text(
            text = stringResource(Strings.title_add_budget),
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onTertiary.copy(0.8f),
            modifier = Modifier.padding(start = 12.dp)
        ) },
        navigationIcon = {
            Box(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.onPrimaryContainer.copy(0.5f)),
                contentAlignment = Alignment.Center
            ){
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Filled.Close, contentDescription = "Orqaga")
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onTertiary,
            navigationIconContentColor = MaterialTheme.colorScheme.onTertiary
        ),
        actions = {
            SaveButton(
                selectedCategory = selectedCategory,
                maxAmountInput = maxAmountInput,
                selectedPeriod = selectedPeriod,
                endDateMillis = endDateMillis,
                startDateMillis = startDateMillis,
                viewModel = viewModel,
                navController = navController,
                snackbarHostState = snackbarHostState,
                scope = scope
            )
        }
    )
}