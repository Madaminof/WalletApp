package dev.samandar.walletapp.wallet.presentation.ui.budjets

import java.util.Calendar

object BudgetDateUtils {
    // Oyning birinchi kunini (millisekundda) qaytaradi
    fun getStartOfMonth(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    // Oyning oxirgi kunini (millisekundda) qaytaradi
    fun getEndOfMonth(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
    }

    // Qolgan kunlarni hisoblash
    fun getDaysRemaining(endDate: Long): Int {
        val diff = endDate - System.currentTimeMillis()
        return if (diff > 0) (diff / (1000 * 60 * 60 * 24)).toInt() + 1 else 0
    }

    fun getStartOfCurrentWeek(): Long {
        val calendar = Calendar.getInstance()
        calendar.firstDayOfWeek = Calendar.MONDAY
        // Bugungi kunni hisobga olgan holda dushanbaga qaytamiz
        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            calendar.add(Calendar.DAY_OF_YEAR, -1)
        }
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    fun getEndOfCurrentWeek(): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = getStartOfCurrentWeek() // Dushanbani asos qilib olamiz
        calendar.add(Calendar.DAY_OF_YEAR, 6) // +6 kun = Yakshanba
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
    }
}