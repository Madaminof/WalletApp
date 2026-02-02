package dev.samandar.walletapp.wallet.presentation.ui.charts.historyTransactions

import java.util.Calendar

enum class TransactionPeriod {
   MONTHLY, YEARLY, ALL
}

object DateFilterUtils {
    fun isSameMonth(timestamp: Long): Boolean {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply { timeInMillis = timestamp }
        return now.get(Calendar.YEAR) == target.get(Calendar.YEAR) &&
                now.get(Calendar.MONTH) == target.get(Calendar.MONTH)
    }

    fun isSameYear(timestamp: Long): Boolean {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply { timeInMillis = timestamp }
        return now.get(Calendar.YEAR) == target.get(Calendar.YEAR)
    }
}