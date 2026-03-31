package dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.domain.useCases

import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.data.local.relation.BillWithDetails
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.domain.model.BillSummary
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.domain.model.SplitResult
import javax.inject.Inject

class CalculateSplitUseCase @Inject constructor() {

    operator fun invoke(billWithDetails: BillWithDetails): BillSummary {
        val bill = billWithDetails.bill
        val participantSums = mutableMapOf<String, Double>()

        // 1. Har bir ishtirokchi uchun ovqatlar summasini (base sum) hisoblash
        billWithDetails.items.forEach { itemWithAssignments ->
            val item = itemWithAssignments.item
            val assignments = itemWithAssignments.assignments

            if (assignments.isNotEmpty()) {
                val totalShares = assignments.sumOf { it.share }
                val itemTotalPrice = item.price * item.quantity

                // Har bir ulush narxi
                val pricePerShare = if (totalShares > 0) itemTotalPrice / totalShares else 0.0

                assignments.forEach { assignment ->
                    val currentSum = participantSums.getOrDefault(assignment.participantId, 0.0)
                    participantSums[assignment.participantId] = currentSum + (pricePerShare * assignment.share)
                }
            }
        }

        // 2. Umumiy ovqatlar yig'indisi (Soliq va xizmat haqisiz)
        val subTotal = participantSums.values.sum()

        // 3. Har bir kishi uchun qo'shimcha xarajatlarni proporsional taqsimlash
        val individualResults = billWithDetails.participants.map { participant ->
            val itemsSum = participantSums.getOrDefault(participant.id, 0.0)

            // Ishtirokchining umumiy ovqatlar ichidagi ulushi (koeffitsiyent)
            val ratio = if (subTotal > 0) itemsSum / subTotal else 0.0

            // Xizmat haqi, soliq va chegirmalarni proporsional bo'lish
            val service = (subTotal * (bill.serviceChargePercent / 100)) * ratio
            val tax = (subTotal * (bill.taxPercent / 100)) * ratio
            val discount = bill.discountAmount * ratio

            SplitResult(
                participantId = participant.id,
                participantName = participant.name,
                itemsSum = itemsSum,
                serviceCharge = service,
                tax = tax,
                discount = discount,
                // Formula: Ovqat + Servis + Soliq - Chegirma
                totalToPay = (itemsSum + service + tax) - discount
            )
        }

        return BillSummary(
            billId = bill.id,
            title = bill.title,
            // Yakuniy summa hamma to'lashi kerak bo'lgan summalar yig'indisiga teng
            totalAmount = individualResults.sumOf { it.totalToPay },
            individualResults = individualResults,
            serviceChargePercent = bill.serviceChargePercent,
            taxPercent = bill.taxPercent,
        )
    }
}