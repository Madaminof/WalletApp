package com.example.walletapp.wallet.presentation.ui.budjets


import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walletapp.wallet.domain.model.Budget
import com.example.walletapp.wallet.domain.model.BudgetStatus
import com.example.walletapp.wallet.domain.model.Category
import com.example.walletapp.wallet.domain.model.TransactionType
import com.example.walletapp.wallet.domain.repository.BudgetRepository
import com.example.walletapp.wallet.domain.usecase.budjets.GetBudgetStatusUseCase
import com.example.walletapp.wallet.domain.usecase.category.GetCategoriesByType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


private const val TAG = "BudgetViewModel"

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val budgetRepository: BudgetRepository,
    private val getBudgetStatusUseCase: GetBudgetStatusUseCase,
    private val getCategoriesByType: GetCategoriesByType,
) : ViewModel() {

    val expenseCategories: StateFlow<List<Category>> = getCategoriesByType(TransactionType.EXPENSE)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    @RequiresApi(Build.VERSION_CODES.O)
    val activeBudgetStatuses: StateFlow<List<BudgetStatus>> =
        budgetRepository.getActiveBudgets()
            .flatMapLatest { budgets ->
                if (budgets.isEmpty()) {
                    flowOf(emptyList())
                } else {
                    val statusFlows: List<Flow<BudgetStatus>> = budgets.map { budget ->
                        getBudgetStatusUseCase(budget)
                    }
                    kotlinx.coroutines.flow.combine(
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

    fun saveBudget(newBudget: Budget) {
        viewModelScope.launch {
            try {
                budgetRepository.saveBudget(newBudget)
                Log.d(TAG, "Budget saved successfully: ${newBudget.id}")
            } catch (e: Exception) {
                Log.e(TAG, "Error saving budget: ${newBudget.id}", e)
            }
        }
    }
    fun deleteBudjet(budget: Budget){
        viewModelScope.launch {
            budgetRepository.deleteBudjet(budget)
        }

    }

}