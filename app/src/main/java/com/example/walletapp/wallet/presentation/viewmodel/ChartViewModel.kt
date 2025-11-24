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
    "Oziq-ovqat" to Color(0xFF4CAF50),      // Yashil – fresh food
    "Transport" to Color(0xFF03A9F4),       // Ko‘k – transport mavzusi
    "Kiyim-kechak" to Color(0xFFE91E63),    // Pushti – fashion vibe
    "Uy-joy" to Color(0xFFFF9800),          // To‘q sariq – home / warmth
    "Ta'lim" to Color(0xFF3F51B5),          // Moviy – education stability
    "Sog‘liq" to Color(0xFF8BC34A),         // Yengil yashil – health
    "Dam olish" to Color(0xFF00BCD4),       // Ko‘k–yashil – vacation
    "Kommunal to'lov" to Color(0xFF9C27B0), // Binafsha – utilities
    "Internet" to Color(0xFF2196F3),        // Moviy – internet
    "Sport" to Color(0xFFFF5722),           // To‘q qizil–sariq – sport energy
    "Hayriya" to Color(0xFF795548),         // Jigarrang – charity
    "Taxi" to Color(0xFFFFC107),            // Sariq – taxi color

    // INCOME
    "Oylik" to Color(0xFF4CAF50),           // Yashil – salary
    "Investitsiya" to Color(0xFF009688),    // Teal – investment
    "Qo'shimcha" to Color(0xFF8E24AA),      // Purple – extra income
    "Bonus" to Color(0xFFFF7043)            // Soft orange – bonus
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