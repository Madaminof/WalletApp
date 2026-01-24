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
    // Soft & Warm tones
    "Groceries" to Color(0xFFF3A683),       // Soft Orange
    "Dining out" to Color(0xFFF78FB3),      // Pinkish
    "Drinks & Coffee" to Color(0xFFCF6A87), // Muted Rose
    "Restaurant" to Color(0xFFE77F67),      // Terracotta

    // --- 2. HOME & LIVING (Uy va Yashash) ---
    // Calm Blues & Indigos
    "Housing" to Color(0xFF546DE5),         // Royal Blue
    "Utilities" to Color(0xFF778BEB),       // Soft Blue
    "Rent" to Color(0xFF6379EE),            // Indigo
    "Maintenance" to Color(0xFF596275),     // Steel Blue

    // --- 3. TRANSPORT & AUTO ---
    "Transport" to Color(0xFF574B90),       // Deep Purple (Bus/Metro)
    "Taxi" to Color(0xFFF19066),            // Soft Salmon (Icon color)
    "Fuel" to Color(0xFFE66767),             // Muted Red
    "Car Maintenance" to Color(0xFF303952), // Dark Navy

    // --- 4. LIFESTYLE & LEISURE ---
    "Entertainment" to Color(0xFFC44569),   // Deep Pink
    "Subscriptions" to Color(0xFF786FA6),   // Muted Purple
    "Self-Care" to Color(0xFFFDA7DF),       // Candy Pink
    "Hobbies" to Color(0xFF577EAA),         // Soft Greyish Blue

    // --- 5. EDUCATION & GROWTH ---
    "Education" to Color(0xFF546DE5),       // Ocean Blue
    "Books" to Color(0xFFF5CD79),           // Sand Yellow
    "Courses" to Color(0xFF45AAF2),          // Sky Blue
    "Certifications" to Color(0xFFF7D794),  // Cream Yellow

    // --- 6. SHOPPING ---
    "Shopping" to Color(0xFFEA8685),        // Coral
    "Clothing" to Color(0xFFF8A5C2),        // Soft Magenta
    "Electronics" to Color(0xFF3DC1D3),     // Cyan
    "Home Appliances" to Color(0xFF596275), // Steel Blue

    // --- 7. HEALTH & WELLNESS ---
    "Health" to Color(0xFFE77F67),          // Apple Red
    "Fitness" to Color(0xFF6379EE),         // Violet
    "Pharmacy" to Color(0xFFF19066),        // Orange Pastel
    "Dental" to Color(0xFF7D5FFF),          // Neon Purple

    // --- 8. FINANCIAL & OTHER ---
    "Investment" to Color(0xFF20BF6B),      // Jade Green
    "Debt & Loans" to Color(0xFFCF6A87),    // Old Rose
    "Gifts & Donation" to Color(0xFFF78FB3), // Flamingo Pink
    "Other" to Color(0xFF596275),           // Charcoal
    "Lent" to Color(0xFFE57373),            // Soft Red (Outgoing)
    "Borrowed" to Color(0xFF81C784),        // Soft Green (Incoming)
    "Debt Payment" to Color(0xFF4CAF50),    // Success Green

    // --- 9. INCOME (Daromadlar) ---
    // Fresh & Wealthy Greens
    "Main Salary" to Color(0xFF26DE81),     // Vibrant Green
    "Side Job" to Color(0xFF20BF6B),        // Mountain Meadow
    "Business" to Color(0xFF05C46B),        // Emerald
    "Freelance" to Color(0xFF8BC34A),       // Light Green
    "Passive Income" to Color(0xFF00D2D3),  // Cyan
    "Dividends" to Color(0xFF32FF7E), // Neon Green
    "Bonus" to Color(0xFFB3AF3E),           // Bright Yellow
    "Cashback" to Color(0xFF18DCFF),        // Electric Blue
    "Grants/Scholarship" to Color(0xFF7D5FFF), // Purple (Achievement)
    "Gifts" to Color(0xFF4FC3F7),           // Sky Blue
    "Other Income" to Color(0xFFA5B1C2)     // Muted Blue Grey
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