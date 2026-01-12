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
                Triple("Cash", 0.0, "#FF26A69A") to R.drawable.money,
                Triple("Card", 0.0, "#FF424666") to R.drawable.credit_card
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
                // --- 1. FOOD & DRINK (Ovqatlanish) ---
                // Emerald/Teal - barqaror ehtiyoj
                Triple("Groceries", "EXPENSE", R.drawable.grocery) to 0xFF27AE60,
                Triple("Dining out", "EXPENSE", R.drawable.dinning_out) to 0xFF2ECC71,
                Triple("Drinks & Coffee", "EXPENSE", R.drawable.ic_coffee) to 0xFF1ABC9C,
                Triple("Restaurant", "EXPENSE", R.drawable.ic_restaurant) to 0xFF16A085,

                // --- 2. HOME & LIVING (Uy va Yashash) ---
                // Blue/Indigo - barqarorlik va xavfsizlik
                Triple("Housing", "EXPENSE", R.drawable.ic_home) to 0xFF2980B9,
                Triple("Utilities", "EXPENSE", R.drawable.ic_bolt) to 0xFF3498DB,
                Triple("Maintenance", "EXPENSE", R.drawable.maintenance) to 0xFF5758BB,
                Triple("Rent", "EXPENSE", R.drawable.ic_apartment) to 0xFF4b7bec,

                // --- 3. LIFESTYLE & LEISURE (Hayot tarzi va Hordiq) ---
                // Purple/Pink - o'yin-kulgi va dam olish
                Triple("Entertainment", "EXPENSE", R.drawable.ic_gaming) to 0xFF8E44AD,
                Triple("Subscriptions", "EXPENSE", R.drawable.ic_subscriptions) to 0xFF9B59B6,
                Triple("Self-Care", "EXPENSE", R.drawable.ic_spa) to 0xFFD980FA,
                Triple("Hobbies", "EXPENSE", R.drawable.ic_palette) to 0xFFFDA7DF, // Yangi qo'shildi

                // --- 4. EDUCATION & GROWTH (Ta'lim va O'sish) ---
                // Yellow/Amber - diqqat va intellekt
                Triple("Education", "EXPENSE", R.drawable.ic_school) to 0xFFF1C40F,
                Triple("Books", "EXPENSE", R.drawable.ic_menu_book) to 0xFFF39C12,
                Triple("Courses", "EXPENSE", R.drawable.ic_laptop_mac) to 0xFFE67E22,
                Triple("Certifications", "EXPENSE", R.drawable.ic_verified) to 0xFFD35400,

                // --- 5. SHOPPING (Xaridlar) ---
                // Orange/Coral - quvonch va energiya
                Triple("Shopping", "EXPENSE", R.drawable.ic_shopping_bag) to 0xFFE17055,
                Triple("Clothing", "EXPENSE", R.drawable.ic_checkroom) to 0xFFFAB1A0,
                Triple("Electronics", "EXPENSE", R.drawable.ic_devices) to 0xFFFF7675,
                Triple("Home Appliances", "EXPENSE", R.drawable.ic_kitchen) to 0xFFEE5253,

                // --- 6. HEALTH & WELLNESS (Salomatlik) ---
                // Red/Pink - hayot va parvarish
                Triple("Health", "EXPENSE", R.drawable.medical_team) to 0xFFEA2027,
                Triple("Fitness", "EXPENSE", R.drawable.ic_fitness_center) to 0xFFFF4D4D,
                Triple("Pharmacy", "EXPENSE", R.drawable.ic_medication) to 0xFFFF5E78,
                Triple("Dental", "EXPENSE", R.drawable.ic_dentistry) to 0xFFFF7979,

                // --- 7. FINANCIAL & OTHER (Moliyaviy va Boshqalar) ---
                // Deep Teal/Forest - Investitsiya (O'sish va boylik)
                Triple("Investment", "EXPENSE", R.drawable.ic_show_chart) to 0xFF10AC84,
                Triple("Debt & Loans", "EXPENSE", R.drawable.debt_loang) to 0xFFEE5253,
                Triple("Gifts & Donation", "EXPENSE", R.drawable.ic_favorite) to 0xFFF368E0,
                Triple("Other", "EXPENSE", R.drawable.ic_other) to 0xFF51B0E0,
                Triple("Lent", "EXPENSE", R.drawable.debt_icon) to 0xFFE57373,
                Triple("Debt Payment", "EXPENSE", R.drawable.ic_debt) to 0xFF4CAF50,

                // --- 8. INCOME (Daromadlar) ---
                // Deep Greens - Barqaror va asosiy daromadlar
                Triple("Main Salary", "INCOME", R.drawable.ic_salary_payments) to 0xFF1B5E20,
                Triple("Side Job", "INCOME", R.drawable.ic_work_outline) to 0xFF2E7D32, // Ikkinchi ish uchun

                // Vibrant Greens - Biznes va tadbirkorlik
                Triple("Business", "INCOME", R.drawable.ic_business) to 0xFF27AE60,
                Triple("Freelance", "INCOME", R.drawable.ic_laptop_mac) to 0xFF8BC34A,

                // Teal & Cyan - Passiv va moliyaviy daromadlar
                Triple("Passive Income", "INCOME", R.drawable.passive_income) to 0xFF009688,
                Triple("Dividends/Interest", "INCOME", R.drawable.ic_treding_up) to 0xFF00ACC1, // Aksiyalar yoki depozit foizlari

                // Gold & Azure - Bonuslar va kutilmagan tushumlar
                Triple("Bonus", "INCOME", R.drawable.ic_stars) to 0xFFFBC02D, // Mukofot pullari
                Triple("Cashback", "INCOME", R.drawable.ic_account_balance_wallet) to 0xFF0097A7, // Xaridlardan qaytgan pul
                Triple("Grants/Scholarship", "INCOME", R.drawable.ic_school) to 0xFF43A047, // Grant yoki stipendiya

                // Light Blue & Grey - Boshqa tushumlar
                Triple("Gifts", "INCOME", R.drawable.gift_card) to 0xFF4FC3F7,
                Triple("Other Income", "INCOME", R.drawable.other_income) to 0xFF78909C,
                Triple("Borrowed", "INCOME", R.drawable.debt_icon) to 0xFF81C784,
                Triple("Debt Payment", "INCOME", R.drawable.ic_debt) to 0xFF4CAF50


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