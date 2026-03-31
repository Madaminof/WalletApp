package dev.samandar.walletapp.wallet.smartScannQR

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.samandar.walletapp.wallet.data.currencyManagerApi.repository.CurrencyRepository
import dev.samandar.walletapp.wallet.domain.model.account.Account
import dev.samandar.walletapp.wallet.domain.model.Category
import dev.samandar.walletapp.wallet.domain.model.Transaction
import dev.samandar.walletapp.wallet.domain.model.TransactionType
import dev.samandar.walletapp.wallet.domain.model.smartScannModel.Receipt
import dev.samandar.walletapp.wallet.domain.usecase.account.GetAllAccounts
import dev.samandar.walletapp.wallet.domain.usecase.category.GetCategoriesByType
import dev.samandar.walletapp.wallet.domain.usecase.smartScannUsecase.SaveReceiptUseCase
import dev.samandar.walletapp.wallet.domain.usecase.transaction.SaveTransaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val saveReceiptUseCase: SaveReceiptUseCase,
    private val saveTransaction: SaveTransaction,
    private val getAccountsUseCase: GetAllAccounts,
    private val getCategoriesUseCase: GetCategoriesByType,
    private val currencyRepository: CurrencyRepository // 👈 Kurslar uchun
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewUiState())
    val uiState: StateFlow<ReviewUiState> = _uiState.asStateFlow()

    val accounts: StateFlow<List<Account>> = getAccountsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Barcha xarajat kategoriyalarini kuzatib borish uchun
    val categories: StateFlow<List<Category>> = getCategoriesUseCase(TransactionType.EXPENSE)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setScannedReceipt(receipt: Receipt) {
        viewModelScope.launch {
            try {
                val accounts = getAccountsUseCase().firstOrNull() ?: emptyList()
                val categories = getCategoriesUseCase(TransactionType.EXPENSE).firstOrNull() ?: emptyList()
                val method = receipt.paymentMethod?.uppercase() ?: "NAQD"

                val matchedAccount = when (method) {
                    "KARTA" -> {
                        val cardKeywords = listOf("Karta", "Card", "Uzcard", "Humo", "Visa", "Master")
                        accounts.find { acc ->
                            cardKeywords.any { key -> acc.name.contains(key, ignoreCase = true) }
                        } ?: accounts.firstOrNull()
                    }
                    "NAQD" -> {
                        accounts.find { it.name.contains("Cash", ignoreCase = true) || it.name.contains("Naqd", ignoreCase = true) }
                            ?: accounts.firstOrNull()
                    }
                    else -> accounts.firstOrNull()
                }

                val matchedCategory = categories.find { it.name.contains("Groceries", ignoreCase = true) || it.name.contains("Oziq", ignoreCase = true) }
                    ?: categories.find { it.name.contains("Other", ignoreCase = true) }
                    ?: categories.firstOrNull()

                _uiState.update { it.copy(
                    receipt = receipt,
                    selectedAccount = matchedAccount,
                    selectedCategory = matchedCategory,
                    errorMessage = null
                ) }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Ma'lumot yuklashda xato") }
            }
        }
    }

    fun updateCategory(category: Category) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun updateAccount(account: Account) {
        _uiState.update { it.copy(selectedAccount = account) }
    }

    fun saveFinalReceipt(onSuccess: () -> Unit) {
        val state = _uiState.value
        val receipt = state.receipt ?: return
        val account = state.selectedAccount ?: return
        val category = state.selectedCategory ?: return

        if (state.isSaving) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }

            val result = withContext(Dispatchers.IO) {
                runCatching {
                    // 1. Unikal ID yaratish
                    val transactionId = UUID.randomUUID().toString()

                    // 2. Eslatma (Note) tayyorlash
                    val transactionNote = buildString {
                        append("QR Scanner: ${receipt.merchantName}")
                        if (!receipt.note.isNullOrBlank()) {
                            append("\n\nBatafsil:\n${receipt.note}")
                        }
                    }

                    // 3. Tranzaksiya obyektini yaratish
                    // DIQQAT: SaveTransaction UseCase-ing 'amount'ni UZSda qabul qilib,
                    // uni account valyutasiga o'zi o'giradi.
                    val transaction = Transaction(
                        id = transactionId,
                        amount = receipt.totalAmount,        // Bazaviy summa (UZS)
                        amountInBase = receipt.totalAmount,  // Statistika uchun (UZS)
                        originalAmount = receipt.totalAmount,
                        originalCurrency = "UZS",            // QR cheklar doim so'mda
                        exchangeRate = 1.0,
                        type = TransactionType.EXPENSE,
                        note = transactionNote,
                        date = receipt.date,
                        category = category,
                        account = account,
                        originalUrl = receipt.originalUrl
                    )

                    // 4. Chek obyektini yangilangan ID bilan tayyorlash
                    val finalReceipt = receipt.copy(
                        transactionId = transactionId,
                        items = receipt.items.map { it.copy(categoryId = category.id) }
                    )

                    // 5. Ma'lumotlarni ketma-ket saqlash
                    // SaveTransaction balansni yangilaydi, SaveReceiptUseCase chek detalini saqlaydi
                    saveTransaction(transaction).getOrThrow()
                    saveReceiptUseCase(finalReceipt)
                }
            }

            // 6. Natijani UI-ga qaytarish
            result.onSuccess {
                withContext(Dispatchers.Main) {
                    _uiState.update { it.copy(isSaving = false) }
                    onSuccess()
                }
            }.onFailure { error ->
                _uiState.update { it.copy(
                    isSaving = false,
                    errorMessage = error.message ?: "Saqlashda kutilmagan xato yuz berdi"
                ) }
            }
        }
    }

    fun resetState() {
        _uiState.update { ReviewUiState() }
    }
}

data class ReviewUiState(
    val receipt: Receipt? = null,
    val selectedAccount: Account? = null,
    val selectedCategory: Category? = null,
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)