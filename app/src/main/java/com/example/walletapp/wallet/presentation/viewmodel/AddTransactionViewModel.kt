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
import com.example.walletapp.wallet.presentation.ui.home.addTransaction.addVoiceTransaction.ParsedTransaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
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
    val errorMessage: String? = null
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
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            try {
                val expenseCats = getCategoriesByType(TransactionType.EXPENSE).first()
                val incomeCats = getCategoriesByType(TransactionType.INCOME).first()
                val accountsList = getAllAccounts().first()

                val initialAccount = accountsList.firstOrNull()

                uiState = uiState.copy(
                    expenseCategories = expenseCats,
                    incomeCategories = incomeCats,
                    accounts = accountsList,
                    selectedAccount = initialAccount,
                    isLoading = false
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    errorMessage = "Ma'lumotlarni yuklashda xato: ${e.localizedMessage}",
                    isLoading = false
                )
            }
        }
    }

    fun onAmountChange(input: String) {
        uiState = uiState.copy(amountInput = input.filter { it.isDigit() || (it == '.' && input.count { dot -> dot == '.' } <= 1) })
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
                date = Date().time,
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
    fun resetSaveSuccessStatus() {
        Log.d("AddTxVM", "Save success holati FALSE ga qaytarildi.")
        uiState = uiState.copy(
            selectedType = TransactionType.EXPENSE,
            selectedCategory = null,
            selectedAccount = null,
            amountInput = "",
            note = "",
            saveSuccess = false,
            errorMessage = null,
            isSaving = false
        )
    }
    fun applyParsedData(parsedData: ParsedTransaction) {
        // 1. Turini o'rnatish
        parsedData.type?.let { onTypeChange(it) }

        // 2. Kategoriya va Hisobni o'rnatish
        parsedData.category?.let { onCategorySelect(it) }
        parsedData.account?.let { onAccountSelect(it) }

        // 3. Summa va Eslatmani o'rnatish
        if (parsedData.amount > 0.0) {
            onAmountChange(parsedData.amount.toString())
        }
        parsedData.note?.let { onNoteChange(it) }
    }

}