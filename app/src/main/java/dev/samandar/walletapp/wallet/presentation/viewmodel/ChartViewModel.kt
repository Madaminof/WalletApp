package dev.samandar.walletapp.wallet.presentation.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.samandar.walletapp.wallet.domain.model.TransactionType
import dev.samandar.walletapp.wallet.domain.usecase.transaction.GetAllTransactions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class CategoryData(
    val categoryName: String,
    val amount: Double,
    val color: Color
)


val categoryColors = mapOf(
    // --- 1. FOOD & DRINK (Ovqatlanish) ---
    "Groceries" to Color(0xFF27AE60),
    "Dining out" to Color(0xFF2ECC71),
    "Drinks & Coffee" to Color(0xFF1ABC9C),
    "Restaurant" to Color(0xFF16A085),

    // --- 2. HOME & LIVING (Uy va Yashash) ---
    "Housing" to Color(0xFF2980B9),
    "Utilities" to Color(0xFF3498DB),
    "Maintenance" to Color(0xFF5758BB),
    "Rent" to Color(0xFF4B7BEC),

    // --- 3. LIFESTYLE & LEISURE (Hayot tarzi) ---
    "Entertainment" to Color(0xFF8E44AD),
    "Subscriptions" to Color(0xFF9B59B6),
    "Self-Care" to Color(0xFFD980FA),
    "Hobbies" to Color(0xFFFDA7DF),

    // --- 4. EDUCATION & GROWTH (Ta'lim) ---
    "Education" to Color(0xFFF1C40F),
    "Books" to Color(0xFFF39C12),
    "Courses" to Color(0xFFE67E22),
    "Certifications" to Color(0xFFD35400),

    // --- 5. SHOPPING (Xaridlar) ---
    "Shopping" to Color(0xFFE17055),
    "Clothing" to Color(0xFFFAB1A0),
    "Electronics" to Color(0xFFFF7675),
    "Home Appliances" to Color(0xFFEE5253),

    // --- 6. HEALTH & WELLNESS (Salomatlik) ---
    "Health" to Color(0xFFEA2027),
    "Fitness" to Color(0xFFFF4D4D),
    "Pharmacy" to Color(0xFFFF5E78),
    "Dental" to Color(0xFFFF7979),

    "Investment" to Color(0xFF10AC84),
    "Debt & Loans" to Color(0xFFEE5253),
    "Gifts & Donation" to Color(0xFFF368E0),
    "Other" to Color(0xFF51B0E0),

    // --- 8. INCOME (Daromadlar) ---
    "Main Salary" to Color(0xFF1B5E20),
    "Side Job" to Color(0xFF2E7D32),
    "Business" to Color(0xFF27AE60),
    "Freelance" to Color(0xFF8BC34A),
    "Passive Income" to Color(0xFF009688),
    "Dividends/Interest" to Color(0xFF00ACC1),
    "Bonus" to Color(0xFFFBC02D),
    "Cashback" to Color(0xFF0097A7),
    "Grants/Scholarship" to Color(0xFF43A047),
    "Gifts" to Color(0xFF4FC3F7),
    "Other Income" to Color(0xFF78909C),

    "Lent" to Color(0xFFE57373),
    "Borrowed" to Color(0xFF81C784),
    "Debt Payment" to Color(0xFF4CAF50)
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
                .groupBy { it.category?.name }
                .map { (categoryName, list) ->
                    val totalAmount = list.sumOf { it.amount }
                    CategoryData(
                        categoryName = categoryName?:"",
                        amount = totalAmount,
                        color = getCategoryColor(categoryName?:"")
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