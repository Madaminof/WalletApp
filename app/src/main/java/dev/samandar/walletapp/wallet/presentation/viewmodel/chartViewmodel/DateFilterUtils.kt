package dev.samandar.walletapp.wallet.presentation.viewmodel.chartViewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import dev.samandar.walletapp.R
import dev.samandar.walletapp.wallet.domain.model.Transaction
import java.time.Instant
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

    // 1. Joriy davr uchun filtr
    @RequiresApi(Build.VERSION_CODES.O)
    fun filterByTime(transactions: List<Transaction>, filter: TimeFilter): List<Transaction> {
        if (filter == TimeFilter.ALL) return transactions

        val now = LocalDate.now()
        return transactions.filter { tx ->
            val txDate = Instant.ofEpochMilli(tx.date).atZone(ZoneId.systemDefault()).toLocalDate()
            when (filter) {
                TimeFilter.DAILY -> txDate.isEqual(now)
                TimeFilter.WEEKLY -> isSameWeek(txDate, now)
                TimeFilter.MONTHLY -> txDate.year == now.year && txDate.month == now.month
                TimeFilter.YEARLY -> txDate.year == now.year
                TimeFilter.ALL -> true
            }
        }
    }

    // Yordamchi funksiyalar: Hafta hisob-kitobi
    @RequiresApi(Build.VERSION_CODES.O)
    private fun isSameWeek(date1: LocalDate, date2: LocalDate): Boolean {
        // Bir xil yil va bir xil hafta ekanini tekshirish
        val week1 = date1.get(java.time.temporal.IsoFields.WEEK_OF_WEEK_BASED_YEAR)
        val year1 = date1.get(java.time.temporal.IsoFields.WEEK_BASED_YEAR)
        val week2 = date2.get(java.time.temporal.IsoFields.WEEK_OF_WEEK_BASED_YEAR)
        val year2 = date2.get(java.time.temporal.IsoFields.WEEK_BASED_YEAR)
        return week1 == week2 && year1 == year2
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun isSamePreviousWeek(date: LocalDate, now: LocalDate): Boolean {
        val previousWeekDate = now.minusWeeks(1)
        return isSameWeek(date, previousWeekDate)
    }
}