package dev.samandar.walletapp.wallet.smartScann

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class ReceiptAnalyzer(
    private val onTextDetected: (String) -> Unit,
    private val onQrDetected: (String) -> Unit
) : ImageAnalysis.Analyzer {

    private val barcodeScanner = BarcodeScanning.getClient(
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
    )
    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    private var isProcessing = false

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image

        if (isProcessing || mediaImage == null) {
            imageProxy.close()
            return
        }

        isProcessing = true
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        barcodeScanner.process(image)
            .addOnSuccessListener { barcodes ->
                val qrCode = barcodes.firstOrNull()?.rawValue
                if (!qrCode.isNullOrBlank()) {
                    onQrDetected(qrCode)
                    imageProxy.close()
                    isProcessing = false
                } else {
                    analyzeText(image, imageProxy)
                }
            }
            .addOnFailureListener {
                imageProxy.close()
                isProcessing = false
            }
    }

    private fun analyzeText(image: InputImage, imageProxy: ImageProxy) {
        textRecognizer.process(image)
            .addOnSuccessListener { visionText ->
                val text = visionText.text
                if (text.isNotBlank() && text.length > 50) {
                    onTextDetected(text)
                }
            }
            .addOnCompleteListener {
                imageProxy.close()
                isProcessing = false
            }
    }
}