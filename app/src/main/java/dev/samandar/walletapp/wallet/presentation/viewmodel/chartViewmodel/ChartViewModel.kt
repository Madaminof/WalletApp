package dev.samandar.walletapp.wallet.presentation.viewmodel.chartViewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.samandar.walletapp.wallet.domain.model.TransactionType
import dev.samandar.walletapp.wallet.domain.usecase.transaction.GetAllTransactions
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.samandar.walletapp.R
import dev.samandar.walletapp.wallet.data.local.dao.TransactionDao
import dev.samandar.walletapp.wallet.data.local.entity.TransactionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import kotlin.random.Random

data class CategoryData(
    val categoryName: String,
    val amount: Double,
    val color: Color,
    val iconResId: Int? = null
)
val categoryIcons = mapOf(
    "Groceries" to R.drawable.groceries_ic2,
    "Dining out" to R.drawable.dining_out_ic2,
    "Drinks & Coffee" to R.drawable.transport_ic2,
    "Restaurant" to R.drawable.restourant_ic2,
    "Housing" to R.drawable.housing_ic2,
    "Utilities" to R.drawable.utilities_ic2,
    "Rent" to R.drawable.rent_ic2,
    "Home Appliances" to R.drawable.home_appliance_ic2,
    "Transport" to R.drawable.transport_ic2,
    "Taxi" to R.drawable.taxi_ic2,
    "Fuel" to R.drawable.fuel_ic2,
    "Car Maintenance" to R.drawable.car_service_ic2,
    "Entertainment" to R.drawable.entertainment_ic2,
    "Subscriptions" to R.drawable.subscriptions_ic2,
    "Self-Care" to R.drawable.self_care_ic2,
    "Hobbies" to R.drawable.hobbies_ic2,
    "Education" to R.drawable.education_ic2,
    "Books" to R.drawable.books_ic2,
    "Courses" to R.drawable.courses_ic2,
    "Certifications" to R.drawable.certificate_ic2,
    "Shopping" to R.drawable.shopping_ic2,
    "Clothing" to R.drawable.clothing_ic2,
    "Electronics" to R.drawable.electronics_ic2,
    "Health" to R.drawable.health_ic2,
    "Pharmacy" to R.drawable.pharmacy_ic2,
    "Dental" to R.drawable.dental_ic2,
    "Investment" to R.drawable.investment_ic2,
    "Debt & Loans" to R.drawable.debt_ic2,
    "Gifts & Donation" to R.drawable.gifts_ic2,
    "Other" to R.drawable.other_ic2,
    "Lent" to R.drawable.ic_debt,
    "Borrowed" to R.drawable.ic_debt,
    "Debt Payment" to R.drawable.debts_icons,
    "Main Salary" to R.drawable.salary_ic2,
    "Side Job" to R.drawable.side_job_ic2,
    "Business" to R.drawable.business_ic2,
    "Passive Income" to R.drawable.passive_income_ic2,
    "Dividends" to R.drawable.dividends_ic2,
    "Bonus" to R.drawable.bonus_ic2,
    "Cashback" to R.drawable.cashback_ic2,
    "Grants" to R.drawable.grants_ic2,
    "Gifts" to R.drawable.gifts_ic2,
    "Other Income" to R.drawable.other_ic2,
    )
fun getCategoryIcon(categoryName: String): Int {
    return categoryIcons[categoryName] ?: R.drawable.card_default_icon // Default ikonka
}


val categoryColors = mapOf(
    // --- 1. FOOD & DRINK (Ishtaha ochar va iliq ohanglar) ---
    "Groceries" to Color(0xFF098B7C),       // Sky Blue
    "Dining out" to Color(0xFFD24D47),      // Sunset Orange
    "Drinks & Coffee" to Color(0xFFC19238), // Golden Sand
    "Restaurant" to Color(0xFFC2425D),      // Rose Pink

    // --- 2. HOME & LIVING (Xotirjamlik va barqarorlik) ---
    "Housing" to Color(0xFF494993),         // Deep Indigo
    "Utilities" to Color(0xFF3F44D4),       // Electric Blue
    "Rent" to Color(0xFF196DCD),            // Soft Blue
    "Maintenance" to Color(0xFF04919E),     // Steel Grey

    // --- 3. TRANSPORT & AUTO (Harakat va tezlik) ---
    "Transport" to Color(0xFF619E36),       // Emerald Green
    "Taxi" to Color(0xFFAAAA29),            // Taxi Yellow
    "Fuel" to Color(0xFFAA4039),            // Red Orange
    "Car Maintenance" to Color(0xFF2B6A9A), // Dark Charcoal

    // --- 4. LIFESTYLE & LEISURE (Kayfiyat va quvonch) ---
    "Entertainment" to Color(0xFF924180),   // Plum
    "Subscriptions" to Color(0xFFB12C6B),   // Magenta
    "Self-Care" to Color(0xFFAF53A6),       // Candy Pink
    "Hobbies" to Color(0xFF20839C),         // Teal

    // --- 5. EDUCATION & GROWTH (Bilim va intellekt) ---
    "Education" to Color(0xFFAA813F),       // Deep Pine
    "Books" to Color(0xFFEA8530),           // Ochre
    "Courses" to Color(0xFF20AFAA),         // Turquoise
    "Certifications" to Color(0xFFCB5B33),  // Deep Orange

    // --- 6. SHOPPING (Trend va yangilik) ---
    "Shopping" to Color(0xFFAC2861),        // Soft Purple
    "Clothing" to Color(0xFFB3495B),        // Crimson
    "Electronics" to Color(0xFF4774A1),     // Tech Blue
    "Home Appliances" to Color(0xFF4958BA), // Steel Blue

    // --- 7. HEALTH & WELLNESS (Hayotiy energiya) ---
    "Health" to Color(0xFFCD242A),          // Medical Red
    "Fitness" to Color(0xFF0652DD),         // Azure Blue
    "Pharmacy" to Color(0xFFBA6A47),        // Peach
    "Dental" to Color(0xFF5656AA),          // Soft Violet

    // --- 8. FINANCIAL & OTHER (Mas'uliyat va hisob-kitob) ---
    "Investment" to Color(0xFF0CA03E),      // Forest Green
    "Debt & Loans" to Color(0xFF3C4082),    // Deep Navy
    "Gifts & Donation" to Color(0xFFEB4D4B), // Soft Red
    "Other" to Color(0xFF566E7D),           // Silver Grey
    "Lent" to Color(0xFFFF5E57),            // Outgoing Red (Pul chiqishi)
    "Borrowed" to Color(0xFF2CA568),        // Incoming Green (Pul kelishi)
    "Debt Payment" to Color(0xFF197F4F),    // Success Green

    // --- 9. INCOME (Daromadlar - Doim "Boylik" ramzi bo'lgan yashil-ko'k ohangda) ---
    "Main Salary" to Color(0xFF2A8655),     // Jade Green
    "Side Job" to Color(0xFF2EC176),        // Dragon Green
    "Business" to Color(0xFF53A530),        // Emerald
    "Freelance" to Color(0xFF208269),       // Dark Teal
    "Passive Income" to Color(0xFF298E89),  // Turquoise
    "Dividends" to Color(0xFF1B9CFC),       // High Blue
    "Bonus" to Color(0xFFA38B2B),           // Bright Gold
    "Cashback" to Color(0xFF3481B1),        // Sky Blue
    "Grants" to Color(0xFF5E3E74), // Royal Purple
    "Gifts" to Color(0xFF419AAD),           // Neon Blue
    "Other Income" to Color(0xFF496DBF)      // Modern Blue
)



fun getCategoryColor(categoryName: String): Color {
    return categoryColors[categoryName] ?: Color.Gray
}


@HiltViewModel
class ChartViewModel @Inject constructor(
    private val getAllTransactions: GetAllTransactions,
) : ViewModel() {

    // 1. Ma'lumotlarni bitta UI State ichida birlashtiramiz
    data class ChartUiState(
        val categoryData: List<CategoryData> = emptyList(),
        val totalAmount: Double = 0.0,
        val isLoading: Boolean = true
    )

    // 2. transactionsFlow'dan kelgan ma'lumotni bir marta qayta ishlaymiz
    val chartUiState: StateFlow<ChartUiState> = getAllTransactions(type = null)
        .map { transactions ->
            // Faqat xarajatlarni filter qilamiz
            val expenses = transactions.filter { it.type == TransactionType.EXPENSE }

            // Umumiy summani hisoblaymiz
            val total = expenses.sumOf { it.amount }

            // Guruhlash va CategoryData yaratish
            val grouped = expenses
                .groupBy { it.category?.name }
                .map { (name, list) ->
                    val catName = name ?: "Other"
                    CategoryData(
                        categoryName = catName,
                        amount = list.sumOf { it.amount },
                        color = getCategoryColor(catName),
                        iconResId = getCategoryIcon(catName) // Ikonkani ham qo'shdik
                    )
                }
                .filter { it.amount > 0.0 }
                .sortedByDescending { it.amount }

            ChartUiState(
                categoryData = grouped,
                totalAmount = total,
                isLoading = false
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ChartUiState()
        )


}