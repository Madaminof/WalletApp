package dev.samandar.walletapp.wallet.smartScannQR

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.samandar.walletapp.R
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import dev.samandar.walletapp.wallet.domain.model.smartScannModel.Receipt
import dev.samandar.walletapp.wallet.domain.model.smartScannModel.ReceiptItem
import dev.samandar.walletapp.wallet.smartScannOCR.OcrAnalyzer
import dev.samandar.walletapp.wallet.smartScannOCR.ocrParser.SmartReceiptParser
import dev.samandar.walletapp.wallet.smartScannQR.soliqParser.SoliqParser
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.UUID

@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val soliqParser: SoliqParser,
    private val ocrAnalyzer: OcrAnalyzer,
    private val smartParser: SmartReceiptParser
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


    fun onImageSelected(uri: Uri,defaultItemName:String) {
        // 1. Double-click va parallel jarayonlarni oldini olish
        if (_uiState.value.isProcessing) return

        viewModelScope.launch {
            try {
                // UI-ni yuklanish holatiga o'tkazish
                _uiState.update { it.copy(isScanning = false, isProcessing = true) }

                // 2. OCR orqali matnni olish
                val rawText = ocrAnalyzer.extractTextFromImage(uri)

                // 🔥 DEBUG LOG: OCR nima o'qiganini ko'rish uchun
                Log.d("SmartScanDebug", "================= OCR RAW START =================")
                Log.d("SmartScanDebug", rawText)
                Log.d("SmartScanDebug", "================== OCR RAW END ==================")

                if (rawText.isBlank()) {
                    Log.e("SmartScanDebug", "OCR natijasi bo'sh!")
                    handleScanError("Rasmdan matn o'qib bo'lmadi")
                    return@launch
                }

                val parsedResult = smartParser.parseOcrText(
                    rawText,
                    defaultItemName = defaultItemName
                )

                // 🔥 DEBUG LOG: Parser natijasini ko'rish
                Log.d("SmartScanDebug", "PARSED -> Shop: ${parsedResult.merchantName}, Sum: ${parsedResult.totalAmount}")

                if (parsedResult.totalAmount > 0) {
                    // 4. Domain modelga o'tkazish va UI-ni yangilash
                    val domainReceipt = mapOcrToDomainReceipt(parsedResult)

                    _uiState.update {
                        it.copy(
                            scanResult = domainReceipt,
                            isProcessing = false
                        )
                    }
                } else {
                    Log.w("SmartScanDebug", "Parser summa topa olmadi!")
                    handleScanError("Chekda summa topilmadi. Iltimos, aniqroq rasmga oling.")
                }
            } catch (e: Exception) {
                Log.e("SmartScanDebug", "Skanerlashda xatolik: ${e.message}", e)
                handleScanError("Rasmni tahlil qilishda xatolik yuz berdi")
            }
        }
    }

    private fun mapOcrToDomainReceipt(parsed: ParsedReceipt): Receipt {
        // Parserdan kelgan ParsedReceipt-ni sening Domain modelindagi Receipt-ga o'tkazamiz
        return Receipt(
            id = UUID.randomUUID().toString(),
            originalUrl = "",
            transactionId = UUID.randomUUID().toString(),
            merchantName = parsed.merchantName,
            merchantAddress = parsed.merchantAddress,
            totalAmount = parsed.totalAmount,
            date = parsed.date,
            paymentMethod = parsed.paymentMethod, // "KARTA" yoki "NAQD"
            rawText = parsed.rawText,
            fiscalSign = "",
            taxAmount = 0.0,
            items = parsed.items.map { it.toDomainItem() },
            note = "📷 Tasvirdan skanerlandi: ${parsed.merchantAddress}"
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
