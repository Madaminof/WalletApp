package dev.samandar.walletapp.wallet.presentation.ui.budjets.addBudget

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.samandar.walletapp.R
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.domain.model.BudgetPeriod
import dev.samandar.walletapp.wallet.domain.model.Category
import dev.samandar.walletapp.wallet.presentation.ui.budjets.BudgetViewModel
import dev.samandar.walletapp.wallet.presentation.ui.topbars.topbarScreen.PremiumButton
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
    CenterAlignedTopAppBar(
        title = { Text(
            text = stringResource(Strings.title_add_budget),
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onTertiary.copy(0.8f),
            modifier = Modifier.padding(start = 12.dp)
        ) },
        navigationIcon = {
            PremiumButton(
                onClick = { navController.popBackStack() },
                icon = R.drawable.close_ic,
                color = MaterialTheme.colorScheme.primary.copy(0.8f),
                modifier = Modifier
            )

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
