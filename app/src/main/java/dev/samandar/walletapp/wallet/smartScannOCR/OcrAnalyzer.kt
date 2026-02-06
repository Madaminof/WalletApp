package dev.samandar.walletapp.wallet.smartScannOCR

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OcrAnalyzer @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // Bizga nafaqat matn, balki har bir blokning joylashuvi ham kerak bo'lishi mumkin
    suspend fun extractTextFromImage(imageUri: Uri): String = withContext(Dispatchers.IO) {
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        return@withContext try {
            val image = InputImage.fromFilePath(context, imageUri)
            val result = recognizer.process(image).await()

            // Bloklarni alohida saqlash mantiqni kuchaytiradi
            result.textBlocks.joinToString("\n---\n") { it.text }
        } catch (e: Exception) {
            ""
        }
    }
}