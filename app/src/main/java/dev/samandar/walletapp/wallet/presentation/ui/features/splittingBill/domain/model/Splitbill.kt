package dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.domain.model

// Ma'lumotlarni hisoblash natijasi uchun model

// Domain model (sof Kotlin)
data class SplitBill(
    val id: String,
    val title: String,
    val date: Long,
    val serviceChargePercent: Double,
    val taxPercent: Double,
    val discountAmount: Double,
    val totalAmount: Double,
    val currency: String,
    val participants: List<Participant>,
    val items: List<SplitBillItem>
)

data class Participant(
    val id: String,
    val name: String
)

data class SplitBillItem(
    val id: String,
    val name: String,
    val price: Double,
    val quantity: Double,
    val assignments: List<ItemAssignment>
)

data class ItemAssignment(
    val participantId: String,
    val share: Double
)


data class SplitResult(
    val participantId: String,
    val participantName: String,
    val itemsSum: Double,      // Faqat yegan ovqatlari
    val serviceCharge: Double,  // Ulushiga tushgan xizmat haqi
    val tax: Double,           // Ulushiga tushgan soliq
    val discount: Double,      // Ulushiga tushgan chegirma
    val totalToPay: Double     // Yakuniy to'lov (Sum + Service + Tax - Discount)
)

data class BillSummary(
    val billId: String,
    val title: String,
    val totalAmount: Double,   // Chekning yakuniy summasi
    val individualResults: List<SplitResult>,
    val serviceChargePercent: Double, // SplitBill dan olinadi
    val taxPercent: Double,           // SplitBill dan olinadi
)