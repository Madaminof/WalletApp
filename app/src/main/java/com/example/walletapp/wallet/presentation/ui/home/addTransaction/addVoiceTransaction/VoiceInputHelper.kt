package com.example.walletapp.wallet.presentation.ui.home.addTransaction.addVoiceTransaction

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

class VoiceInputHelper(private val activity: ComponentActivity) {

    // Natijani qaytaruvchi funksiya
    var onResult: ((String?) -> Unit)? = null

    // Activity natijasini kutish uchun launcher
    private val speechLauncher: ActivityResultLauncher<Intent> =
        activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == -1) {
                val matches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                // Eng yaxshi natijani qaytarish
                onResult?.invoke(matches?.get(0))
            } else {
                onResult?.invoke(null) // Xatolik yoki bekor qilinsa
            }
        }

    fun startListening(resultCallback: (String?) -> Unit) {
        this.onResult = resultCallback

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "uz-UZ")
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Tranzaksiya ma'lumotlarini ayting...")
        }
        speechLauncher.launch(intent)
    }
}

fun Context.findActivity(): ComponentActivity? = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@Composable
fun rememberVoiceInputHelper(): VoiceInputHelper {
    val context = LocalContext.current
    // ✅ Eng to'g'ri yondashuv
    val activity = remember(context) { context.findActivity() as? ComponentActivity
        ?: throw IllegalStateException("Composable must be hosted in an Activity") }
    return remember { VoiceInputHelper(activity) }
}