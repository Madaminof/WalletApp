package dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.data.local.entity.BillItemEntity
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.data.local.entity.ItemAssignmentEntity
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.data.local.entity.ParticipantEntity
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.data.local.relation.BillWithDetails
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.data.local.relation.ItemWithAssignments
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.domain.repository.SplitBillRepository
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.domain.useCases.CalculateSplitUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject
import java.util.Date

@HiltViewModel
class SplitBillViewModel @Inject constructor(
    private val repository: SplitBillRepository,
    private val calculateSplitUseCase: CalculateSplitUseCase,
) : ViewModel() {

    val allBills = repository.getAllBills()

    private val _uiState = MutableStateFlow(SplitBillUiState())
    val uiState: StateFlow<SplitBillUiState> = _uiState.asStateFlow()

    val isFormValid: Boolean
        get() = _uiState.value.let { state ->
            state.bill.title.isNotBlank() &&
                    state.participants.isNotEmpty() &&
                    state.items.isNotEmpty()
        }

    // 1. Ishtirokchi qo'shish
    fun addParticipant(name: String) {
        if (name.isBlank()) return
        val newParticipant = ParticipantEntity(
            billId = _uiState.value.bill.id,
            name = name
        )
        _uiState.update {
            it.copy(
                participants = it.participants + newParticipant
            )
        }
        recalculate()
    }

    // 2. Mahsulot qo'shish
    fun addItem(name: String, price: Double, quantity: Double) {
        val newItem = BillItemEntity(
            billId = _uiState.value.bill.id,
            itemName = name,
            price = price,
            quantity = quantity
        )
        _uiState.update {
            it.copy(
                items = it.items + newItem
            )
        }
        recalculate()
    }

    // 3. Biriktirish (Assignment) - Eng muhim mantiq
    // Agar bor bo'lsa o'chiradi, yo'q bo'lsa qo'shadi (Toggle)
    fun toggleAssignment(itemId: String, participantId: String) {
        val currentAssignments = _uiState.value.assignments.toMutableList()
        val existing =
            currentAssignments.find { it.itemId == itemId && it.participantId == participantId }

        if (existing != null) {
            currentAssignments.remove(existing)
        } else {
            currentAssignments.add(
                ItemAssignmentEntity(
                    itemId = itemId,
                    participantId = participantId
                )
            )
        }

        _uiState.update { it.copy(assignments = currentAssignments) }
        recalculate()
    }

    fun deleteItem(itemId: String) {
        viewModelScope.launch {
            // 1. Avval ushbu mahsulotga tegishli barcha biriktiruvlarni (assignments) o'chiramiz
            val updatedAssignments = _uiState.value.assignments.filterNot { it.itemId == itemId }

            // 2. Mahsulotlar ro'yxatidan ushbu mahsulotni olib tashlaymiz
            val updatedItems = _uiState.value.items.filterNot { it.id == itemId }

            // 3. State-ni yangilaymiz
            _uiState.update { currentState ->
                currentState.copy(
                    items = updatedItems,
                    assignments = updatedAssignments
                )
            }

            // 4. Hisob-kitobni qaytadan amalga oshiramiz (chunki summa o'zgardi)
            recalculate()

            // 5. Agar bazaga (Room/Firebase) ulangan bo'lsangiz, u yerda ham o'chirish:
            // repository.deleteItem(itemId)
        }
    }


    // 4. Hisob-kitobni yangilash
    private fun recalculate() {
        val state = _uiState.value
        // Relation ko'rinishidagi obyektni vaqtincha yasaymiz (UseCase uchun)
        val billWithDetails = BillWithDetails(
            bill = state.bill,
            participants = state.participants,
            items = state.items.map { item ->
                ItemWithAssignments(
                    item = item,
                    assignments = state.assignments.filter { it.itemId == item.id }
                )
            }
        )

        val summary = calculateSplitUseCase(billWithDetails)
        _uiState.update { it.copy(billSummary = summary) }
    }

    // 5. Bazaga saqlash
    fun saveBill(onSuccess: () -> Unit) { // onSuccess qo'shildi
        val state = _uiState.value
        if (state.bill.title.isBlank() || state.items.isEmpty() || state.participants.isEmpty()) {
            _uiState.update { it.copy(error = "Ma'lumotlar to'liq emas") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val result = repository.saveBill(
                state.bill,
                state.participants,
                state.items,
                state.assignments
            )

            result.onSuccess {
                _uiState.update { it.copy(isLoading = false) }
                onSuccess() // FAQAT saqlangandan keyin chaqiriladi
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun loadBillDetails(billId: String?) {
        if (billId == null) return

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                // Repository-dan Flow-ni olamiz
                repository.getBillWithDetails(billId).collect { detailedBill ->
                    // Agar detailedBill null bo'lsa (topilmasa), state-ni yangilamaymiz
                    if (detailedBill != null) {
                        _uiState.update { currentState ->
                            currentState.copy(
                                bill = detailedBill.bill, // Mana endi topishi kerak
                                participants = detailedBill.participants,

                                // items: List<ItemWithAssignments> dan itemlarni ajratamiz
                                items = detailedBill.items.map { it.item },

                                // assignments: Har bir item ichidagi listlarni bitta listga jamlaymiz
                                assignments = detailedBill.items.flatMap { it.assignments },

                                isLoading = false,
                                error = null
                            )
                        }
                        // Ma'lumotlar kelganidan keyin hisob-kitobni yangilaymiz
                        recalculate()
                    } else {
                        _uiState.update { it.copy(isLoading = false, error = "Hisob topilmadi") }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun updateBillTitle(newTitle: String) {
        _uiState.update { it.copy(bill = it.bill.copy(title = newTitle)) }
    }

    fun removeParticipant(participantId: String) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(
                    // 1. Ishtirokchini o'chirish
                    participants = currentState.participants.filterNot { it.id == participantId },
                    // 2. Ushbu ishtirokchiga tegishli barcha biriktiruvlarni (assignments) tozalash
                    assignments = currentState.assignments.filterNot { it.participantId == participantId }
                )
            }
            // 3. Hisob-kitobni yangilash
            recalculate()
        }
    }


    fun deleteBill(billId: String) {
        viewModelScope.launch {
            repository.deleteBill(billId) // Repository orqali bazadan o'chirish
        }
    }



    // Xizmat haqi foizini yangilash
    fun updateServiceCharge(percent: String) {
        val value = percent.toDoubleOrNull() ?: 0.0
        _uiState.update { it.copy(bill = it.bill.copy(serviceChargePercent = value)) }
        recalculate() // Har safar o'zgarganda qayta hisoblaymiz
    }

    // Soliq foizini yangilash
    fun updateTax(percent: String) {
        val value = percent.toDoubleOrNull() ?: 0.0
        _uiState.update { it.copy(bill = it.bill.copy(taxPercent = value)) }
        recalculate()
    }

    // Chegirma summasini yangilash
    fun updateDiscount(amount: String) {
        val value = amount.toDoubleOrNull() ?: 0.0
        _uiState.update { it.copy(bill = it.bill.copy(discountAmount = value)) }
        recalculate()
    }


    fun getShareSummaryText(): String {
        val state = _uiState.value ?: return ""
        // summary va bill'ni xavfsiz olish
        val summary = state.billSummary ?: return "Ma'lumot topilmadi"
        val billDate = state.bill?.date ?: System.currentTimeMillis()

        val sb = StringBuilder()
        val appLink = "https://play.google.com/store/apps/details?id=dev.samandar.walletapp"
        val locale = Locale.US // Bir xillik uchun

        try {
            // 1. Sarlavha
            val title = summary.title.ifBlank { "HISOB-FAKTURA" }.uppercase()
            sb.append("┃ $title\n")

            val dateString = try {
                SimpleDateFormat("dd.MM.yyyy • HH:mm", Locale.getDefault()).format(Date(billDate))
            } catch (e: Exception) { "" }
            sb.append("┃ $dateString\n")
            sb.append("⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯\n")

            // 3. Taqsimot
            sb.append("TO'LOV TAQSIMOTI:\n\n")
            summary.individualResults.forEach { result ->
                val safeAmount = if (result.totalToPay.isFinite()) result.totalToPay else 0.0
                val amountFormatted = String.format(locale, "%,.0f", safeAmount)

                // Ismni xavfsiz formatlash (null bo'lsa "Mehmon", uzun bo'lsa kesish)
                val rawName = result.participantName ?: "Ismsiz"
                val safeName = if (rawName.length > 14) rawName.take(11) + ".." else rawName

                val line = String.format(locale, "%-14s %10s", safeName, amountFormatted)
                sb.append("$line so'm\n")
            }
            sb.append("\n⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯\n")

            // 2. Hisob tafsilotlari
            val subTotal = summary.individualResults.sumOf { if (it.itemsSum.isFinite()) it.itemsSum else 0.0 }
            sb.append("Kichik jami: ${String.format(locale, "%,.0f", subTotal)} so'm\n")

            if (summary.serviceChargePercent > 0) {
                val totalService = summary.individualResults.sumOf { if (it.serviceCharge.isFinite()) it.serviceCharge else 0.0 }
                sb.append("Xizmat (${summary.serviceChargePercent.toInt()}%): ${String.format(locale, "%,.0f", totalService)} so'm\n")
            }

            if (summary.taxPercent > 0) {
                val totalTax = summary.individualResults.sumOf { if (it.tax.isFinite()) it.tax else 0.0 }
                sb.append("Soliq (${summary.taxPercent.toInt()}%): ${String.format(locale, "%,.0f", totalTax)} so'm\n")
            }
            sb.append("⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯\n")

            // 4. Yakuniy Jami
            val safeTotal = if (summary.totalAmount.isFinite()) summary.totalAmount else 0.0
            val totalFormatted = String.format(locale, "%,.0f", safeTotal)
            sb.append("JAMI TO'LOV: $totalFormatted so'm\n\n")

            sb.append("📱 Wallet Analyst — Moliyaviy yordamchingiz.\n\n")
            sb.append("Yuklab olish: $appLink")

        } catch (e: Exception) {
            return "Hisobni tayyorlashda xatolik yuz berdi."
        }

        return sb.toString()
    }
}