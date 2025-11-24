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
                Triple("Cash", 0.0, "#1976D2") to R.drawable.ic_cash,
                Triple("Card", 0.0, "#0F9915") to R.drawable.ic_card
            )

            defaultAccounts.forEach { (triple, icon) ->
                val (name, balance, colorHex) = triple
                val escapedName = name.replace("'", "''")
                db.execSQL(
                    "INSERT INTO accounts (id, name, balance, colorHex, iconResId) " +
                            "VALUES ('${UUID.randomUUID()}', '$escapedName', $balance, '$colorHex', $icon)"
                )
            }

            val defaultCategories = listOf(
                Triple("Oziq-ovqat", "EXPENSE", R.drawable.ic_food),
                Triple("Transport", "EXPENSE", R.drawable.ic_transport),
                Triple("Kiyim-kechak", "EXPENSE", R.drawable.ic_clothes),
                Triple("Uy-joy", "EXPENSE", R.drawable.ic_home),
                Triple("Ta'lim", "EXPENSE", R.drawable.ic_education),
                Triple("Sog‘liq", "EXPENSE", R.drawable.ic_health),
                Triple("Dam olish", "EXPENSE", R.drawable.ic_fun),
                Triple("Kommunal to'lov", "EXPENSE", R.drawable.ic_electricity),
                Triple("Internet", "EXPENSE", R.drawable.ic_internet),
                Triple("Sport", "EXPENSE", R.drawable.ic_sport),
                Triple("Hayriya", "EXPENSE", R.drawable.ic_charity),
                Triple("Taxi", "EXPENSE", R.drawable.ic_car),

                Triple("Oylik", "INCOME", R.drawable.ic_salary),
                Triple("Investitsiya", "INCOME", R.drawable.ic_invest),
                Triple("Qo'shimcha", "INCOME", R.drawable.ic_extra_income),
                Triple("Bonus", "INCOME", R.drawable.ic_bonus)
            )
            defaultCategories.forEach { (name, type, icon) ->
                val escapedName = name.replace("'", "''")
                db.execSQL(
                    "INSERT INTO categories (id, name, type, iconResId) VALUES ('${UUID.randomUUID()}', '$escapedName', '$type', $icon)"
                )
            }
        }
    }
}
