package com.example.walletapp.wallet.presentation.ui.charts.expenseListComponents

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight


@Composable
fun DeleteConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirmDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Tranzaksiyani o'chirish", fontWeight = FontWeight.Bold)
        },
        containerColor = MaterialTheme.colorScheme.onBackground,
        titleContentColor = MaterialTheme.colorScheme.onTertiary,
        textContentColor = MaterialTheme.colorScheme.onTertiary.copy(0.7f),
        text = {
            Text("Haqiqatan ham ushbu tranzaksiyani o'chirmoqchimisiz? Bu amalni qaytarib bo'lmaydi.")
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmDelete()
                    onDismiss()
                }
            ) {
                Text(
                    "O'CHIRISH",
                    color = Color(0xFFD32F2F),
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("BEKOR QILISH", color = Color.Gray)
            }
        }
    )
}