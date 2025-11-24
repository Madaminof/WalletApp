package com.example.walletapp.wallet.presentation.ui.home.addTransaction.addVoiceTransaction

// ... (Boshqa importlar)
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.walletapp.wallet.presentation.viewmodel.AddTransactionViewModel
import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import com.example.walletapp.domain.parser.VoiceInputParser

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

@Composable
fun AddvoiceScreen(
    navController: NavController,
    viewModel: AddTransactionViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState = viewModel.uiState

    val allCategories = uiState.expenseCategories + uiState.incomeCategories
    val allAccounts = uiState.accounts

    // STT Natijasini Qabul Qilish Launcher'i
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val matches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val recognizedText = matches?.get(0)

            if (recognizedText != null) {
                val parsedData = VoiceInputParser.parse(
                    text = recognizedText,
                    allCategories = allCategories,
                    allAccounts = allAccounts
                )
                viewModel.applyParsedData(parsedData)
                Toast.makeText(context, "Tranzaksiya tahlil qilindi: ${parsedData.amount} so'm", Toast.LENGTH_SHORT).show()

            }
        } else if (result.resultCode == Activity.RESULT_CANCELED) {
            // Internet ulanish xatosi yoki foydalanuvchi tomonidan bekor qilish
            Toast.makeText(context, "Ovozli kiritish bekor qilindi yoki xato yuz berdi.", Toast.LENGTH_SHORT).show()
        }
    }

    // Ovozli kiritishni boshlash funksiyasi (Ruxsat berilganda chaqiriladi)
    val startVoiceInput: () -> Unit = {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "uz-UZ")
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Tranzaksiya ma'lumotlarini ayting...")
        }
        try {
            speechLauncher.launch(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Qurilmada ovozli kiritish xizmati topilmadi.", Toast.LENGTH_LONG).show()
        }
    }

    // Mikrofon Ruxsatini So'rash Launcher'i
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            startVoiceInput() // Ruxsat berilgach, asosiy funksiyani chaqirish
        } else {
            Toast.makeText(context, "Mikrofon ruxsatisiz ovozli kiritish ishlamaydi.", Toast.LENGTH_LONG).show()
        }
    }

    // Tugmani bosganda ishga tushiruvchi funksiya
    val onMicClick: () -> Unit = {
        when {
            // 1. Ruxsat berilgan bo'lsa
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED -> {
                startVoiceInput()
            }
            // 2. Ruxsat so'rash kerak bo'lsa
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }


    Scaffold(
        topBar = { /* TopBar komponentingiz shu yerda */ }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.surface)
        ) {

            FloatingActionButton(
                onClick = onMicClick,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(end = 16.dp, bottom = 16.dp),
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
            ) {
                Icon(Icons.Default.Mic, contentDescription = "Ovozli kiritish")
            }
        }
    }
}