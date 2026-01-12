package dev.samandar.walletapp.wallet.domain.model.debt

enum class DebtType { LENT, BORROWED }

data class Debt(
    val id: String,
    val personName: String,
    val totalAmount: Double,      // Umumiy qarz summasi
    val remainingAmount: Double,  // Qolgan summa (to'lovlar chegirilgan holda)
    val type: DebtType,
    val startDate: Long,
    val dueDate: Long?,           // Qaytarish muddati (ixtiyoriy)
    val createdAt: Long = System.currentTimeMillis(),
    val isSettled: Boolean,       // To'liq yopilganmi?
    val description: String? = null,
    val colorArgb: Int? = null,    // UI uchun rang (ixtiyoriy)
    val accountId: String
)
data class DebtTransaction(
    val id: String,
    val debtId: String,       // Qaysi qarzga tegishli ekanligi
    val amount: Double,       // To'langan summa
    val date: Long,           // To'lov qilingan vaqt
    val note: String? = null, // To'lov haqida eslatma (masalan: "Yarmidan ko'pi berildi")
    val accountId: String? = null // Qaysi hamyondan pul chiqdi yoki kirdi (Balans integratsiyasi uchun)
)