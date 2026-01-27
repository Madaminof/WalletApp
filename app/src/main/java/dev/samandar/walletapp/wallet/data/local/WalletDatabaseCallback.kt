package dev.samandar.walletapp.wallet.data.local

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import dev.samandar.walletapp.R
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WalletDatabaseCallback @Inject constructor() : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        db.beginTransaction()
        try {
            val defaultAccounts = listOf(
                Triple("Cash", 0.0, "#009688") to R.drawable.cash_icon2,      // Zumrad yashil
                Triple("Card", 0.0, "#2980B9") to R.drawable.card_default_icon // Premium ko'k
            )

            defaultAccounts.forEach { (triple, icon) ->
                val (name, balance, colorHex) = triple
                val escapedName = name.replace("'", "''")
                db.execSQL(
                    "INSERT INTO accounts (id, name, balance, colorHex, iconResId) " +
                            "VALUES ('${UUID.randomUUID()}', '$escapedName', $balance, '$colorHex', $icon)"
                )
            }

            val categoryData = listOf(
                // --- 1. FOOD & DRINK ---
                Triple("Groceries", "EXPENSE", R.drawable.groceries_ic2) to 0xFF098B7C,
                Triple("Dining out", "EXPENSE", R.drawable.dining_out_ic2) to 0xFFD24D47,
                Triple("Drinks & Coffee", "EXPENSE", R.drawable.drink_ic2) to 0xFFC19238,
                Triple("Restaurant", "EXPENSE", R.drawable.restourant_ic2) to 0xFFC2425D,

                // --- 2. HOME & LIVING ---
                Triple("Housing", "EXPENSE", R.drawable.housing_ic2) to 0xFF494993,
                Triple("Utilities", "EXPENSE", R.drawable.utilities_ic2) to 0xFF3F44D4,
                Triple("Rent", "EXPENSE", R.drawable.rent_ic2) to 0xFF196DCD,
                Triple("Home Appliances", "EXPENSE", R.drawable.home_appliance_ic2) to 0xFF4958BA,

                // --- 3. TRANSPORT & AUTO ---
                Triple("Transport", "EXPENSE", R.drawable.transport_ic2) to 0xFF619E36,
                Triple("Taxi", "EXPENSE", R.drawable.taxi_ic2) to 0xFFAAAA29,
                Triple("Fuel", "EXPENSE", R.drawable.fuel_ic2) to 0xFFAA4039,
                Triple("Car Maintenance", "EXPENSE", R.drawable.car_service_ic2) to 0xFF2B6A9A,

                // --- 4. LIFESTYLE & LEISURE ---
                Triple("Entertainment", "EXPENSE", R.drawable.entertainment_ic2) to 0xFF924180,
                Triple("Subscriptions", "EXPENSE", R.drawable.subscriptions_ic2) to 0xFFB12C6B,
                Triple("Self-Care", "EXPENSE", R.drawable.self_care_ic2) to 0xFFAF53A6,
                Triple("Hobbies", "EXPENSE", R.drawable.hobbies_ic2) to 0xFF20839C,

                // --- 5. EDUCATION & GROWTH ---
                Triple("Education", "EXPENSE", R.drawable.education_ic2) to 0xFFAA813F,
                Triple("Books", "EXPENSE", R.drawable.books_ic2) to 0xFFEA8530,
                Triple("Courses", "EXPENSE", R.drawable.courses_ic2) to 0xFF20AFAA,
                Triple("Certifications", "EXPENSE", R.drawable.certificate_ic2) to 0xFFCB5B33,

                // --- 6. SHOPPING ---
                Triple("Shopping", "EXPENSE", R.drawable.shopping_ic2) to 0xFFAC2861,
                Triple("Clothing", "EXPENSE", R.drawable.clothing_ic2) to 0xFFB3495B,
                Triple("Electronics", "EXPENSE", R.drawable.electronics_ic2) to 0xFF4774A1,
                Triple("Investment", "EXPENSE", R.drawable.investment_ic2) to 0xFF0CA03E,

                // --- 7. HEALTH & WELLNESS ---
                Triple("Health", "EXPENSE", R.drawable.health_ic2) to 0xFFCD242A,
                Triple("Fitness", "EXPENSE", R.drawable.fitness_ic2) to 0xFF0652DD,
                Triple("Pharmacy", "EXPENSE", R.drawable.pharmacy_ic2) to 0xFFBA6A47,
                Triple("Dental", "EXPENSE", R.drawable.dental_ic2) to 0xFF5656AA,

                // --- 8. FINANCIAL & OTHER ---
                Triple("Debt & Loans", "EXPENSE", R.drawable.debt_ic2) to 0xFF3C4082,
                Triple("Gifts & Donation", "EXPENSE", R.drawable.gifts_ic2) to 0xFFEB4D4B,
                Triple("Other", "EXPENSE", R.drawable.other_ic2) to 0xFF566E7D,

                // --- 9. INCOME ---
                Triple("Main Salary", "INCOME", R.drawable.salary_ic2) to 0xFF2A8655,
                Triple("Side Job", "INCOME", R.drawable.side_job_ic2) to 0xFF2EC176,
                Triple("Business", "INCOME", R.drawable.business_ic2) to 0xFF53A530,
                Triple("Passive Income", "INCOME", R.drawable.passive_income_ic2) to 0xFF298E89,
                Triple("Dividends", "INCOME", R.drawable.dividends_ic2) to 0xFF1B9CFC,
                Triple("Bonus", "INCOME", R.drawable.bonus_ic2) to 0xFFA38B2B,
                Triple("Cashback", "INCOME", R.drawable.cashback_ic2) to 0xFF3481B1,
                Triple("Grants", "INCOME", R.drawable.grants_ic2) to 0xFF5E3E74,
                Triple("Other Income", "INCOME", R.drawable.other_income_ic2) to 0xFF496DBF
            )

            categoryData.forEach { (data, color) ->
                val (name, type, icon) = data
                val escapedName = name.replace("'", "''")
                db.execSQL(
                    """
                    INSERT INTO categories (id, name, type, iconResId, colorArgb) 
                    VALUES ('${UUID.randomUUID()}', '$escapedName', '$type', $icon, $color)
                    """.trimIndent()
                )
            }

            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }
}