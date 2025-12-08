package com.example.walletapp.wallet.presentation.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walletapp.wallet.domain.model.Transaction
import com.example.walletapp.wallet.domain.model.TransactionType
import com.example.walletapp.wallet.domain.model.Account
import com.example.walletapp.wallet.domain.model.Category
import com.example.walletapp.wallet.domain.usecase.account.GetAllAccounts
import com.example.walletapp.wallet.domain.usecase.category.GetCategoriesByType
import com.example.walletapp.wallet.domain.usecase.transaction.SaveTransaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

data class AddTransactionUiState(
    val amountInput: String = "",
    val note: String = "",
    val selectedType: TransactionType = TransactionType.EXPENSE,
    val selectedCategory: Category? = null,
    val selectedAccount: Account? = null,
    val accounts: List<Account> = emptyList(),
    val expenseCategories: List<Category> = emptyList(),
    val incomeCategories: List<Category> = emptyList(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null,
    val selectedDate: Long = Date().time,
)

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val saveTransaction: SaveTransaction,
    private val getCategoriesByType: GetCategoriesByType,
    private val getAllAccounts: GetAllAccounts
) : ViewModel() {

    var uiState by mutableStateOf(AddTransactionUiState())
        private set

    init {
        observeDataFlows()
    }

    private fun observeDataFlows() {
        uiState = uiState.copy(isLoading = true, errorMessage = null)

        val categoriesFlow = combine(
            getCategoriesByType(TransactionType.EXPENSE),
            getCategoriesByType(TransactionType.INCOME)
        ) { expenseCats, incomeCats ->
            Pair(expenseCats, incomeCats)
        }
        combine(
            getAllAccounts(),
            categoriesFlow
        ) { accountsList, categoryPair ->
            Triple(accountsList, categoryPair.first, categoryPair.second)
        }.onEach { (accountsList, expenseCats, incomeCats) ->

            uiState = uiState.copy(
                accounts = accountsList,
                expenseCategories = expenseCats,
                incomeCategories = incomeCats,
                isLoading = false,
                selectedAccount = if (uiState.selectedAccount == null || !accountsList.contains(uiState.selectedAccount)) {
                    accountsList.firstOrNull()
                } else {
                    uiState.selectedAccount
                }
            )
        }.launchIn(viewModelScope)
    }

    fun onAmountChange(input: String) {
        val sanitizedInput = input.filter { it.isDigit() || (it == '.' && input.count { dot -> dot == '.' } <= 1) }

        uiState = uiState.copy(amountInput = sanitizedInput)
    }

    fun setAmount(amount: Double) {
        val formattedAmount = if (amount % 1.0 == 0.0) {
            amount.toLong().toString()
        } else {
            String.format("%.8f", amount).trimEnd('0').trimEnd('.')
        }

        uiState = uiState.copy(amountInput = formattedAmount)
    }

    fun onCategorySelect(category: Category) {
        uiState = uiState.copy(selectedCategory = category)
    }

    fun onAccountSelect(account: Account) {
        uiState = uiState.copy(selectedAccount = account)
    }

    fun onNoteChange(note: String) {
        uiState = uiState.copy(note = note)
    }

    fun onTypeChange(type: TransactionType) {
        uiState = uiState.copy(
            selectedType = type,
            selectedCategory = null
        )
    }
    fun onDateChange(newDate: Long) {
        uiState = uiState.copy(selectedDate = newDate)
    }


    fun saveTransaction() {
        viewModelScope.launch {
            val amount = uiState.amountInput.toDoubleOrNull()

            if (uiState.selectedCategory == null || uiState.selectedAccount == null || amount == null || amount <= 0) {
                uiState = uiState.copy(errorMessage = "Summa, Kategoriya va Hisob musbat qiymat bilan to'ldirilishi shart.")
                return@launch
            }

            uiState = uiState.copy(isSaving = true, errorMessage = null)
            val newTransaction = Transaction(
                id = "",
                amount = amount,
                type = uiState.selectedType,
                category = uiState.selectedCategory!!,
                account = uiState.selectedAccount!!,
                note = uiState.note.trim(),
                date = uiState.selectedDate,
            )

            saveTransaction(newTransaction)
                .onSuccess {
                    uiState = uiState.copy(saveSuccess = true, isSaving = false)
                }
                .onFailure { error ->
                    uiState = uiState.copy(errorMessage = error.message ?: "Saqlashda noma'lum xato.", isSaving = false)
                }
        }
    }

    fun setupForShoppingList(totalAmount: Double, note: String) {
        // Holatni tozalab, keyin maxsus qiymatlarni o'rnatish
        clearState()

        val defaultAccount = uiState.accounts.firstOrNull()
        val defaultCategory = uiState.expenseCategories.firstOrNull()

        uiState = uiState.copy(
            selectedType = TransactionType.EXPENSE, // Doimo EXPENSE
            note = note,
            selectedCategory = defaultCategory, // Birinchi xarajat kategoriyasini tanlash
            selectedAccount = defaultAccount, // Birinchi hisobni tanlash
        )
        setAmount(totalAmount) // Hisoblangan summani o'rnatish
    }

    fun clearState() {
        Log.d("AddTxVM", "Holat boshlang'ich qiymatga qaytarildi.")
        val defaultState = AddTransactionUiState()

        uiState = uiState.copy(
            selectedType = defaultState.selectedType,
            selectedCategory = defaultState.selectedCategory,
            selectedAccount = uiState.accounts.firstOrNull(),
            amountInput = defaultState.amountInput,
            note = defaultState.note,
            selectedDate = defaultState.selectedDate,
            saveSuccess = defaultState.saveSuccess,
            errorMessage = defaultState.errorMessage,
            isSaving = defaultState.isSaving
        )
    }
}