package dev.samandar.walletapp.wallet.presentation.ui.budjets


import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.samandar.walletapp.R
import dev.samandar.walletapp.wallet.domain.model.Budget
import dev.samandar.walletapp.wallet.domain.model.BudgetStatus
import dev.samandar.walletapp.wallet.domain.model.Category
import dev.samandar.walletapp.wallet.domain.model.TransactionType
import dev.samandar.walletapp.wallet.domain.repository.BudgetRepository
import dev.samandar.walletapp.wallet.domain.usecase.budjets.GetBudgetStatusUseCase
import dev.samandar.walletapp.wallet.domain.usecase.category.GetCategoriesByType
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.items.currency.CurrencyManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


val activeCurrency by CurrencyManager.currentCurrency


private const val TAG = "BudgetViewModel"

sealed class BudgetEvent {
    data class ShowSnackbar(@StringRes val messageResId: Int) : BudgetEvent()
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val budgetRepository: BudgetRepository,
    private val getBudgetStatusUseCase: GetBudgetStatusUseCase,
    private val getCategoriesByType: GetCategoriesByType,
) : ViewModel() {

    private val _budgetEvent = Channel<BudgetEvent>()
    val budgetEvent = _budgetEvent.receiveAsFlow()

    val expenseCategories: StateFlow<List<Category>> = getCategoriesByType(TransactionType.EXPENSE)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())


    val budgets: StateFlow<List<Budget>> = budgetRepository.getActiveBudgets()
        .map { list ->
            list.sortedByDescending { it.createdAt }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    @RequiresApi(Build.VERSION_CODES.O)
    val activeBudgetStatuses: StateFlow<List<BudgetStatus>> =
        budgetRepository.getActiveBudgets()
            .map {budgets ->
                budgets.sortedByDescending { it.createdAt }
            }
            .flatMapLatest { budgets ->
                if (budgets.isEmpty()) {
                    flowOf(emptyList())
                } else {
                    val statusFlows: List<Flow<BudgetStatus>> = budgets.map { budget ->
                        getBudgetStatusUseCase(budget)
                    }

                    combine(
                        flows = statusFlows,
                        transform = { statusArray: Array<BudgetStatus> ->
                            statusArray.toList()
                        }
                    )
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun saveBudget(newBudget: Budget) = viewModelScope.launch {
        try {
            budgetRepository.saveBudget(newBudget)
            _budgetEvent.send(BudgetEvent.ShowSnackbar(R.string.snackbar_success_budget_saved))
            Log.d(TAG, "Budget saved successfully: ${newBudget.id}")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving budget: ${newBudget.id}", e)
        }
    }

    fun updateBudget(budget: Budget){
        viewModelScope.launch {
            try {
                budgetRepository.updateBudget(budget)
                _budgetEvent.send(BudgetEvent.ShowSnackbar(R.string.snackbar_success_budget_updated))
                Log.d(TAG, "Budget updated successfully: ${budget.id}")
            } catch (e: Exception) {
                Log.e(TAG, "Error updating budget: ${budget.id}", e)
            }
        }
    }
/*
    fun getBudgetById(budgetId: String?): Flow<BudgetWithCategory?> {
        return if (budgetId != null) {
            budgetRepository.getBudgetById(budgetId)
        } else {
            emptyFlow()
        }
    }*/
    @RequiresApi(Build.VERSION_CODES.O)
    fun getBudgetStatusById(budgetId: String): Flow<BudgetStatus?> {
        return activeBudgetStatuses.map { statuses ->
            statuses.find { it.budget.id == budgetId }
        }
    }


    fun deleteBudjet(budget: Budget){
        viewModelScope.launch {
            try {
                budgetRepository.deleteBudjet(budget)
                _budgetEvent.send(BudgetEvent.ShowSnackbar(R.string.snackbar_success_budget_deleted))
                Log.d(TAG, "Budget deleted successfully: ${budget.id}")
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting budget: ${budget.id}", e)
            }
        }
    }


    data class BudgetCardState(
        val hasActiveBudget: Boolean = false,
        val totalBudgetLimit: Double = 0.0,
        val totalSpentAmount: Double = 0.0,
        val totalDailyLimit: Double = 0.0,
        val currencySymbol: String = activeCurrency
    )


    @RequiresApi(Build.VERSION_CODES.O)
    val budgetCardState: StateFlow<BudgetCardState> = activeBudgetStatuses
        .map { statuses ->
            if (statuses.isEmpty()) {
                BudgetCardState(hasActiveBudget = false)
            } else {
                val totalLimit = statuses.sumOf { it.budget.maxAmount }
                val totalSpent = statuses.sumOf { it.spentAmount }

                val totalDaily = statuses.sumOf { status ->
                    val remaining = status.budget.maxAmount - status.spentAmount
                    if (status.daysRemaining > 0 && remaining > 0) {
                        remaining / status.daysRemaining
                    } else {
                        0.0
                    }
                }

                BudgetCardState(
                    hasActiveBudget = true,
                    totalBudgetLimit = totalLimit,
                    totalSpentAmount = totalSpent,
                    totalDailyLimit = totalDaily
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = BudgetCardState()
        )
}