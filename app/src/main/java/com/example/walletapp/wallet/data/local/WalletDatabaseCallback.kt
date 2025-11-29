package com.example.walletapp.wallet.data.local

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.walletapp.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WalletDatabaseCallback @Inject constructor(
    private val applicationScope: CoroutineScope
) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        applicationScope.launch(Dispatchers.IO) {

            val defaultAccounts = listOf(
                Triple("Naqd pul", 0.0, "#1976D2") to R.drawable.ic_naqd_pul,
                Triple("Karta", 0.0, "#0F9915") to R.drawable.ic_card_default
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
                Triple("Oziq-ovqat", "EXPENSE", R.drawable.ic_food) to 0xFFE53935,          // To'q Qizil
                Triple("Restoran/Kafe", "EXPENSE", R.drawable.ic_restaurant) to 0xFFEF5350,   // Ochiq Qizil
                Triple("Uy-joy", "EXPENSE", R.drawable.ic_apartment) to 0xFF43A047,    // To'q Yashil
                Triple("Kommunal", "EXPENSE", R.drawable.ic_lightbulb) to 0xFF00ACC1, // Ko'k-yashil (Cyan)
                Triple("Kiyim-kechak", "EXPENSE", R.drawable.ic_clothes) to 0xFF1E88E5,      // Moviy
                Triple("Sog‘liq/Dori", "EXPENSE", R.drawable.ic_health) to 0xFFD81B60,       // To'q Pushti
                Triple("Ta'lim/Kurslar", "EXPENSE", R.drawable.ic_education) to 0xFFFDD835,     // Yorqin Sariq
                Triple("Internet/TV", "EXPENSE", R.drawable.ic_wifi_new) to 0xFF546E7A,         // Kulrang-Moviy
                Triple("Telefon balansi", "EXPENSE", R.drawable.ic_phone) to 0xFF8D6E63,       // Jigarrang
                Triple("Shaxsiy Xaridlar", "EXPENSE", R.drawable.ic_shopping_bag) to 0xFF673AB7, // To'q Binafsha
                Triple("Dam olish/O'yin", "EXPENSE", R.drawable.ic_gamepad) to 0xFF66BB6A,     // O'rta Yashil
                Triple("Sug'urta to'lovi", "EXPENSE", R.drawable.ic_insurance) to 0xFF03A9F4,   // Osmon Moviy
                Triple("Uy hayvonlari", "EXPENSE", R.drawable.ic_pet) to 0xFF7CB342,          // Yashil-Jigarrang
                Triple("Transport", "EXPENSE", R.drawable.ic_bus) to 0xFFFF7043, // Yorqin To'q Sariq
                Triple("Avto yoqilg'i", "EXPENSE", R.drawable.ic_gas_station) to 0xFFFB8C00, // To'q Sariq
                Triple("Boshqa Xarajat", "EXPENSE", R.drawable.ic_default_expense) to 0xFF9E9E9E, // O'rta Kulrang
                Triple("Abonent/Obuna", "EXPENSE", R.drawable.ic_subscription) to 0xFFFBC02D, // O'rta Sariq (Netflix, Spotify)
                Triple("Qarzni to'lash", "EXPENSE", R.drawable.ic_debt) to 0xFFB71C1C,        // Eng To'q Qizil
                Triple("Avto xizmat", "EXPENSE", R.drawable.ic_car_service) to 0xFF4DD0E1, // Ochiq Cyan
                Triple("Bog'chas", "EXPENSE", R.drawable.ic_kindergarten) to 0xFF8E24AA, // To'q Binafsha
                Triple("Bank", "EXPENSE", R.drawable.ic_bank_fee) to 0xFF78909C,  // Kulrang-Moviy (Yengilroq)
                Triple("ta'mirlash", "EXPENSE", R.drawable.ic_home_repair) to 0xFF689F38,  // To'q Yashil
                Triple("Tozalash", "EXPENSE", R.drawable.ic_cleaning_new) to 0xFF07F6E0,
                Triple("Jarima/Soliq", "EXPENSE", R.drawable.ic_tax) to 0xFFC2185B,         // To'q Pushti
                Triple("Kredit to'lovi", "EXPENSE", R.drawable.ic_credit_card) to 0xFF3949AB, // Indigo

                Triple("Oylik Maosh", "INCOME", R.drawable.ic_salary) to 0xFF4CAF50,          // Yashil
                Triple("Qo'shimcha", "INCOME", R.drawable.ic_extra_income) to 0xFF8BC34A, // Och Yashil
                Triple("Investitsiya", "INCOME", R.drawable.ic_trending_up) to 0xFF00BFA5, // Feruza (Teal)
                Triple("Bonuslar", "INCOME", R.drawable.ic_bonus) to 0xFFFFC107,              // Sariq
                Triple("Sovg'a/Yutuq", "INCOME", R.drawable.ic_gift) to 0xFF5C6BC0,           // Indigo
                Triple("Daromad", "INCOME", R.drawable.ic_default_income) to 0xFF4FC3F7 // Ochiq Moviy
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


        }
    }
}
