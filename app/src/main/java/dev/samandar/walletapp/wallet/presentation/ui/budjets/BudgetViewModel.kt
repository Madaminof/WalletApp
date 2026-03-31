package dev.samandar.walletapp.wallet.presentation.ui.budjets

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.samandar.walletapp.R
import dev.samandar.walletapp.wallet.data.currencyManagerApi.repository.CurrencyRepository
import dev.samandar.walletapp.wallet.domain.model.Budget
import dev.samandar.walletapp.wallet.domain.model.BudgetStatus
import dev.samandar.walletapp.wallet.domain.model.Category
import dev.samandar.walletapp.wallet.domain.model.TransactionType
import dev.samandar.walletapp.wallet.domain.repository.BudgetRepository
import dev.samandar.walletapp.wallet.domain.usecase.budjets.GetBudgetStatusUseCase
import dev.samandar.walletapp.wallet.domain.usecase.category.GetCategoriesByType
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.items.currency.CurrencyManager
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.items.currency.changeUpdateAmount.CurrencyEvaluator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

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
    private val currencyRepository: CurrencyRepository // Currency qo'shildi
) : ViewModel() {

    private val _budgetEvent = Channel<BudgetEvent>()
    val budgetEvent = _budgetEvent.receiveAsFlow()

    // Valyuta oqimlari
    private val ratesFlow = currencyRepository.allRates
    private val currentCurrencyFlow = CurrencyManager.getCurrencyFlow()

    val expenseCategories: StateFlow<List<Category>> = getCategoriesByType(TransactionType.EXPENSE)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val budgets: StateFlow<List<Budget>> = budgetRepository.getActiveBudgets()
        .map { list -> list.sortedByDescending { it.createdAt } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    @RequiresApi(Build.VERSION_CODES.O)
    val activeBudgetStatuses: StateFlow<List<BudgetStatus>> = combine(
        budgetRepository.getActiveBudgets(),
        currentCurrencyFlow,
        ratesFlow
    ) { rawBudgets, currency, rates ->
        Triple(rawBudgets, currency, rates)
    }.flatMapLatest { (budgets, currency, rates) ->
        if (budgets.isEmpty()) {
            flowOf(emptyList())
        } else {
            val statusFlows: List<Flow<BudgetStatus>> = budgets.map { budget ->
                getBudgetStatusUseCase(budget).map { status ->
                    // Summalarni valyutaga o'girish
                    val convertedMax = CurrencyEvaluator.convert(status.budget.maxAmount, currency, rates)
                    val convertedSpent = CurrencyEvaluator.convert(status.spentAmount, currency, rates)

                    status.copy(
                        budget = status.budget.copy(maxAmount = convertedMax),
                        spentAmount = convertedSpent
                    )
                }
            }
            combine(statusFlows) { it.toList() }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun saveBudget(newBudget: Budget) = viewModelScope.launch {
        try {
            // 1. Hozirgi tanlangan valyuta va kurslarni olamiz
            val currentCurrency = CurrencyManager.getCurrencyFlow().first()
            val rates = currencyRepository.allRates.first()

            // 2. FOYDALANUVCHI KIRITGAN SUMMANI BAZAVIY VALYUTAGA (UZS) O'GIRAMIZ
            // Agar main USD bo'lsa va 500 kiritilsa, uni bazaga moslab saqlash kerak
            val baseAmount = CurrencyEvaluator.convertToBase(
                amount = newBudget.maxAmount,
                currentCurrency = currentCurrency,
                rates = rates,
            )

            val budgetToSave = newBudget.copy(maxAmount = baseAmount)

            budgetRepository.saveBudget(budgetToSave)
            _budgetEvent.send(BudgetEvent.ShowSnackbar(R.string.snackbar_success_budget_saved))
        } catch (e: Exception) {
            Log.e(TAG, "Error saving budget", e)
        }
    }

    fun updateBudget(budget: Budget) = viewModelScope.launch {
        try {
            budgetRepository.updateBudget(budget)
            _budgetEvent.send(BudgetEvent.ShowSnackbar(R.string.snackbar_success_budget_updated))
        } catch (e: Exception) {
            Log.e(TAG, "Error updating budget", e)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getBudgetStatusById(budgetId: String): Flow<BudgetStatus?> {
        return activeBudgetStatuses.map { statuses ->
            statuses.find { it.budget.id == budgetId }
        }
    }

    fun deleteBudjet(budget: Budget) = viewModelScope.launch {
        try {
            budgetRepository.deleteBudjet(budget)
            _budgetEvent.send(BudgetEvent.ShowSnackbar(R.string.snackbar_success_budget_deleted))
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting budget", e)
        }
    }

    data class BudgetCardState(
        val hasActiveBudget: Boolean = false,
        val totalBudgetLimit: Double = 0.0,
        val totalSpentAmount: Double = 0.0,
        val totalDailyLimit: Double = 0.0,
        val currencySymbol: String = ""
    )

    @RequiresApi(Build.VERSION_CODES.O)
    val budgetCardState: StateFlow<BudgetCardState> = combine(
        activeBudgetStatuses,
        currentCurrencyFlow
    ) { statuses, currency ->
        if (statuses.isEmpty()) {
            BudgetCardState(hasActiveBudget = false, currencySymbol = currency)
        } else {
            val totalLimit = statuses.sumOf { it.budget.maxAmount }
            val totalSpent = statuses.sumOf { it.spentAmount }

            val totalDaily = statuses.sumOf { status ->
                val remaining = status.budget.maxAmount - status.spentAmount
                if (status.daysRemaining > 0 && remaining > 0) {
                    remaining / status.daysRemaining
                } else 0.0
            }

            BudgetCardState(
                hasActiveBudget = true,
                totalBudgetLimit = totalLimit,
                totalSpentAmount = totalSpent,
                totalDailyLimit = totalDaily,
                currencySymbol = currency
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BudgetCardState()
    )
}