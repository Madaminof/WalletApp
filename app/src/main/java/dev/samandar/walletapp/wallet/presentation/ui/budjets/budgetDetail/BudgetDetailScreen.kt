package dev.samandar.walletapp.wallet.presentation.ui.budjets.budgetDetail

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.samandar.walletapp.R
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.domain.model.Budget
import dev.samandar.walletapp.wallet.domain.model.BudgetStatus
import dev.samandar.walletapp.wallet.presentation.ui.budjets.BudgetEvent
import dev.samandar.walletapp.wallet.presentation.ui.budjets.BudgetViewModel
import dev.samandar.walletapp.wallet.presentation.ui.budjets.editBudget.EditBudgetSheet
import dev.samandar.walletapp.wallet.presentation.ui.charts.expenseListComponents.DeleteConfirmationDialog
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.snackbar.ModernSnackbar
import dev.samandar.walletapp.wallet.presentation.ui.topbars.topbarScreen.CustomTopBar


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BudgetDetailScreen(
    budgetStatus: BudgetStatus,
    onBack: () -> Unit,
    onDelete: () -> Unit,
    viewModel: BudgetViewModel
) {
    var budgetToEdit by remember { mutableStateOf<Budget?>(null) }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.budgetEvent.collect { event ->
            when (event) {
                is BudgetEvent.ShowSnackbar -> {
                    val message = context.getString(event.messageResId)
                    snackbarHostState.showSnackbar(message)
                }
            }
        }
    }


    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                CustomTopBar(
                    title = stringResource(Strings.details),
                    onBackClick = onBack,
                )

            },
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                MainBudgetCard(budgetStatus)

                Spacer(modifier = Modifier.height(12.dp))

                SecondaryInfoCard(budgetStatus)

                Spacer(modifier = Modifier.height(12.dp))

                ActionButtonsRow(
                    onDelete = {showDeleteDialog = true},
                    onEdit = {
                        budgetToEdit = budgetStatus.budget
                        showEditDialog = true
                    }
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding(),

            contentAlignment = Alignment.TopCenter
        ) {
            SnackbarHost(hostState = snackbarHostState) { data ->
                AnimatedContent(
                    targetState = data,
                    transitionSpec = {
                        (slideInVertically(initialOffsetY = { -it }) + fadeIn())
                            .togetherWith(slideOutVertically(targetOffsetY = { -it }) + fadeOut())
                    },
                    label = "ModernSnackbarAnim"
                ) { targetData ->
                    ModernSnackbar(targetData)
                }
            }
        }

    }



    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            onDismiss = { showDeleteDialog = false },
            onConfirmDelete = {
                showDeleteDialog = false
                onDelete()
            },
            title = stringResource(R.string.budget_delete_dialog_title),
            text = stringResource(R.string.budget_delete_dialog_text)
        )
    }
    budgetToEdit?.let { editBudget ->
        EditBudgetSheet(
            budgetToEdit = editBudget,
            onDismiss = { budgetToEdit = null },
            viewModel = viewModel
        )
    }
}
