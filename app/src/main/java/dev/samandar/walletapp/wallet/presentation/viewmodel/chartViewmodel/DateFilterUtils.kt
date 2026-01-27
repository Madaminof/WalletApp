package dev.samandar.walletapp.wallet.presentation.viewmodel.chartViewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import dev.samandar.walletapp.R
import dev.samandar.walletapp.wallet.domain.model.Transaction
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import java.util.Calendar
import java.util.Date

enum class TimeFilter(val titleResId: Int) {
    DAILY(R.string.filter_day),
    WEEKLY(R.string.filter_week),
    MONTHLY(R.string.filter_month),
    YEARLY(R.string.filter_year),
    ALL(R.string.filter_all)
}

object DateFilterUtils {
    fun filterByTime(transactions: List<Transaction>, filter: TimeFilter): List<Transaction> {
        val now = Calendar.getInstance()
        return transactions.filter { transaction ->
            val txDate = Calendar.getInstance().apply {
                // Agar date Long bo'lsa:
                time = Date(transaction.date)
                // Agar date Date obyekt bo'lsa, shunchaki: time = transaction.date
            }
            when (filter) {
                TimeFilter.DAILY -> isSameDay(txDate, now)
                TimeFilter.WEEKLY -> isSameWeek(txDate, now)
                TimeFilter.MONTHLY -> isSameMonth(txDate, now)
                TimeFilter.YEARLY -> isSameYear(txDate, now)
                TimeFilter.ALL -> true
            }
        }
    }

    private fun isSameDay(c1: Calendar, c2: Calendar) =
        c1[Calendar.YEAR] == c2[Calendar.YEAR] &&
                c1[Calendar.DAY_OF_YEAR] == c2[Calendar.DAY_OF_YEAR]

    private fun isSameWeek(c1: Calendar, c2: Calendar) =
        c1[Calendar.YEAR] == c2[Calendar.YEAR] &&
                c1[Calendar.WEEK_OF_YEAR] == c2[Calendar.WEEK_OF_YEAR]

    private fun isSameMonth(c1: Calendar, c2: Calendar) =
        c1[Calendar.YEAR] == c2[Calendar.YEAR] &&
                c1[Calendar.MONTH] == c2[Calendar.MONTH]

    private fun isSameYear(c1: Calendar, c2: Calendar) =
        c1[Calendar.YEAR] == c2[Calendar.YEAR]
}