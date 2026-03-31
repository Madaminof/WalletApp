package dev.samandar.walletapp.wallet.presentation.ui.otherScreens.exportData.exportImpl.excel

import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi

object ExcelTheme {
    @RequiresApi(Build.VERSION_CODES.O)
    val PRIMARY_COLOR = Color.valueOf(0xFF1A73E8.toInt()) // Google Blue kabi
    @RequiresApi(Build.VERSION_CODES.O)
    val INCOME_COLOR = Color.valueOf(0xFF32D74B.toInt())
    @RequiresApi(Build.VERSION_CODES.O)
    val EXPENSE_COLOR = Color.valueOf(0xFFFF453A.toInt())
    @RequiresApi(Build.VERSION_CODES.O)
    val HEADER_BG = Color.valueOf(0xFFF1F3F4.toInt())
}