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
                Triple("Cash", 0.0, "#A3CB38") to R.drawable.cash_default_ic,
                Triple("Card", 0.0, "#12CBC4") to R.drawable.card_ic
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
                // Ikonalarda pushti va och yashil ustunlik qiladi
                Triple("Groceries", "EXPENSE", R.drawable.groceries_ic) to 0xFFF3A683, // Sabzavotlar rangiga mos (Soft Orange)
                Triple("Dining out", "EXPENSE", R.drawable.dining_out_ic) to 0xFFF78FB3, // Idish-tovoq detallari uchun (Pink)
                Triple("Drinks & Coffee", "EXPENSE", R.drawable.drink_ic) to 0xFFCF6A87, // Shisha va ichimlik (Deep Rose)
                Triple("Restaurant", "EXPENSE", R.drawable.restourant_ic) to 0xFFE77F67, // Ovqatlanish (Terracotta)

                // --- 2. HOME & LIVING (Uy va Yashash) ---
                // Moviy va binafsha ohanglar
                Triple("Housing", "EXPENSE", R.drawable.housing_ic) to 0xFF546DE5, // Uy (Royal Blue)
                Triple("Utilities", "EXPENSE", R.drawable.utilities_ic) to 0xFF778BEB, // Kommunal (Soft Blue)
                Triple("Rent", "EXPENSE", R.drawable.rent_ic) to 0xFF6379EE, // Ijara (Indigo)
                Triple("Home Appliances", "EXPENSE", R.drawable.home_appliance_ic) to 0xFF596275, // Texnika (Steel Blue)

                // --- 3. TRANSPORT & AUTO ---
                // Ikonadagi detallarga ko'ra ranglar
                Triple("Transport", "EXPENSE", R.drawable.transport_ic) to 0xFF574B90, // Avtobus (Deep Purple)
                Triple("Taxi", "EXPENSE", R.drawable.taxi_ic) to 0xFFF19066, // Sening ikonangdagi taksi rangi (Soft Salmon)
                Triple("Fuel", "EXPENSE", R.drawable.fuel_ic) to 0xFFE66767, // Yoqilg'i (Soft Red)
                Triple("Car Maintenance", "EXPENSE", R.drawable.car_service_ic) to 0xFF303952, // Servis (Dark Navy)

                // --- 4. LIFESTYLE & LEISURE ---
                // Quvnoq va yorqin ranglar
                Triple("Entertainment", "EXPENSE", R.drawable.entertainment_ic) to 0xFFC44569, // O'yin-kulgi (Deep Pink)
                Triple("Subscriptions", "EXPENSE", R.drawable.subscriptions_ic) to 0xFF786FA6, // Obunalar (Muted Purple)
                Triple("Self-Care", "EXPENSE", R.drawable.self_care_ic) to 0xFFFDA7DF, // Parvarish (Candy Pink)
                Triple("Hobbies", "EXPENSE", R.drawable.hobbies_ic) to 0xFF577EAA, // Qiziqishlar (Soft Greyish Blue)

                // --- 5. EDUCATION & GROWTH ---
                Triple("Education", "EXPENSE", R.drawable.education_ic) to 0xFF546DE5, // Ta'lim (Ocean Blue)
                Triple("Books", "EXPENSE", R.drawable.books_ic) to 0xFFF5CD79, // Kitoblar (Sand Yellow)
                Triple("Courses", "EXPENSE", R.drawable.course_ic) to 0xFF45AAF2, // Kurslar (Sky Blue)
                Triple("Certifications", "EXPENSE", R.drawable.certificate_ic) to 0xFFF7D794, // Sertifikat (Cream Yellow)

                // --- 6. SHOPPING ---
                Triple("Shopping", "EXPENSE", R.drawable.shopping_ic) to 0xFFEA8685, // Xarid (Coral)
                Triple("Clothing", "EXPENSE", R.drawable.clothing_ic) to 0xFFF8A5C2, // Kiyim (Soft Magenta)
                Triple("Electronics", "EXPENSE", R.drawable.electronic_ic) to 0xFF3DC1D3, // Elektronika (Cyan)
                Triple("Investment", "EXPENSE", R.drawable.investment_ic) to 0xFF20BF6B, // Investitsiya (Jade Green)

                // --- 7. HEALTH & WELLNESS ---
                Triple("Health", "EXPENSE", R.drawable.health_ic) to 0xFFE77F67, // Salomatlik (Apple Red)
                Triple("Fitness", "EXPENSE", R.drawable.fitness_ic) to 0xFF6379EE, // Fitnes (Violet)
                Triple("Pharmacy", "EXPENSE", R.drawable.pharmacy) to 0xFFF19066, // Dorixona (Orange Pastel)
                Triple("Dental", "EXPENSE", R.drawable.dental_ic) to 0xFF7D5FFF, // Stomatolog (Neon Purple)

                // --- 8. FINANCIAL & OTHER ---
                Triple("Debt & Loans", "EXPENSE", R.drawable.debt_ic) to 0xFFCF6A87, // Qarz (Old Rose)
                Triple("Gifts & Donation", "EXPENSE", R.drawable.gifts_ic) to 0xFFF78FB3, // Sovg'a (Flamingo Pink)
                Triple("Other", "EXPENSE", R.drawable.other_ic) to 0xFF596275, // Boshqa (Charcoal)

                // --- 9. INCOME (Daromadlar) ---
                // Yashil va Firuza ohanglar (Boylik ramzi)
                Triple("Main Salary", "INCOME", R.drawable.salary_ic) to 0xFF26DE81, // Oylik (Vibrant Green)
                Triple("Side Job", "INCOME", R.drawable.side_job_ic) to 0xFF20BF6B, // Qo'shimcha ish (Mountain Meadow)
                Triple("Business", "INCOME", R.drawable.business_ic) to 0xFF05C46B, // Biznes (Emerald)
                Triple("Passive Income", "INCOME", R.drawable.passive_income_ic) to 0xFF00D2D3, // Passiv (Cyan)
                Triple("Dividends", "INCOME", R.drawable.divident_income_ic) to 0xFF32FF7E, // Dividend (Neon Green)
                Triple("Bonus", "INCOME", R.drawable.bonus_ic) to 0xFFB3AF3E, // Bonus (Bright Yellow)
                Triple("Cashback", "INCOME", R.drawable.cashback_ic) to 0xFF18DCFF, // Keshbek (Electric Blue)
                Triple("Grants", "INCOME", R.drawable.grant_ic) to 0xFF7D5FFF, // Grant (Purple)
                Triple("Other Income", "INCOME", R.drawable.other_income) to 0xFFA5B1C2 // Boshqa (Muted Blue)
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