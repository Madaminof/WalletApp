package dev.samandar.walletapp.wallet.smartScann

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import dev.samandar.walletapp.wallet.domain.model.smartScannModel.Receipt
import dev.samandar.walletapp.wallet.domain.model.smartScannModel.ReceiptItem
import dev.samandar.walletapp.wallet.smartScann.soliqParser.SoliqParser
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.UUID

@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val soliqParser: SoliqParser
) : ViewModel() {

    private val TAG = "SmartScanDebug"

    private val _uiState = MutableStateFlow(ScannerUiState())
    val uiState = _uiState.asStateFlow()

    @Volatile
    private var isScanned = false

    fun onQrDetected(qrContent: String) {
        if (isScanned || !qrContent.contains("soliq.uz")) return

        isScanned = true

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isScanning = false, isProcessing = true) }

                val result = soliqParser.parseFromUrl(qrContent)

                if (result != null && result.totalAmount > 0) {
                    _uiState.update {
                        it.copy(
                            scanResult = mapToDomainReceipt(result, qrContent),
                            isProcessing = false
                        )
                    }
                } else {
                    handleScanError("Ma'lumot topilmadi")
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Skanerlashda xatolik: ${e.message}")
                handleScanError("Internet aloqasini tekshiring")
            }
        }
    }

    private fun handleScanError(message: String) {
        _uiState.update { it.copy(isProcessing = false, isScanning = true) }
        isScanned = false
    }

    fun startScanning() {
        isScanned = false
        _uiState.update {
            it.copy(
                isScanning = true,
                isProcessing = false,
                scanResult = null
            )
        }
    }


    private fun mapToDomainReceipt(parsed: ParsedReceipt, url: String): Receipt {
        val itemsSummary = parsed.items.joinToString(separator = "\n") { item ->
            "${item.name}: ${item.quantity.toInt()} x ${String.format(Locale.US, "%,.0f", item.price)} = ${String.format(Locale.US, "%,.0f", item.price * item.quantity)}"
        }

        val finalNote = if (itemsSummary.isNotEmpty()) {
            "🛒 Mahsulotlar ro'yxati:\n$itemsSummary"
        } else {
            "Soliq cheki skanerlandi"
        }

        return Receipt(
            id = UUID.randomUUID().toString(),
            originalUrl = url,
            transactionId = UUID.randomUUID().toString(),
            merchantName = parsed.merchantName ?: "Noma'lum do'kon",
            merchantAddress = parsed.merchantAddress ?: "",
            totalAmount = parsed.totalAmount,
            date = parsed.date,
            paymentMethod = parsed.paymentMethod,
            rawText = parsed.rawText,
            fiscalSign = "",
            taxAmount = 0.0,
            items = parsed.items.map { it.toDomainItem() },
            note = finalNote
        )
    }

    private fun ParsedItem.toDomainItem() = ReceiptItem(
        id = UUID.randomUUID().toString(),
        name = this.name,
        quantity = this.quantity,
        unitPrice = this.price,
        totalPrice = this.price * this.quantity,
        categoryId = null
    )



}

data class ScannerUiState(
    val scanResult: Receipt? = null,
    val isProcessing: Boolean = false,
    val isScanning: Boolean = true
)
