package com.example.walletapp.wallet.presentation.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walletapp.wallet.domain.model.TransactionType
import com.example.walletapp.wallet.domain.usecase.transaction.GetAllTransactions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class CategoryData(
    val categoryName: String,
    val amount: Double,
    val color: Color
)
val categoryColors = mapOf(
    "Oziq-ovqat" to Color(0xFFE53935),
    "Restoran/Kafe" to Color(0xFFEF5350),
    "Uy-joy" to Color(0xFF43A047),
    "Kommunal" to Color(0xFF00ACC1),
    "Kiyim-kechak" to Color(0xFF1E88E5),
    "Sog‘liq/Dori" to Color(0xFFD81B60),
    "Ta'lim/Kurslar" to Color(0xFFFDD835),
    "Internet/TV" to Color(0xFF546E7A),
    "Telefon balansi" to Color(0xFF8D6E63),
    "Shaxsiy Xaridlar" to Color(0xFF673AB7),
    "Dam olish/O'yin" to Color(0xFF66BB6A),
    "Sug'urta to'lovi" to Color(0xFF03A9F4),
    "Uy hayvonlari" to Color(0xFF7CB342),
    "Transport" to Color(0xFFFF7043),
    "Avto yoqilg'i" to Color(0xFFFB8C00),
    "Boshqa Xarajat" to Color(0xFF9E9E9E),

    // YANGI QO'SHILGAN XARAJATLAR:
    "Abonent/Obuna" to Color(0xFFFBC02D),
    "Qarzni to'lash" to Color(0xFFB71C1C),
    "Avto xizmat" to Color(0xFF4DD0E1),
    "Bog'chas" to Color(0xFF8E24AA),
    "Bank" to Color(0xFF78909C),
    "ta'mirlash" to Color(0xFF689F38),
    "Tozalash" to Color(0xFF07F6E0),
    "Jarima/Soliq" to Color(0xFFC2185B),
    "Kredit to'lovi" to Color(0xFF3949AB),

    // DAROMADLAR:
    "Oylik Maosh" to Color(0xFF4CAF50),
    "Qo'shimcha" to Color(0xFF8BC34A),
    "Investitsiya" to Color(0xFF00BFA5),
    "Bonuslar" to Color(0xFFFFC107),
    "Sovg'a/Yutuq" to Color(0xFF5C6BC0),
    "Daromad" to Color(0xFF4FC3F7)
)


fun getCategoryColor(categoryName: String): Color {
    return categoryColors[categoryName] ?: Color.Gray
}
@HiltViewModel
class ChartViewModel @Inject constructor(
    private val getAllTransactions: GetAllTransactions
) : ViewModel() {
    private val transactionsFlow = getAllTransactions(type = null)
    val expenseDataForChart: StateFlow<List<CategoryData>> = transactionsFlow
        .map { transactions ->
            val expenseTransactions = transactions.filter { it.type == TransactionType.EXPENSE }
            val groupedExpenses = expenseTransactions
                .groupBy { it.category.name }
                .map { (categoryName, list) ->
                    val totalAmount = list.sumOf { it.amount }
                    CategoryData(
                        categoryName = categoryName,
                        amount = totalAmount,
                        color = getCategoryColor(categoryName)
                    )
                }
                .filter { it.amount > 0.0 }
                .sortedByDescending { it.amount }

            groupedExpenses
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    val totalExpenseFlow: StateFlow<Double> = transactionsFlow
        .map { transactions ->
            transactions
                .filter { it.type == TransactionType.EXPENSE }
                .sumOf { it.amount }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )
}